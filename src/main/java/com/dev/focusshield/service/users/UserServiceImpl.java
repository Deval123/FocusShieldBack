package com.dev.focusshield.service.users;

import com.dev.focusshield.config.JwtTokenProvider;
import com.dev.focusshield.entities.RoleEntity;
import com.dev.focusshield.entities.UserEntity;
import com.dev.focusshield.exceptions.*;
import com.dev.focusshield.model.LoginRequest;
import com.dev.focusshield.model.RegisterRequest;
import com.dev.focusshield.model.UpdateRequest;
import com.dev.focusshield.model.User;
import com.dev.focusshield.repositories.RoleRepository;
import com.dev.focusshield.repositories.UserRepository;
import com.dev.focusshield.utils.PasswordEncryptionUtil;
import com.dev.focusshield.utils.PasswordValidator;
import com.dev.focusshield.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.dev.focusshield.entities.AccountStatus.REGISTERED;
import static com.dev.focusshield.exceptions.FocusShieldErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncryptionUtil passwordEncryptionUtil;
    private final RoleRepository roleRepository;
    private final JwtTokenProvider jwtTokenProvider;


    /**
     * Registers a new user in the system.
     *
     * @param registerRequest the registration data
     * @return AuthResponse containing minimal user data or token (TODO)
     */
    @Override
    public User register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new FocusShieldAlreadyExistsException(DATA_ERROR_USERNAME_ALREADY_TAKEN);
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new FocusShieldAlreadyExistsException(DATA_ERROR_EMAIL_ALREADY_TAKEN);
        }
        if (!PasswordValidator.isValidPassword(registerRequest.getPassword())) {
            throw new FocusShieldException(DATA_ERROR_INVALID_PASSWORD_FORMAT);
        }
        var savedUser  = UserMapper.INSTANCE.registerRequestToUserEntity(registerRequest);

        savedUser.setPassword(passwordEncryptionUtil.encryptPassword(registerRequest.getPassword()));
        savedUser.setStatus(REGISTERED);

        RoleEntity userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new FocusShieldNotFoundException(DATA_ERROR_DEFAULT_ROLE_NOT_FOUND));
        savedUser.setRoles(List.of(userRole));

        return UserMapper.INSTANCE.userEntityToUser(userRepository.save(savedUser));
    }

    /**
     * Retrieves a paginated list of all users.
     *
     * @param page the page number
     * @param size the size of each page
     * @return paginated list of users
     */
    @Override
    public Page<User> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAll(pageable)
                .map(UserMapper.INSTANCE::userEntityToUser);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId the unique ID of the user
     * @return Optional containing User if found
     */
    @Override
    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId)
                .map(UserMapper.INSTANCE::userEntityToUser);
    }

    /**
     * Updates an existing user's information.
     *
     * @param userId        the ID of the user to update
     * @param updateRequest the new user data
     * @return Optional of updated User
     */
    @Override
    public Optional<User> updateUser(UUID userId, UpdateRequest updateRequest) {
        return userRepository.findById(userId)
                .map(entity -> {
                    Optional.ofNullable(updateRequest.getFirstname())
                            .ifPresent(entity::setFirstname);
                    Optional.ofNullable(updateRequest.getSurname())
                            .ifPresent(entity::setSurname);
                    Optional.ofNullable(updateRequest.getUsername())
                            .ifPresent(entity::setUsername);
                    Optional.ofNullable(updateRequest.getPhone())
                            .ifPresent(entity::setPhone);
                    Optional.ofNullable(updateRequest.getEmail())
                            .ifPresent(entity::setEmail);
                    Optional.ofNullable(updateRequest.getDateOfBirth())
                            .ifPresent(entity::setDateOfBirth);
                    return userRepository.save(entity);
                })
                .map(UserMapper.INSTANCE::userEntityToUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     */
    @Override
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new FocusShieldNotFoundException(DATA_ERROR_USER_NOT_FOUND);
        }
        userRepository.deleteById(userId);
    }

    /**
     * Assigns a role to a user identified by their email.
     *
     * @param email the email of the user
     * @param role  the role to assign
     * @return the updated user
     * @throws FocusShieldNotFoundException if the user or role is not found
     */
    @Override
    public User assignRoleToUser(String email, String role) {
        var roleEntity = roleRepository.findByRoleName(role)
                .orElseThrow(() -> new FocusShieldNotFoundException(FocusShieldErrorCode.DATA_ERROR_ROLE_NOT_FOUND));

        var userToUpdate = getUserByEmailOrThrow(email);

        if (userToUpdate.getRoles() == null) {
            userToUpdate.setRoles(new ArrayList<>());
        }

        if (!userToUpdate.getRoles().contains(roleEntity)) {
            userToUpdate.getRoles().add(roleEntity);
        }

        return UserMapper.INSTANCE.userEntityToUser(userRepository.saveAndFlush(userToUpdate));
    }


    /**
     * Unassigns a role from a user identified by their email.
     *
     * @param email the email of the user
     * @param role  the role to unassign
     * @return the updated user
     * @throws FocusShieldNotFoundException if the user or role is not found
     */
    @Override
    public User unassignRoleToUser(String email, String role) {
        var roleEntity = roleRepository.findByRoleName(role)
                .orElseThrow(() -> new FocusShieldNotFoundException(FocusShieldErrorCode.DATA_ERROR_ROLE_NOT_FOUND));

        var userToUpdate = getUserByEmailOrThrow(email);

        if (userToUpdate.getRoles() != null) {
            userToUpdate.getRoles().removeIf(r -> r.equals(roleEntity));
        }

        return UserMapper.INSTANCE.userEntityToUser(userRepository.saveAndFlush(userToUpdate));
    }

    /**
     * Authenticates a user based on email and password.
     *
     * <p>This method checks if a user exists with the given email, then verifies
     * that the provided password matches the stored (encoded) password.
     * If authentication is successful, a JWT token is generated and returned.
     *
     * @param request the login request containing the user's email and password
     * @return a JWT token if authentication is successful
     * @throws IllegalArgumentException if the email is not found or the password does not match
     */
    @Override
    public String login(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new FocusShieldInvalidCredentials(DATA_ERROR_INVALID_CREDENTIALS));

        if (!passwordEncryptionUtil.matches(request.getPassword(), user.getPassword())) {
            throw new FocusShieldInvalidCredentials(DATA_ERROR_INVALID_CREDENTIALS);
        }

        return jwtTokenProvider.generateToken(user);
    }

    /**
     * Retrieves a user entity by email or throws a FocusShieldNotFoundException.
     *
     * @param email the email of the user
     * @return the user entity
     * @throws FocusShieldNotFoundException if the user is not found
     */
    private UserEntity getUserByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new FocusShieldNotFoundException(FocusShieldErrorCode.DATA_ERROR_USER_NOT_FOUND));
    }

}