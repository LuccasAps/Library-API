package com.luccasaps.jpa.service;

import com.luccasaps.jpa.exceptions.OperacaoNaoPermitidaException;
import com.luccasaps.jpa.model.Autor;
import com.luccasaps.jpa.model.Usuario;
import com.luccasaps.jpa.repository.AutorRepository;
import com.luccasaps.jpa.repository.LivroRepository;
import com.luccasaps.jpa.security.SecurityService;
import com.luccasaps.jpa.validator.AutorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository autorRepository;
    private final AutorValidator autorValidator;
    private final LivroRepository livroRepository;
    private final SecurityService securityService;

    public Autor salvar (Autor autor) {
        autorValidator.validar(autor);
        Usuario usuario = securityService.obterUsuarioLogado();
        autor.setUsuario(usuario);
        return autorRepository.save(autor);
    }

    public void atualizar(Autor autor) {
        if(autor.getId() == null){
            throw new IllegalArgumentException("Para atualizar é necessario que o autor ja esteja salvo na base");
        }
        autorValidator.validar(autor);
        autorRepository.save(autor);
    }

    public Optional<Autor> obterPorId(UUID id) {
        return autorRepository.findById(id);
    }

    public void excluir(Autor autor) {
        if(possuiLivro(autor)){
            throw new OperacaoNaoPermitidaException("Não é permitido excluir um Autor possui livros cadastrados");
        }
        autorRepository.delete(autor);
    }

    public List<Autor> pesquisa(String nome,String nacionalidade) {

        if(nome != null && nacionalidade != null) {
            return autorRepository.findByNomeAndNacionalidade(nome, nacionalidade);
        }
        if(nome != null) {
            return autorRepository.findByNome(nome);
        }
        if(nacionalidade != null) {
            return autorRepository.findByNacionalidade(nacionalidade);
        }

        return autorRepository.findAll();
    }

    public List<Autor> pesquisaByExample(String nome,String nacionalidade){
        var autor = new Autor();
        autor.setNome(nome);
        autor.setNacionalidade(nacionalidade);

        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnorePaths("id", "dataNascimento", "dataCadastro")
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Autor> autorExample =  Example.of(autor, matcher);

        return autorRepository.findAll(autorExample);
    }

    public boolean possuiLivro(Autor autor){
        return livroRepository.existsByAutor(autor);
    }
}
