package com.example.realestate.domain.mapper;

import com.example.realestate.data.db.entity.ReservationEntity;
import com.example.realestate.data.db.entity.ReservationWithPropertyEntity;
import com.example.realestate.data.db.entity.UserEntity;
import com.example.realestate.domain.model.Reservation;
import com.example.realestate.domain.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper class to convert between database entities and domain models for user reservations
 */
public class UserReservationMapper {

    /**
     * Maps a list of reservation entities to a map of user entities and their reservations
     * @param entities List of reservation with property entities
     * @param users Map of email to user entities for quick lookup
     * @return Map of user entity to list of reservation entities
     */
    public static Map<UserEntity, List<ReservationEntity>> toUserReservationEntityMap(
            List<ReservationWithPropertyEntity> entities, Map<String, UserEntity> users) {

        Map<UserEntity, List<ReservationEntity>> result = new HashMap<>();

        for (ReservationWithPropertyEntity entity : entities) {
            String userEmail = entity.reservation.email;
            UserEntity userEntity = users.get(userEmail);

            // Skip if user not found
            if (userEntity == null) continue;

            // Add user to map if not present
            if (!result.containsKey(userEntity)) {
                result.put(userEntity, new ArrayList<>());
            }

            // Add reservation to user's list
            result.get(userEntity).add(entity.reservation);
        }

        return result;
    }

    /**
     * Maps a map of user entities to reservation entities to a map of domain models
     * @param entityMap Map of user entities to lists of reservation entities
     * @return Map of user domain models to lists of reservation domain models
     */
    public static Map<User, List<Reservation>> toDomainMap(Map<UserEntity, List<ReservationEntity>> entityMap) {
        Map<User, List<Reservation>> result = new HashMap<>();

        for (Map.Entry<UserEntity, List<ReservationEntity>> entry : entityMap.entrySet()) {
            User user = UserMapper.toDomain(entry.getKey());
            List<Reservation> reservations = ReservationMapper.toDomainList(entry.getValue());
            result.put(user, reservations);
        }

        return result;
    }
}
