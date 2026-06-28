package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.LessonMapper;
import com.golf.app.golfapp.mapper.ReservationMapper;
import com.golf.app.golfapp.model.Account;
import com.golf.app.golfapp.model.Lesson;
import com.golf.app.golfapp.model.Reservation;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class LessonController {

    private final LessonMapper lessonMapper;
    private final ReservationMapper reservationMapper;

    public LessonController(
            LessonMapper lessonMapper,
            ReservationMapper reservationMapper
    ) {
        this.lessonMapper = lessonMapper;
        this.reservationMapper = reservationMapper;
    }

    @GetMapping("/lessons")
    public String index(
            Model model,
            HttpSession session
    ) {
        List<Lesson> lessons = lessonMapper.findAll();

        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        model.addAttribute("lessons", lessons);
        model.addAttribute("loginAccount",loginAccount);

        return "lessons/index";
    }

    @GetMapping("/lessons/{id}")
    public String detail(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {
        Lesson lesson = lessonMapper.findById(id);

        if (lesson == null) {
            return "redirect:/lessons";
        }

        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        model.addAttribute("lesson", lesson);
        model.addAttribute("loginAccount", loginAccount);
        model.addAttribute(
                "reservationCount",
                reservationMapper.findByLessonId(id).size()
        );

        if (loginAccount != null && loginAccount.getRole() == 2) {
            model.addAttribute(
                    "reservations",
                    reservationMapper.findByLessonId(id)
            );
        }

        return "lessons/detail";
    }

    @GetMapping("/lessons/new")
    public String newLesson(HttpSession session) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 2) {
            return "redirect:/user/home";
        }

        return "lessons/new";
    }

    @PostMapping("/lessons")
    public String createLesson(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("image") String image,
            @RequestParam("cause") String cause,
            @RequestParam("improvement") String improvement,
            @RequestParam("practice") String practice,
            @RequestParam("category") String category,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 2) {
            return "redirect:/user/home";
        }

        Lesson lesson = new Lesson();

        lesson.setTitle(title);
        lesson.setContent(content);
        lesson.setImage(image);
        lesson.setCause(cause);
        lesson.setImprovement(improvement);
        lesson.setPractice(practice);
        lesson.setCategory(category);

        lessonMapper.insert(lesson);

        return "redirect:/pro/home";
    }

    @GetMapping("/lessons/{id}/edit")
    public String edit(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 2) {
            return "redirect:/user/home";
        }

        Lesson lesson = lessonMapper.findById(id);

        if (lesson == null) {
            return "redirect:/lessons";
        }

        model.addAttribute("lesson", lesson);

        return "lessons/edit";
    }

    @PostMapping("/lessons/{id}/edit")
    public String updateLesson(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam String image,
            @RequestParam String cause,
            @RequestParam String improvement,
            @RequestParam String practice,
            @RequestParam String category,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 2) {
            return "redirect:/user/home";
        }

        Lesson lesson = new Lesson();

        lesson.setId(id);
        lesson.setTitle(title);
        lesson.setContent(content);
        lesson.setImage(image);
        lesson.setCause(cause);
        lesson.setImprovement(improvement);
        lesson.setPractice(practice);
        lesson.setCategory(category);

        lessonMapper.update(lesson);

        return "redirect:/pro/home";
    }

    @PostMapping("/lessons/{id}/delete")
    public String deleteLesson(
            @PathVariable Long id,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 2) {
            return "redirect:/user/home";
        }

        lessonMapper.deleteById(id);

        return "redirect:/pro/home";
    }

    @GetMapping("/lessons/category/{category}")
    public String category(
            @PathVariable String category,
            Model model,
            HttpSession session
    ) {
        List<Lesson> lessons =
                lessonMapper.findByCategory(category);

        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        model.addAttribute("lessons", lessons);
        model.addAttribute("loginAccount", loginAccount);

        return "lessons/index";
    }

}