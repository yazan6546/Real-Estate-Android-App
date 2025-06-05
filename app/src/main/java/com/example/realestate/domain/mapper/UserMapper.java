package com.example.realestate.domain.mapper;

import com.example.realestate.data.db.entity.UserEntity;
import com.example.realestate.domain.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {

    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;

        // Create user with basic information
        User user = new User(
                entity.firstName,
                entity.lastName,
                entity.email,
                entity.password,
                entity.phone,
                entity.country,
                entity.city,
                entity.gender,
                entity.isAdmin
        );

        user.setProfileImage(entity.profileImage);
        return user;
    }

    public static UserEntity fromDomain(User user) {
        if (user == null) return null;

        UserEntity entity = new UserEntity();
        entity.email = user.getEmail();
        entity.password = user.getPassword();
        entity.firstName = user.getFirstName();
        entity.lastName = user.getLastName();
        entity.phone = user.getPhone();
        entity.country = user.getCountry();
        entity.city = user.getCity();
        entity.gender = user.getGender() != null ? user.getGender().name() : null;
        entity.isAdmin = user.isAdmin();
        entity.profileImage = user.getProfileImage();

        return entity;
    }

    public static List<User> toDomainList(List<UserEntity> users) {
        return users.stream()
                .map(UserMapper::toDomain)
                .collect(Collectors.toList());
    }
}

