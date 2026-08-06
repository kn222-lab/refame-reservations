package com.example.reframe.service;

import com.example.reframe.entity.Location;
import com.example.reframe.entity.Reservation;
import com.example.reframe.repository.LocationRepository;
import com.example.reframe.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private LocationRepository locationRepository;

    public static final List<String> ALL_SLOTS = Arrays.asList("11:00", "12:15", "13:30", "14:45");

    public List<LocalDate> getAvailableDates(String locationName) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();

        YearMonth currentMonth = YearMonth.from(today);
        List<YearMonth> targetMonths = Arrays.asList(currentMonth, currentMonth.plusMonths(1));

        List<Integer> targetWeeks = "逗子".equals(locationName) ? Arrays.asList(1, 3) : Arrays.asList(2, 4);

        for (YearMonth ym : targetMonths) {
            int lengthOfMonth = ym.lengthOfMonth();
            for (int day = 1; day <= lengthOfMonth; day++) {
                LocalDate date = ym.atDay(day);

                if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    int weekOfMonth = (day - 1) / 7 + 1;
                    if (targetWeeks.contains(weekOfMonth)) {
                        if (!date.isBefore(today)) {
                            dates.add(date);
                        }
                    }
                }
            }
        }
        return dates;
    }

    public List<String> getBookedSlots(String locationName, LocalDate date) {
        List<Reservation> reservations = reservationRepository.findByLocation_NameAndReservedDateAndStatus(
                locationName, date, "BOOKED");
        
        List<String> bookedSlots = new ArrayList<>();
        for (Reservation r : reservations) {
            bookedSlots.add(r.getTimeSlot());
        }
        return bookedSlots;
    }

    public boolean createReservation(Reservation reservation, String locationName) {
        boolean exists = reservationRepository.existsByLocation_NameAndReservedDateAndTimeSlotAndStatus(
                locationName,
                reservation.getReservedDate(),
                reservation.getTimeSlot(),
                "BOOKED"
        );

        if (exists) {
            return false;
        }

        Location location = locationRepository.findByName(locationName)
                .orElseThrow(() -> new IllegalArgumentException("指定された店舗が存在しません: " + locationName));
        
        reservation.setLocation(location);

        reservationRepository.save(reservation);
        return true;
    }

    /**
     * 管理者用: 条件に応じた予約一覧の取得
     */
    public List<Reservation> getAdminReservations(String locationName, String dateStr) {
        if ("すべて".equals(locationName) && (dateStr == null || dateStr.isBlank())) {
            return reservationRepository.findAll();
        }

        // 全件からメモリ上で簡易フィルタリング（件数が少ないフェーズ1で最適）
        return reservationRepository.findAll().stream()
                .filter(r -> "すべて".equals(locationName) || r.getLocationName().equals(locationName))
                .filter(r -> dateStr == null || dateStr.isBlank() || r.getReservedDate().toString().equals(dateStr))
                .sorted((a, b) -> b.getReservedDate().compareTo(a.getReservedDate()))
                .toList();
    }

    /**
     * 管理者用: 予約のキャンセル処理
     */
    public boolean cancelReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservation -> {
            reservation.setStatus("CANCELLED");
            reservationRepository.save(reservation);
            return true;
        }).orElse(false);
    }
}