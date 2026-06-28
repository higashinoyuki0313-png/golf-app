package com.golf.app.golfapp.repository;

import com.golf.app.golfapp.model.Reservation;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class InMemoryReservationRepository
        implements ReservationRepository {

    private final List<Reservation> reservations =
            new ArrayList<>();

    @Override
    public List<Reservation> findAll() {
        return reservations;
    }

    @Override
    public void save(Reservation reservation) {
        reservations.add(reservation);
    }

    @Override
    public List<Reservation> findByUserId(Long userId) {

        return reservations.stream()
                .filter(reservation ->
                        reservation.getUserId().equals(userId))
                .toList();
    }
}