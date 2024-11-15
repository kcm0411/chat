package com.example.chat.repository;

import com.example.chat.entity.User;

import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findByLoginIdAndName(String username, String name);
}