package com.luccasaps.jpa.validator;

import com.luccasaps.jpa.exceptions.RegistroDuplicadoException;
import com.luccasaps.jpa.model.Autor;
import com.luccasaps.jpa.repository.AutorRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AutorValidator {

    private AutorRepository autorRepository;


    public AutorValidator(AutorRepository autorRepository) {
        this.autorRepository = autorRepository;
    }

    public void validar(Autor autor) {
        if(existeAutorCadastrado(autor)) {
            throw new RegistroDuplicadoException("Autor ja cadastrado");
        }
    }

    private boolean existeAutorCadastrado(Autor autor) {
        Optional<Autor> autorEncontrado = autorRepository.findByNomeAndDataNascimentoAndNacionalidade(
                autor.getNome(),
                autor.getDataNascimento(),
                autor.getNacionalidade()
        );

        if(autor.getId() == null){
            return autorEncontrado.isPresent();
        }

        return !autor.getId().equals(autorEncontrado.get().getId()) && autorEncontrado.isPresent();
    }
}
