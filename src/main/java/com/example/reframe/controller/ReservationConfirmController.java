package com.example.reframe.controller;

import com.example.reframe.entity.Reservation;
import com.example.reframe.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/reservation")
public class ReservationConfirmController {

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/confirm")
    public String showConfirmPage(@RequestParam("code") String code, Model model) {
        Optional<Reservation> reservationOpt = reservationRepository.findByReservationCode(code);
        reservationOpt.ifPresent(reservation -> model.addAttribute("reservation", reservation));
        return "confirm";
    }

    @PostMapping("/cancel")
    public String cancelReservation(@RequestParam("code") String code, Model model) {
        Optional<Reservation> reservationOpt = reservationRepository.findByReservationCode(code);
        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            reservation.setStatus("CANCELLED");
            reservationRepository.save(reservation);
            model.addAttribute("reservation", reservation);
        }
        return "confirm";
    }
}