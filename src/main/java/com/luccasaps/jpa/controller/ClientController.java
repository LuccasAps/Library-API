package com.luccasaps.jpa.controller;

import com.luccasaps.jpa.model.Client;
import com.luccasaps.jpa.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GERENTE')")
    public void  salvar(@RequestBody Client client) {
        clientService.salvar(client);
    }
}
