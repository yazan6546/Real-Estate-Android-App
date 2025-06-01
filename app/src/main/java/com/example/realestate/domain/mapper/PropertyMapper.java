package com.example.realestate.domain.mapper;

import com.example.realestate.data.api.dto.PropertyDTO;
import com.example.realestate.data.db.entity.PropertyEntity;

public class PropertyMapper {
    public static PropertyEntity toEntity(PropertyDTO dto) {
        PropertyEntity entity = new PropertyEntity();
        entity.property_id = dto.id;
        entity.title = dto.title;
        entity.price = dto.price;
        entity.type = dto.type;
        entity.image = dto.imageUrl;
        entity.location = dto.location; // Assuming location is also part of the entity
        entity.description = dto.description; // Assuming description is also part of the entity
        entity.bedrooms = dto.bedrooms; // Assuming bedrooms is also part of the entity
        entity.bathrooms = dto.bathrooms; // Assuming bathrooms is also part of the entity
        entity.area = dto.area; // Assuming area is also part of the entity

        return entity;
    }

    public static PropertyDTO toDTO(PropertyEntity entity) {
        PropertyDTO dto = new PropertyDTO();
        dto.id = entity.property_id;
        dto.title = entity.title;
        dto.price = entity.price;
        dto.type = entity.type;
        dto.imageUrl = entity.image;
        dto.location = entity.location; // Assuming location is also part of the DTO
        dto.description = entity.description; // Assuming description is also part of the DTO
        dto.bedrooms = entity.bedrooms; // Assuming bedrooms is also part of the DTO
        dto.bathrooms = entity.bathrooms; // Assuming bathrooms is also part of the DTO
        dto.area = entity.area; // Assuming area is also part of the DTO
        return dto;
    }
}
