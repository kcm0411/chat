package com.example.chat.repository;

import com.example.chat.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.Optional;


public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<User> findByLoginIdAndName(String username, String name) {
        String jpql = "SELECT u FROM User u WHERE u.username = :username AND u.name = :name";
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("username", username);
        query.setParameter("name", name);

        return query.getResultList().stream().findFirst();
    }
}
