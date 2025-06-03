package com.example.realestate.domain.mapper;

import com.example.realestate.data.db.entity.UserEntity;
import com.example.realestate.domain.model.User;

public class UserMapper {
    public static UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();

        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setEmail(user.getEmail());
        entity.setPassword(user.getPassword());
        entity.setPhone(user.getPhone());

        if (user.getGender() != null) {
            entity.setGender(user.getGender().name());
        } else {
            entity.setGender(null);
        }
        entity.setAdmin(user.isAdmin());
        entity.setCountry(user.getCountry());
        entity.setCity(user.getCity());
        return entity;
    }

    public static User fromEntity(UserEntity entity) {
        return new User(
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getPhone(),
                entity.getCountry(),
                entity.getCity(),
                entity.isAdmin
        );
    }
}