package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.LessonSubmissionMapper;
import com.golf.app.golfapp.mapper.ReservationMapper;
import com.golf.app.golfapp.model.Account;
import com.golf.app.golfapp.model.LessonSubmission;
import com.golf.app.golfapp.model.Reservation;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LessonSubmissionController {

    private final LessonSubmissionMapper lessonSubmissionMapper;
    private final ReservationMapper reservationMapper;

    public LessonSubmissionController(
            LessonSubmissionMapper lessonSubmissionMapper,
            ReservationMapper reservationMapper
    ) {
        this.lessonSubmissionMapper = lessonSubmissionMapper;
        this.reservationMapper = reservationMapper;
    }

    @GetMapping("/lessons/{id}/submit")
    public String submitForm(
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
            return "redirect:/lessons";
        }

        boolean applied = false;

        for (Reservation reservation :
                reservationMapper.findByLessonId(id)) {

            if (reservation.getUserId().equals(loginAccount.getId())) {
                applied = true;
                break;
            }
        }

        if (!applied) {
            return "redirect:/lessons";
        }

        boolean submitted = false;

        for (LessonSubmission submission :
                lessonSubmissionMapper.findAll()) {

            if (
                    submission.getLessonId().equals(id)
                            &&
                            submission.getUserId().equals(loginAccount.getId())
            ) {
                submitted = true;
                break;
            }
        }

        if (submitted) {
            return "redirect:/lessons";
        }

        model.addAttribute("lessonId", id);

        return "submissions/new";
    }

    @PostMapping("/lessons/{id}/submit")
    public String submitLesson(
            @PathVariable Long id,
            @RequestParam String comment,
            @RequestParam String videoUrl,
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

        LessonSubmission submission = new LessonSubmission();

        submission.setLessonId(id);
        submission.setUserId(loginAccount.getId());
        submission.setComment(comment);
        submission.setVideoUrl(videoUrl);
        submission.setStatus(1);

        lessonSubmissionMapper.insert(submission);

        return "redirect:/submissions";
    }

    @GetMapping("/submissions")
    public String index(
            Model model,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        model.addAttribute("loginAccount", loginAccount);

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() == 2) {
            model.addAttribute(
                    "submissions",
                    lessonSubmissionMapper.findAll()
            );

            return "submissions/index";
        }

        List<LessonSubmission> mySubmissions =
                new ArrayList<>();

        for (LessonSubmission submission :
                lessonSubmissionMapper.findAll()) {

            if (submission.getUserId().equals(loginAccount.getId())) {
                mySubmissions.add(submission);
            }
        }

        model.addAttribute("submissions", mySubmissions);

        return "submissions/index";
    }

    @GetMapping("/submissions/{id}")
    public String detail(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        LessonSubmission submission =
                lessonSubmissionMapper.findById(id);

        if (submission == null) {
            return "redirect:/submissions";
        }

        if (
                loginAccount.getRole() != 2
                        &&
                        !submission.getUserId().equals(loginAccount.getId())
        ) {
            return "redirect:/submissions";
        }

        model.addAttribute("submission", submission);
        model.addAttribute("loginAccount", loginAccount);

        return "submissions/detail";
    }

    @GetMapping("/submissions/{id}/feedback")
    public String feedbackForm(
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

        LessonSubmission submission =
                lessonSubmissionMapper.findById(id);

        if (submission == null) {
            return "redirect:/submissions";
        }

        model.addAttribute("submission", submission);

        return "submissions/feedback";
    }

    @PostMapping("/submissions/{id}/feedback")
    public String saveFeedback(
            @PathVariable Long id,
            @RequestParam String feedback,
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

        LessonSubmission submission =
                lessonSubmissionMapper.findById(id);

        if (submission == null) {
            return "redirect:/submissions";
        }

        submission.setFeedback(feedback);
        submission.setStatus(3);

        lessonSubmissionMapper.updateFeedback(submission);

        return "redirect:/submissions";
    }
}