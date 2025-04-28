package com.luccasaps.jpa.repository;

import com.luccasaps.jpa.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {


    Client findByClientId(String clientId);
}
