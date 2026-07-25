package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.ReservationMapper;
import com.golf.app.golfapp.model.Account;
import com.golf.app.golfapp.model.Reservation;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ReservationController {

    private final ReservationMapper reservationMapper;

    public ReservationController(ReservationMapper reservationMapper) {
        this.reservationMapper = reservationMapper;
    }

    @PostMapping("/lessons/{id}/apply")
    public String applyLesson(
            @PathVariable Long id,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 1) {
            return "redirect:/lessons";
        }

        Reservation reservation = new Reservation();

        reservation.setLessonId(id);
        reservation.setUserId(loginAccount.getId());
        reservation.setStatus(1);

        reservationMapper.save(reservation);

        return "redirect:/my-lessons";
    }

    @GetMapping("/my-lessons")
    public String myLessons(
            Model model,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 1) {
            return "redirect:/pro/home";
        }

        model.addAttribute(
                "reservations",
                reservationMapper.findMyLessons(loginAccount.getId())
        );

        model.addAttribute("loginAccount", loginAccount);

        return "reservations/my-lessons";
    }

    @GetMapping("/my-lessons/{id}")
    public String show(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 1) {
            return "redirect:/pro/home";
        }

        Reservation reservation = reservationMapper.findById(id);

        model.addAttribute("reservation", reservation);
        model.addAttribute("loginAccount", loginAccount);

        return "reservations/show";
    }

    @PostMapping("/my-lessons/{id}/status/{status}")
    public String updateReservationStatus(
            @PathVariable Long id,
            @PathVariable Integer status,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 1) {
            return "redirect:/pro/home";
        }

        reservationMapper.updateStatus(id, status);

        return "redirect:/my-lessons";
    }
}