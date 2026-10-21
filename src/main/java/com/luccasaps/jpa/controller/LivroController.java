package com.luccasaps.jpa.controller;

import com.luccasaps.jpa.controller.dto.CadastroLivroDTO;
import com.luccasaps.jpa.controller.dto.ResultadoPesquisaLivroDTO;
import com.luccasaps.jpa.controller.mappers.LivroMapper;
import com.luccasaps.jpa.model.GeneroLivro;
import com.luccasaps.jpa.model.Livro;
import com.luccasaps.jpa.service.LivroService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/livros")
@Tag(name = "Livros")
@RequiredArgsConstructor
public class LivroController implements GenericController {

    private final LivroService livroService;
    private final LivroMapper livroMapper;
    private final Executor taskExecutor;

    @PostMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public CompletableFuture<ResponseEntity<Void>> salvar(@RequestBody @Valid CadastroLivroDTO dto) {

        return CompletableFuture.supplyAsync(() -> {
            Livro livro = livroMapper.toEntity(dto);
            livroService.salvar(livro);

            var url = gerarHeaderLocation(livro.getId());

            return ResponseEntity.created(url).build();
        }, taskExecutor);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public CompletableFuture<ResponseEntity<ResultadoPesquisaLivroDTO>> obterDetalhes(@PathVariable String id) {
        return CompletableFuture.supplyAsync(() -> livroService.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    var dto = livroMapper.toDTO(livro);
                    return ResponseEntity.ok(dto);
                }).orElseGet(() -> ResponseEntity.notFound().build()), taskExecutor
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<Object> deletar(@PathVariable String id) {
        return livroService.obterPorId(UUID.fromString(id)).map(livro -> {
            livroService.deletar(livro);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public CompletableFuture<ResponseEntity<Page<ResultadoPesquisaLivroDTO>>> pesquisa(
            @RequestParam(value = "isbn", required = false)
            String isbn,

            @RequestParam(value = "titulo", required = false)
            String titulo,

            @RequestParam(value = "nome-autor", required = false)
            String nomeAutor,

            @RequestParam(value = "genero", required = false)
            GeneroLivro genero,

            @RequestParam(value = "ano-publicacao", required = false)
            Integer anoPublicacao,

            @RequestParam(value = "pagina", defaultValue = "0")
            Integer pagina,

            @RequestParam(value = "tamanho-pagina", defaultValue = "10")
            Integer tamanhoPagina
    ){
        return CompletableFuture.supplyAsync(() -> {
            Page<Livro> paginaResultado = livroService.pesquisa(isbn, titulo, nomeAutor, genero, anoPublicacao, pagina, tamanhoPagina);
            Page<ResultadoPesquisaLivroDTO> resultado = paginaResultado.map(livroMapper::toDTO);
            return ResponseEntity.ok(resultado);
        }, taskExecutor);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public CompletableFuture<ResponseEntity<Object>> atualizar(
            @PathVariable("id") String id,
            @RequestBody @Valid CadastroLivroDTO dto) {

        return CompletableFuture.supplyAsync(() -> {
            return livroService.obterPorId(UUID.fromString(id))
                    .map(livro -> {
                        Livro entidadeaux = livroMapper.toEntity(dto);

                        livro.setDataPublicacao(entidadeaux.getDataPublicacao());
                        livro.setIsbn(entidadeaux.getIsbn());
                        livro.setPreco(entidadeaux.getPreco());
                        livro.setGenero(entidadeaux.getGenero());
                        livro.setTitulo(entidadeaux.getTitulo());
                        livro.setAutor(entidadeaux.getAutor());

                        livroService.atualizar(livro);

                        return ResponseEntity.noContent().build();
                    }).orElseGet(() -> ResponseEntity.notFound().build());
        }, taskExecutor);
    }
}
