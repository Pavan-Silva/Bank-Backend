package com.example.userservice.service;

import com.example.userservice.client.AccHolderClient;
import com.example.userservice.dto.AccHolder;
import com.example.userservice.dto.UserRequest;
import com.example.userservice.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloakUserService keycloakUserService;
    private final AccHolderClient accHolderClient;

    public UserResponse createUser(UserRequest userRequest) {
        List<UserRepresentation> userRepresentations = keycloakUserService.findUserByUsername(userRequest.getUsername());

        if (!userRepresentations.isEmpty()) {
            throw new RuntimeException("This username already registered as a user. Please check and retry.");
        }

        AccHolder accHolder = accHolderClient.findAccount(userRequest.getAccHolderId());

        if (accHolder.getEmail() != null) {
            UserRepresentation userRepresentation = generateRepresentation(userRequest, accHolder);

            Integer userCreationResponse = keycloakUserService.createUser(userRepresentation);

            if (userCreationResponse == 201) {
                List<UserRepresentation> users = keycloakUserService.findUserByUsername(userRequest.getUsername());

                return UserResponse.builder()
                        .authId(users.get(0).getId())
                        .username(users.get(0).getUsername())
                        .build();
            }
        }

        throw new RuntimeException("We couldn't find user under given identification. Please check and retry");
    }

    public void updateCredentials(UserRequest req) {
        UserRepresentation userRepresentation = keycloakUserService.findUser("");
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);
        keycloakUserService.updateUser(userRepresentation);
    }

    public void disableUser() {
        UserRepresentation userRepresentation = keycloakUserService.findUser("");
        userRepresentation.setEnabled(false);
        keycloakUserService.updateUser(userRepresentation);
    }

    private static UserRepresentation generateRepresentation(UserRequest userRequest, AccHolder accHolder) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userRequest.getUsername());
        userRepresentation.setEmail(accHolder.getEmail());
        userRepresentation.setEmailVerified(false);
        userRepresentation.setEnabled(false);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(userRequest.getPassword());
        credentialRepresentation.setTemporary(false);
        userRepresentation.setCredentials(Collections.singletonList(credentialRepresentation));
        return userRepresentation;
    }
}
