package com.nisa.itsm.user.controller;

import com.nisa.itsm.common.dto.PageResponse;
import com.nisa.itsm.user.dto.UserDetailDto;
import com.nisa.itsm.user.dto.UserSummaryDto;
import com.nisa.itsm.user.dto.request.UpdateUserRolesRequest;
import com.nisa.itsm.user.dto.request.UserCreateRequest;
import com.nisa.itsm.user.dto.response.UserResponse;
import com.nisa.itsm.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(params = {"page"})
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<UserSummaryDto> getPaginatedUsers(Pageable pageable) {
        Page<UserSummaryDto> page = userService.getAllUsers(pageable);

        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDetailDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRolesRequest request) {
        return userService.updateUserRoles(id, request);
    }
}