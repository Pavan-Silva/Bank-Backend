package com.example.userservice.service;

import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;

public interface KeycloakService {

    Integer createUser(UserRepresentation userRepresentation);

    void updateUser(UserRepresentation userRepresentation);

    List<UserRepresentation> findUserByUsername(String username);

    UserRepresentation findUser(String authId);
}
