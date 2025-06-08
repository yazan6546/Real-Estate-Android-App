package com.example.realestate.domain.mapper;

import com.example.realestate.data.db.entity.ReservationEntity;
import com.example.realestate.data.db.entity.PropertyEntity;
import com.example.realestate.domain.model.Reservation;
import com.example.realestate.domain.model.Property;
import java.util.List;
import java.util.stream.Collectors;

public class ReservationMapper {

    public static Reservation toDomain(ReservationEntity entity) {
        if (entity == null) return null;

        Reservation reservation = new Reservation();
        reservation.setReservationId(entity.reservation_id);
        reservation.setEmail(entity.email);
        reservation.setPropertyId(entity.property_id);
        reservation.setStartDate(entity.startDate);
        reservation.setEndDate(entity.endDate);
        reservation.setStatus(entity.status);
        return reservation;
    }

    public static Reservation toDomainWithProperty(ReservationEntity entity, PropertyEntity propertyEntity) {
        if (entity == null) return null;

        Reservation reservation = toDomain(entity);
        if (propertyEntity != null) {
            Property property = PropertyMapper.toDomain(propertyEntity);
            reservation.setProperty(property);
        }
        return reservation;
    }
    
    public static List<Reservation> toDomainList(List<ReservationEntity> entities) {
        if (entities == null) return null;

        return entities.stream()
                .map(ReservationMapper::toDomain)
                .collect(Collectors.toList());
    }

    public static ReservationEntity fromDomain(Reservation reservation) {
        if (reservation == null) return null;

        ReservationEntity entity = new ReservationEntity();
        entity.reservation_id = reservation.getReservationId();
        entity.email = reservation.getEmail();
        entity.property_id = reservation.getPropertyId();
        entity.startDate = reservation.getStartDate();
        entity.endDate = reservation.getEndDate();
        entity.status = reservation.getStatus();
        return entity;
    }

    public static List<ReservationEntity> fromDomainList(List<Reservation> reservations) {
        if (reservations == null) return null;

        return reservations.stream()
                .map(ReservationMapper::fromDomain)
                .collect(Collectors.toList());
    }
}
