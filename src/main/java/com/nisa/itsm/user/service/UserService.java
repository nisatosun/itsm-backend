package com.nisa.itsm.user.service;

import com.nisa.itsm.common.exception.ResourceNotFoundException;
import com.nisa.itsm.exception.custom.UserAlreadyExistsException;
import com.nisa.itsm.user.dto.request.UpdateUserRolesRequest;
import com.nisa.itsm.user.dto.request.UserCreateRequest;
import com.nisa.itsm.user.dto.UserSummaryDto;
import com.nisa.itsm.user.dto.UserDetailDto;
import com.nisa.itsm.user.dto.response.UserResponse;
import com.nisa.itsm.user.entity.User;
import com.nisa.itsm.user.repository.UserRepository;
import com.nisa.itsm.common.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Bu email zaten kullanımda");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Bu username zaten kullanımda");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Page<UserSummaryDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> new UserSummaryDto(
                        String.valueOf(user.getId()),
                        user.getUsername(),
                        user.getEmail(),
                        null,
                        null,
                        List.of()));
    }

    public UserDetailDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserDetailDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        null,
                        null,
                        null,
                        user.getRoles().stream().map(Enum::name).collect(Collectors.toList()),
                        user.getCreatedAt()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserResponse updateUserRoles(Long id, UpdateUserRolesRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<Role> roles = request.getRoles().stream()
                .map(roleStr -> Role.valueOf(roleStr.toUpperCase()))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        userRepository.save(user);

        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());
        response.setRoles(
                user.getRoles()
                        .stream()
                        .map(Enum::name)
                        .toList()
        );
        return response;
    }
}
