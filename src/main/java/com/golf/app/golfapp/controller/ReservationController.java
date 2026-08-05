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

    /**
     * マイレッスン一覧。
     *
     * <p>ヘッダーのリンクや動画提出後のリダイレクト先が /my-lessons を指しているため、
     * この URL で公開する。一覧にはレッスン名・担当プロ・提出状況を表示するので、
     * それらを JOIN / 集計して返す findMyLessons を使う
     * (findByUserId は予約の id と status しか返さず、画面の項目を埋められない)。
     */
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

    // 旧 URL。ブックマークや古いリンクから来た場合に 404 にせず現行の URL へ送る。
    @GetMapping("/reservations")
    public String index() {
        return "redirect:/my-lessons";
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