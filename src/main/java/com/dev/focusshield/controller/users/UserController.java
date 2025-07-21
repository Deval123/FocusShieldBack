package com.dev.focusshield.controller.users;

import com.dev.focusshield.model.*;
import com.dev.focusshield.service.users.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.dev.focusshield.utils.contants.ApiRoutes.*;

@RestController
@RequestMapping(USERS)
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "400", description = "Invalid password format", content = @Content)
    })
    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody @Valid RegisterRequest request) {
        User registered = userService.register(request);
        return ResponseEntity.status(201).body(registered);
    }

    @Operation(summary = "Get paginated list of all users")
    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @Operation(summary = "Get a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable UUID userId) {
        return userService.getUserById(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update an existing user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable UUID userId,
            @RequestBody UpdateRequest updateRequest
    ) {
        return userService.updateUser(userId, updateRequest)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Login a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        AuthResponse authResponse = userService.login(request);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Update - Update an existing application
     * @param userEmail - The Email of the user to update
     * @param roleName - The name of the role updated
     * @return user - The user updated
     */
    @PatchMapping(USERS_ASSIGN_ROLE)
    public User assignRole(@RequestParam("userEmail") final String userEmail, @RequestParam("roleName") final String roleName) {
        return userService.assignRoleToUser(userEmail, roleName);
    }

    /**
     * Update - Update an existing application
     * @param userEmail - The Email of the user to update
     * @param roleName - The name of the role updated
     * @return user - The user updated
     */
    @PatchMapping(USERS_UNASSIGN_ROLE)
    public User unassignRole(@RequestParam("userEmail") final String userEmail, @RequestParam("roleName") final String roleName) {
        return userService.unassignRoleToUser(userEmail, roleName);
    }

}