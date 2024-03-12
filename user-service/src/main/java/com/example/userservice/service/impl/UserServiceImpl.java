package com.example.userservice.service.impl;

import com.example.userservice.client.AccHolderClient;
import com.example.userservice.client.EmailClient;
import com.example.userservice.dto.request.Mail;
import com.example.userservice.dto.request.PasswordResetRequest;
import com.example.userservice.dto.request.UserRequest;
import com.example.userservice.dto.request.UserVerificationRequest;
import com.example.userservice.dto.response.AccHolder;
import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.exception.BadRequestException;
import com.example.userservice.exception.NotFoundException;
import com.example.userservice.model.UserVerification;
import com.example.userservice.repository.UserVerificationRepository;
import com.example.userservice.service.KeycloakService;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final KeycloakService keycloakService;
    private final AccHolderClient accHolderClient;
    private final EmailClient emailClient;
    private final UserVerificationRepository userVerificationRepository;

    @Override
    public List<UserResponse> findAll(int page, int size) {
        return keycloakService.findAllUsers(page, size).stream().map(
                user -> UserResponse.builder()
                        .userId(user.getId())
                        .username(user.getUsername())
                        .build()
        ).toList();
    }

    @Override
    public UserResponse findUser(String userId) {
        UserRepresentation user = keycloakService.findUser(userId);

        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .build();
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        List<UserRepresentation> userRepresentations = keycloakService.findUserByUsername(userRequest.getUsername());

        if (!userRepresentations.isEmpty()) {
            throw new BadRequestException("This username already registered as a user. Please check and retry.");
        }

        AccHolder accHolder = accHolderClient.findAccount(userRequest.getAccHolderId());

        if (accHolder.getEmail() != null) {
            if (!accHolder.getNic().equals(userRequest.getNic())) {
                throw new BadRequestException("User verification failed");
            }

            UserRepresentation userRepresentation = generateUserRepresentation(
                    userRequest.getUsername(),
                    accHolder.getEmail()
            );

            userRepresentation.setCredentials(Collections.singletonList(
                    generateCredentialRepresentation(userRequest.getPassword()))
            );

            Integer userCreationResponse = keycloakService.createUser(userRepresentation);

            if (userCreationResponse == 201) {
                List<UserRepresentation> users = keycloakService.findUserByUsername(userRequest.getUsername());

                sendVerification(users.get(0));

                return UserResponse.builder()
                        .userId(users.get(0).getId())
                        .username(users.get(0).getUsername())
                        .build();
            }
        }

        throw new NotFoundException("We couldn't find user under given identification. Please check and retry");
    }

    private void sendVerification(UserRepresentation user) {
        userVerificationRepository.deleteAllByUserId(user.getId());

        Random random = new Random();

        UserVerification userVerification = userVerificationRepository.save(
                UserVerification.builder()
                        .expirationTime(Instant.now().plus(6, ChronoUnit.HOURS))
                        .code(String.valueOf(random.nextInt(1000000)))
                        .userId(user.getId())
                        .build()
        );

        emailClient.sendMail(
                Mail.builder()
                        .subject("ABC Bank Account Verification")
                        .message("Your account verification code is - " + userVerification.getCode())
                        .receiver(user.getEmail())
                        .build()
        );
    }

    @Override
    public void verifyUser(String userId, UserVerificationRequest request) {
        UserVerification userVerification = userVerificationRepository.findByUserId(userId)
                .orElseThrow();

        boolean isCodeValid = userVerification.getCode().equals(request.getCode());
        boolean isNotExpired = userVerification.getExpirationTime().isAfter(Instant.now());

        if (isNotExpired && isCodeValid) {
            UserRepresentation userRepresentation = keycloakService.findUser(userId);
            userRepresentation.setEmailVerified(true);
            userRepresentation.setEnabled(true);
            keycloakService.updateUser(userRepresentation);

            userVerificationRepository.delete(userVerification);
        }

        else throw new RuntimeException();
    }

    @Override
    public void updateUser(Long userId, PasswordResetRequest req) {
        UserRepresentation userRepresentation = keycloakService.findUser(userId.toString());

        if (userRepresentation == null)
            throw new NotFoundException("User not found");

        if (req.getNewPassword() != null)
            userRepresentation.setCredentials(Collections.singletonList(
                    generateCredentialRepresentation(req.getNewPassword())
            ));

        keycloakService.updateUser(userRepresentation);
    }

    @Override
    public void disableUser(String userId) {
        UserRepresentation userRepresentation = keycloakService.findUser(userId);

        if (userRepresentation == null)
            throw new NotFoundException("User not found");

        userRepresentation.setEnabled(false);
        keycloakService.updateUser(userRepresentation);
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
