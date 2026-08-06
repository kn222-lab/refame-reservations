package com.example.reframe.repository;

import com.example.reframe.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByLocation_NameAndReservedDateAndStatus(
            String locationName, LocalDate reservedDate, String status);

    boolean existsByLocation_NameAndReservedDateAndTimeSlotAndStatus(
            String locationName, LocalDate reservedDate, String timeSlot, String status);

    // ★今回追加するメソッド（予約コードによる1件検索）
    Optional<Reservation> findByReservationCode(String reservationCode);
}