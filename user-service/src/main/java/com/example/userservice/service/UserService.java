package com.example.userservice.service;

import com.example.userservice.client.AccHolderClient;
import com.example.userservice.dto.AccHolder;
import com.example.userservice.dto.PasswordResetRequest;
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

    public UserResponse findUser(Long userId) {
        UserRepresentation user = keycloakUserService.findUser(userId.toString());

        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    public UserResponse createUser(UserRequest userRequest) {
        List<UserRepresentation> userRepresentations = keycloakUserService.findUserByUsername(userRequest.getUsername());

        if (!userRepresentations.isEmpty()) {
            throw new RuntimeException("This username already registered as a user. Please check and retry.");
        }

        AccHolder accHolder = accHolderClient.findAccount(userRequest.getAccHolderId());

        if (accHolder.getEmail() != null) {
            UserRepresentation userRepresentation = generateUserRepresentation(
                    userRequest.getUsername(),
                    accHolder.getEmail()
            );

            userRepresentation.setCredentials(Collections.singletonList(
                    generateCredentialRepresentation(userRequest.getPassword()))
            );

            Integer userCreationResponse = keycloakUserService.createUser(userRepresentation);

            if (userCreationResponse == 201) {
                List<UserRepresentation> users = keycloakUserService.findUserByUsername(userRequest.getUsername());

                return UserResponse.builder()
                        .userId(users.get(0).getId())
                        .username(users.get(0).getUsername())
                        .build();
            }
        }

        throw new RuntimeException("We couldn't find user under given identification. Please check and retry");
    }

    public void updateUser(Long userId, PasswordResetRequest req) {
        UserRepresentation userRepresentation = keycloakUserService.findUser(userId.toString());

        if (req.getNewPassword() != null)
            userRepresentation.setCredentials(Collections.singletonList(
                    generateCredentialRepresentation(req.getNewPassword())
            ));

        keycloakUserService.updateUser(userRepresentation);
    }

    public void disableUser(Long userId) {
        UserRepresentation userRepresentation = keycloakUserService.findUser(userId.toString());
        userRepresentation.setEnabled(false);
        keycloakUserService.updateUser(userRepresentation);
    }

    private UserRepresentation generateUserRepresentation(String username, String email) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setRealmRoles(Collections.singletonList("USER"));
        userRepresentation.setUsername(username);
        userRepresentation.setEmail(email);
        userRepresentation.setEmailVerified(false);
        userRepresentation.setEnabled(false);

        return userRepresentation;
    }

    private CredentialRepresentation generateCredentialRepresentation(String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(password);
        credentialRepresentation.setTemporary(false);

        return credentialRepresentation;
    }
}
