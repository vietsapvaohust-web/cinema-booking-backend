package com.cinema.booking.entity;

import java.io.Serializable;
import java.util.UUID;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class FavoriteId implements Serializable {
    private UUID user;
    private UUID movie;
}