package com.luccasaps.jpa.repository;

import com.luccasaps.jpa.model.Autor;
import com.luccasaps.jpa.model.GeneroLivro;
import com.luccasaps.jpa.model.Livro;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@SpringBootTest
public class LivroRepositoryTest {

    @Autowired
    LivroRepository repository;

    @Autowired
    AutorRepository autorRepository;

    @Test
    void salvarTest(){
        Livro livro = new Livro();
        livro.setIsbn("90887-84874");
        livro.setPreco(BigDecimal.valueOf(100));
        livro.setGenero(GeneroLivro.CIENCIA);
        livro.setTitulo("Ciencias");
        livro.setDataPublicacao(LocalDate.of(1980,1,2));

        Autor autor = autorRepository.findById(UUID.fromString("8c1129bf-4038-460a-a0f0-63dbe7012a4a")).orElse(null);

        livro.setAutor(autor);

        repository.save(livro);
    }




}
