package com.event.auth.service;

import com.event.auth.dto.UserDTO;
import com.event.auth.dto.UserUpdateDTO;
import com.event.auth.exception.UserNotFoundException;
import com.event.auth.model.User;
import com.event.auth.repository.UserRepository;
import com.event.auth.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User updateUser(Long userId, UserUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Update fields if provided
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getSkillsAndInterests() != null) user.setSkillsAndInterests(dto.getSkillsAndInterests());
        if (dto.getSocialProfileUrl() != null) user.setSocialProfileUrl(dto.getSocialProfileUrl());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(PasswordUtil.hashPassword(dto.getPassword()));
        }

        User savedUser = userRepository.save(user);
        log.info("Updated user {} successfully", savedUser.getId());
        return savedUser;
    }

    public List<UserDTO> getUsersByIds(List<Long> ids) {
        return userRepository.findAllById(ids)
                .stream()
                .map(UserDTO::mapDto)
                .toList();
    }
}
