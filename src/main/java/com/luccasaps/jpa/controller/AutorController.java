package com.luccasaps.jpa.controller;

import com.luccasaps.jpa.controller.dto.AutorDTO;
import com.luccasaps.jpa.controller.mappers.AutorMapper;
import com.luccasaps.jpa.model.Autor;
import com.luccasaps.jpa.service.AutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.CodePointLength;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/autores")
/**
 http://host:8080/autores
 */
@RequiredArgsConstructor
@Tag(name = "Autores")
@Slf4j
public class AutorController implements GenericController {

    private final AutorService autorService;
    private final AutorMapper autorMapper;
    private final Executor taskExecutor;

    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(
            summary = "Salvar",
            description = "Cadastrar novo autor"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cadastrado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Erro de validação"),
            @ApiResponse(responseCode = "409", description = "Autor já cadastrado")
    })
    // ^ mesma coisa @RequestMapping(method = RequestMethod.POST)
    public CompletableFuture<ResponseEntity<Void>> salvar(@RequestBody @Valid AutorDTO dto) {

        return CompletableFuture.supplyAsync(() -> {
            log.info("Cadastrando novo autor: {}", dto.nome());

            Autor autor = autorMapper.toEntity(dto);
            autorService.salvar(autor);
            URI location = gerarHeaderLocation(autor.getId());

            return ResponseEntity.created(location).build();
        }, taskExecutor);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(
            summary = "obter Detalhes",
            description = "Retornar os dados do autor via ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autor encontrado"),
            @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    })
    public CompletableFuture<ResponseEntity<AutorDTO>> obterDetalhes(@PathVariable String id) {
        return CompletableFuture.supplyAsync(() -> {
            var idAutor = UUID.fromString(id);
            Optional<Autor> autorOptional = autorService.obterPorId(idAutor);
            return autorService
                    .obterPorId(idAutor)
                    .map(autor -> {
                        AutorDTO dto = autorMapper.toDTO(autor);
                        return ResponseEntity.ok(dto);
                    }).orElseGet(() -> ResponseEntity.notFound().build());
        }, taskExecutor);
    }

    //indenpontente
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(
            summary = "Deletar",
            description = "Deleta um autor existente via ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Autor não encontrado"),
            @ApiResponse(responseCode = "400", description = "Autor possui livros cadastrados")
    })
    public CompletableFuture<ResponseEntity<Void>> excluir(@PathVariable("id") String id) {

        return CompletableFuture.supplyAsync(() -> {
            log.info("Deletando autor: {}", id);

            var idAutor = UUID.fromString(id);
            Optional<Autor> autorOptional = autorService.obterPorId(idAutor);

            if (autorOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            autorService.excluir(autorOptional.get());
            return ResponseEntity.noContent().build();
        }, taskExecutor);

    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    @Operation(
            summary = "Pesquisar",
            description = "Realiza pesquisa de autores por parametros"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso")
    })
    public CompletableFuture<ResponseEntity<List<AutorDTO>>> pesquisar(
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "nacionalidade", required = false) String nacionalidade) {

        return CompletableFuture.supplyAsync(() -> {

            List<Autor> resultado = autorService.pesquisaByExample(nome, nacionalidade);
            List<AutorDTO> lista = resultado
                    .stream()
                    .map(autorMapper::toDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(lista);
        }, taskExecutor);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GERENTE')")
    @Operation(
            summary = "Atualizar",
            description = "Atualiza os dados de um autor existente"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Autor não encontrado")
    })
    public CompletableFuture<ResponseEntity<Void>> atualizar(@PathVariable("id") String id,
                                            @RequestBody @Valid AutorDTO dto) {

        return CompletableFuture.supplyAsync(() -> {
            var idAutor = UUID.fromString(id);
            Optional<Autor> autorOptional = autorService.obterPorId(idAutor);

            if (autorOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            var autor = autorOptional.get();
            autor.setNome(dto.nome());
            autor.setNacionalidade(dto.nacionalidade());
            autor.setDataNascimento(dto.dataNascimento());

            autorService.salvar(autor);

            return ResponseEntity.noContent().build();
        }, taskExecutor);
    }
}
