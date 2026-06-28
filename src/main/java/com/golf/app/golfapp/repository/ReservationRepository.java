package com.golf.app.golfapp.repository;

import com.golf.app.golfapp.model.Reservation;

import java.util.List;

public interface ReservationRepository {

    List<Reservation> findAll();

    void save(Reservation reservation);

    List<Reservation> findByUserId(Long userId);
}