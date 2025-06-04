package com.example.realestate.domain.mapper;

import com.example.realestate.data.db.entity.FavoriteEntity;
import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.domain.model.Favorite;
import com.example.realestate.domain.model.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FavoriteMapper {

    public static Favorite toDomain(FavoriteEntity entity) {
        if (entity == null) return null;

        return new Favorite
                (entity.email,
                entity.property_id,
                entity.added_date);
    }

    public static Favorite toDomainWithProperty(FavoriteEntity entity, PropertyEntity propertyEntity) {
        if (entity == null) return null;

        Favorite favorite = toDomain(entity);
        if (propertyEntity != null) {
            Property property = PropertyMapper.toDomain(propertyEntity);
            favorite.setProperty(property);
        }
        return favorite;
    }

    public static FavoriteEntity fromDomain(Favorite favorite) {
        if (favorite == null) return null;

        FavoriteEntity entity = new FavoriteEntity();
        entity.email = favorite.getEmail();
        entity.property_id = favorite.getPropertyId();
        entity.added_date = favorite.getAddedDate();
        return entity;
    }

    public static List<Favorite> toDomainList(List<FavoriteEntity> entities) {
        if (entities == null) return null;

        return entities.stream()
                .map(FavoriteMapper::toDomain)
                .collect(Collectors.toList());
    }

    public static List<FavoriteEntity> fromDomainList(List<Favorite> favorites) {
        if (favorites == null) return null;

        return favorites.stream()
                .map(FavoriteMapper::fromDomain)
                .collect(Collectors.toList());

    }
}
