package com.luccasaps.jpa.service;

import com.luccasaps.jpa.model.GeneroLivro;
import com.luccasaps.jpa.model.Livro;
import com.luccasaps.jpa.model.Usuario;
import com.luccasaps.jpa.repository.LivroRepository;
import com.luccasaps.jpa.security.SecurityService;
import com.luccasaps.jpa.validator.LivroValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.luccasaps.jpa.repository.specs.LivroSpecs.*;

@Service
@RequiredArgsConstructor
public class LivroService {

    private final LivroRepository livroRepository;
    private final LivroValidator livroValidator;
    private final SecurityService securityService;

    public Livro salvar(Livro livro) {
        livroValidator.validar(livro);
        Usuario usuario = securityService.obterUsuarioLogado();
        livro.setUsuario(usuario);
        return livroRepository.save(livro);
    }

    public Optional<Livro> obterPorId(UUID id) {
        return livroRepository.findById(id);
    }

    public void deletar(Livro livro) {
        livroRepository.delete(livro);
    }

    public Page<Livro> pesquisa(String isbn,
                                String titulo,
                                String nomeAutor,
                                GeneroLivro genero,
                                Integer anoPublicacao,
                                Integer pagina,
                                Integer tamanhoPagina
    ) {

//        Specification<Livro> specs = Specification
//                .where(LivroSpecs.isbnEqual(isbn)
//                .and(LivroSpecs.titulo(titulo)
//                .and(LivroSpecs.generoEqual(genero))))
//                ;


        Specification<Livro> specs = Specification.where((root, query, cb) -> cb.conjunction());

        if(isbn != null) {
            specs = specs.and(isbnEqual(isbn));
        }

        if(titulo != null) {
            specs = specs.and(tituloLike(titulo));
        }

        if(genero != null) {
            specs = specs.and(generoEqual(genero));
        }

        if(anoPublicacao != null) {
            specs = specs.and(anoPublicacaoEqual(anoPublicacao));
        }

        if(nomeAutor != null) {
            specs = specs.and(nomeAutorLike(nomeAutor));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);

        return livroRepository.findAll(specs, pageRequest);
    }

    public void atualizar(Livro livro) {

        if(livro.getTitulo() == null) {
            throw new IllegalArgumentException("Para atualizar é necessario que o livro ja esteja salvo na base");
        }

        livroValidator.validar(livro);
        livroRepository.save(livro);
    }
}
