package com.luccasaps.jpa.controller.mappers;

import com.luccasaps.jpa.controller.dto.UsuarioDTO;
import com.luccasaps.jpa.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    Usuario toEntity(UsuarioDTO dto);
}
