package com.example.reframe.service;

import com.example.reframe.entity.Reservation;
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

    // 固定枠の定義
    public static final List<String> ALL_SLOTS = Arrays.asList("11:00", "12:15", "13:30", "14:45");

    /**
     * 店舗に応じた対象土曜日の日付一覧を取得
     * 逗子: 第1・3土曜 / 由比ヶ浜: 第2・4土曜
     */
    public List<LocalDate> getAvailableDates(String locationName) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // 当月と翌月を対象
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
                        // 過去日は除外
                        if (!date.isBefore(today)) {
                            dates.add(date);
                        }
                    }
                }
            }
        }
        return dates;
    }

    /**
     * 指定店舗・日付で予約済みの時間枠一覧を取得
     */
    public List<String> getBookedSlots(String locationName, LocalDate date) {
        List<Reservation> reservations = reservationRepository.findByLocationNameAndReservationDateAndStatus(
                locationName, date, "予約");
        
        List<String> bookedSlots = new ArrayList<>();
        for (Reservation r : reservations) {
            bookedSlots.add(r.getSlotTime());
        }
        return bookedSlots;
    }

    /**
     * 予約登録処理
     */
    public boolean createReservation(Reservation reservation) {
        // 重複チェック
        boolean exists = reservationRepository.existsByLocationNameAndReservationDateAndSlotTimeAndStatus(
                reservation.getLocationName(),
                reservation.getReservationDate(),
                reservation.getSlotTime(),
                "予約"
        );

        if (exists) {
            return false; // 重複あり
        }

        reservationRepository.save(reservation);
        return true;
    }
}