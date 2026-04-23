package com.nisa.itsm.user.controller;

import com.nisa.itsm.user.dto.UserDetailDto;
import com.nisa.itsm.user.dto.UserSummaryDto;
import com.nisa.itsm.user.dto.request.UpdateUserRolesRequest;
import com.nisa.itsm.user.dto.request.UserCreateRequest;
import com.nisa.itsm.user.dto.response.UserResponse;
import com.nisa.itsm.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Creates a new user")
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Returns all users without pagination")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(params = {"page"})
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get paginated users", description = "Returns paginated users, only accessible by ADMIN")
    public Page<UserSummaryDto> getPaginatedUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Get user by id", description = "Returns user details by user id, only accessible by ADMIN")
    public UserDetailDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update user roles", description = "Updates roles of a user, only accessible by ADMIN")
    public UserResponse updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRolesRequest request) {
        return userService.updateUserRoles(id, request);
    }
}
