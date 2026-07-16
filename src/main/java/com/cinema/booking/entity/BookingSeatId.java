package com.cinema.booking.entity;

import java.io.Serializable;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class BookingSeatId implements Serializable {
    private UUID booking;
    private UUID seat;
}