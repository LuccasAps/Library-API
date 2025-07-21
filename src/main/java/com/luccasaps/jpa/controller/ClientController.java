package com.luccasaps.jpa.controller;

import com.luccasaps.jpa.model.Client;
import com.luccasaps.jpa.service.ClientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("clients")
@Tag(name = "Clientes")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('GERENTE')")
    public void salvar(@RequestBody Client client) {

        log.info("Registrando novo cliente: {} com scope: {}", client.getClientId(), client.getScope());

        clientService.salvar(client);
    }
}
