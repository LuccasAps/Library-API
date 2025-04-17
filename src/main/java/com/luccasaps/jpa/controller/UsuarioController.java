package com.luccasaps.jpa.controller;

import com.luccasaps.jpa.controller.dto.UsuarioDTO;
import com.luccasaps.jpa.controller.mappers.UsuarioMapper;
import com.luccasaps.jpa.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void salvar (@RequestBody UsuarioDTO dto){
        var usuario = usuarioMapper.toEntity(dto);
        usuarioService.salvar(usuario);
    }
}
