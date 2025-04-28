package com.luccasaps.jpa.controller.mappers;

import com.luccasaps.jpa.controller.dto.CadastroLivroDTO;
import com.luccasaps.jpa.controller.dto.ResultadoPesquisaLivroDTO;
import com.luccasaps.jpa.model.Livro;
import com.luccasaps.jpa.repository.AutorRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = AutorMapper.class)
public abstract class LivroMapper {

    @Autowired
    AutorRepository autorRepository;

    @Mapping(target = "autor", expression = "java( autorRepository.findById(dto.idAutor()).orElse(null) )")
    public abstract Livro toEntity(CadastroLivroDTO dto);

    public abstract ResultadoPesquisaLivroDTO toDTO(Livro livro);
}
