package com.cinema.booking.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cinema.booking.entity.Room;

public interface RoomRepository extends JpaRepository<Room, UUID> {
}