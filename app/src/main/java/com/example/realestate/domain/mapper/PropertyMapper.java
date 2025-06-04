package com.example.realestate.domain.mapper;

import com.example.realestate.data.api.dto.PropertyDTO;
import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.domain.model.Property;

public class PropertyMapper {
    public static PropertyEntity toEntity(PropertyDTO dto) {
        PropertyEntity entity = new PropertyEntity();
        entity.property_id = dto.id;
        entity.title = dto.title;
        entity.price = dto.price;
        entity.type = dto.type;
        entity.image = dto.imageUrl;
        entity.location = dto.location;
        entity.description = dto.description;
        entity.bedrooms = dto.bedrooms;
        entity.bathrooms = dto.bathrooms;
        entity.area = dto.area;

        return entity;
    }

    public static PropertyDTO toDTO(PropertyEntity entity) {
        PropertyDTO dto = new PropertyDTO();
        dto.id = entity.property_id;
        dto.title = entity.title;
        dto.price = entity.price;
        dto.type = entity.type;
        dto.imageUrl = entity.image;
        dto.location = entity.location;
        dto.description = entity.description;
        dto.bedrooms = entity.bedrooms;
        dto.bathrooms = entity.bathrooms;
        dto.area = entity.area;
        return dto;
    }

    // New methods for domain model conversion

    public static Property toDomain(PropertyEntity entity) {
        if (entity == null) return null;

        Property property = new Property();
        property.setPropertyId(entity.property_id);
        property.setTitle(entity.title);
        property.setDescription(entity.description);
        property.setPrice(entity.price);
        property.setLocation(entity.location);
        property.setImage(entity.image);
        property.setType(entity.type);
        property.setBedrooms(entity.bedrooms);
        property.setBathrooms(entity.bathrooms);
        property.setArea(entity.area);
        return property;
    }

    public static PropertyEntity fromDomain(Property property) {
        if (property == null) return null;

        PropertyEntity entity = new PropertyEntity();
        entity.property_id = property.getPropertyId();
        entity.title = property.getTitle();
        entity.description = property.getDescription();
        entity.price = property.getPrice();
        entity.location = property.getLocation();
        entity.image = property.getImage();
        entity.type = property.getType();
        entity.bedrooms = property.getBedrooms();
        entity.bathrooms = property.getBathrooms();
        entity.area = property.getArea();
        return entity;
    }
}
