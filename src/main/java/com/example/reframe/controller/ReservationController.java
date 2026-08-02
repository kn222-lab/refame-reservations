package com.example.reframe.controller;

import com.example.reframe.entity.Reservation;
import com.example.reframe.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    // トップページ（画面表示）
    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    // 店舗に応じた営業日取得API
    @GetMapping("/api/dates")
    @ResponseBody
    public List<LocalDate> getDates(@RequestParam("location") String location) {
        return reservationService.getAvailableDates(location);
    }

    // 指定日・店舗の予約済み時間枠取得API
    @GetMapping("/api/booked-slots")
    @ResponseBody
    public List<String> getBookedSlots(@RequestParam("location") String location,
                                       @RequestParam("date") String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        return reservationService.getBookedSlots(location, date);
    }

    // 予約実行API
    @PostMapping("/api/reserve")
    @ResponseBody
    public ResponseEntity<Map<String, String>> reserve(@RequestBody Reservation reservation) {
        Map<String, String> response = new HashMap<>();
        
        boolean success = reservationService.createReservation(reservation);
        if (success) {
            response.put("status", "OK");
            response.put("message", "予約が完了しました");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "NG");
            response.put("message", "申し訳ありません。この枠はすでに予約されています");
            return ResponseEntity.badRequest().body(response);
        }
    }
}