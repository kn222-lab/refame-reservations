package com.example.reframe.repository;

import com.example.reframe.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByLocationNameAndReservationDateAndStatus(
        String locationName, 
        LocalDate reservationDate, 
        String status
    );

    boolean existsByLocationNameAndReservationDateAndSlotTimeAndStatus(
        String locationName, 
        LocalDate reservationDate, 
        String slotTime, 
        String status
    );
}