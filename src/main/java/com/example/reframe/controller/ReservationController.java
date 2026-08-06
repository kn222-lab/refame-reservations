package com.example.reframe.controller;

import com.example.reframe.entity.Reservation;
import com.example.reframe.service.EmailService; // ★追加
import com.example.reframe.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private EmailService emailService; // ★追加

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/api/dates")
    @ResponseBody
    public List<LocalDate> getDates(@RequestParam String location) {
        return reservationService.getAvailableDates(location);
    }

    @GetMapping("/api/booked-slots")
    @ResponseBody
    public List<String> getBookedSlots(@RequestParam String location, @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return reservationService.getBookedSlots(location, localDate);
    }

    @PostMapping("/api/reserve")
    @ResponseBody
    public ResponseEntity<Map<String, String>> reserve(@RequestBody ReservationRequest request) {
        Map<String, String> response = new HashMap<>();

        Reservation reservation = new Reservation();
        reservation.setReservationCode(UUID.randomUUID().toString());
        reservation.setLocationName(request.getLocation());
        reservation.setReservedDate(LocalDate.parse(request.getDate()));
        reservation.setTimeSlot(request.getTimeSlot());
        reservation.setCustomerName(request.getCustomerName());
        reservation.setCustomerEmail(request.getCustomerEmail());
        reservation.setStatus("BOOKED");

        boolean success = reservationService.createReservation(reservation, request.getLocation());

        if (success) {
            // ★ DB登録成功後にメール送信処理を実行
            try {
                emailService.sendCustomerConfirmation(reservation); // お客様向け
                emailService.sendAdminNotification(reservation);    // 管理者向け
            } catch (Exception e) {
                // メール送信でエラーが起きても予約自体は完了させるためのログ出力
                System.err.println("メール送信エラー: " + e.getMessage());
            }

            response.put("status", "OK");
            response.put("message", "予約が完了しました");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "NG");
            response.put("message", "申し訳ありません。この枠はすでに予約されています");
            return ResponseEntity.badRequest().body(response);
        }
    }

    // DTO Class
    public static class ReservationRequest {
        private String location;
        private String date;
        private String timeSlot;
        private String customerName;
        private String customerEmail;

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getTimeSlot() { return timeSlot; }
        public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    }

    // 管理者画面の表示
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    // 管理者用: 予約一覧API
    @GetMapping("/api/admin/reservations")
    @ResponseBody
    public List<Reservation> getAdminReservations(
            @RequestParam(defaultValue = "すべて") String location,
            @RequestParam(required = false) String date) {
        return reservationService.getAdminReservations(location, date);
    }

    // 管理者用: 予約キャンセルAPI
    @PostMapping("/api/admin/reservations/{id}/cancel")
    @ResponseBody
    public ResponseEntity<String> cancelReservation(@PathVariable Long id) {
        boolean success = reservationService.cancelReservation(id);
        if (success) {
            return ResponseEntity.ok("予約をキャンセルしました");
        } else {
            return ResponseEntity.badRequest().body("対象の予約が見つかりませんでした");
        }
    } 
}