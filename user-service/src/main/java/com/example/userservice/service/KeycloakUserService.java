package com.example.userservice.service;

import com.example.userservice.config.KeycloakManager;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private final KeycloakManager keyCloakManager;

    public Integer createUser(UserRepresentation userRepresentation) {
        Response response = keyCloakManager.getKeyCloakInstanceWithRealm().users()
                .create(userRepresentation);

        return response.getStatus();
    }

    public void updateUser(UserRepresentation userRepresentation) {
        keyCloakManager.getKeyCloakInstanceWithRealm().users()
                .get(userRepresentation.getId())
                .update(userRepresentation);
    }

    public List<UserRepresentation> findUserByUsername(String username) {
        return keyCloakManager.getKeyCloakInstanceWithRealm().users().search(username);
    }

    public UserRepresentation findUser(String authId) {
        try {
            UserResource userResource = keyCloakManager.getKeyCloakInstanceWithRealm().users().get(authId);
            return userResource.toRepresentation();
        } catch (Exception e) {
            throw new RuntimeException("User not found under given ID");
        }
    }
}
