package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.LessonMapper;
import com.golf.app.golfapp.mapper.LessonSubmissionMapper;
import com.golf.app.golfapp.mapper.ReservationMapper;
import com.golf.app.golfapp.model.Account;
import com.golf.app.golfapp.model.Lesson;
import com.golf.app.golfapp.model.LessonSubmission;
import com.golf.app.golfapp.model.Reservation;
import com.golf.app.golfapp.service.CloudinaryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class LessonSubmissionController {

    private final LessonSubmissionMapper lessonSubmissionMapper;
    private final ReservationMapper reservationMapper;
    private final LessonMapper lessonMapper;
    private final CloudinaryService cloudinaryService;

    public LessonSubmissionController(
            LessonSubmissionMapper lessonSubmissionMapper,
            ReservationMapper reservationMapper,
            LessonMapper lessonMapper,
            CloudinaryService cloudinaryService
    ) {
        this.lessonSubmissionMapper = lessonSubmissionMapper;
        this.reservationMapper = reservationMapper;
        this.lessonMapper = lessonMapper;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/submissions/new/{reservationId}")
    public String newSubmission(
            @PathVariable Long reservationId,
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

        Reservation reservation =
                reservationMapper.findById(reservationId);

        if (reservation == null) {
            return "redirect:/my-lessons";
        }

        if (!reservation.getUserId().equals(loginAccount.getId())) {
            return "redirect:/my-lessons";
        }

        model.addAttribute("reservation", reservation);
        model.addAttribute("loginAccount", loginAccount);

        return "submissions/new";
    }

    @PostMapping("/submissions/new/{reservationId}")
    public String createSubmission(
            @PathVariable Long reservationId,
            @RequestParam(required = false) String comment,
            @RequestParam(value = "video", required = false) MultipartFile video,
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

        Reservation reservation =
                reservationMapper.findById(reservationId);

        if (reservation == null) {
            return "redirect:/my-lessons";
        }

        if (!reservation.getUserId().equals(loginAccount.getId())) {
            return "redirect:/my-lessons";
        }

        model.addAttribute("reservation", reservation);
        model.addAttribute("loginAccount", loginAccount);
        model.addAttribute("comment", comment);

        if (comment == null || comment.trim().isEmpty()) {
            model.addAttribute(
                    "errorMessage",
                    "コメントを入力してください。"
            );
            return "submissions/new";
        }

        if (comment.length() > 500) {
            model.addAttribute(
                    "errorMessage",
                    "コメントは500文字以内で入力してください。"
            );
            return "submissions/new";
        }

        if (video == null || video.isEmpty()) {
            model.addAttribute(
                    "errorMessage",
                    "提出する動画を選択してください。"
            );
            return "submissions/new";
        }

        String contentType = video.getContentType();

        if (contentType == null || !contentType.startsWith("video/")) {
            model.addAttribute(
                    "errorMessage",
                    "動画ファイルを選択してください。"
            );
            return "submissions/new";
        }

        long maxFileSize = 100L * 1024 * 1024;

        if (video.getSize() > maxFileSize) {
            model.addAttribute(
                    "errorMessage",
                    "動画のファイルサイズは100MB以内にしてください。"
            );
            return "submissions/new";
        }

        String videoUrl;

        try {
            videoUrl = cloudinaryService.uploadVideo(video);
        } catch (IOException e) {
            model.addAttribute(
                    "errorMessage",
                    "動画のアップロードに失敗しました。もう一度お試しください。"
            );

            return "submissions/new";
        }

        LessonSubmission submission = new LessonSubmission();

        submission.setReservationId(reservation.getId());
        submission.setLessonId(reservation.getLessonId());
        submission.setUserId(loginAccount.getId());
        submission.setComment(comment.trim());
        submission.setVideoUrl(videoUrl);
        submission.setStatus(1);

        lessonSubmissionMapper.insert(submission);

        return "redirect:/lessons/complete";
    }

    @GetMapping("/submissions")
    public String index(
            Model model,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        model.addAttribute("loginAccount", loginAccount);

        if (loginAccount.getRole() == 2) {
            List<LessonSubmission> submissions =
                    lessonSubmissionMapper.findByProId(loginAccount.getId());

            model.addAttribute("submissions", submissions);

            return "submissions/index";
        }

        model.addAttribute(
                "submissions",
                lessonSubmissionMapper.findByUserId(loginAccount.getId())
        );

        return "submissions/index";
    }

    @GetMapping("/submissions/reservation/{reservationId}")
    public String reservationSubmissions(
            @PathVariable Long reservationId,
            Model model,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        Reservation reservation =
                reservationMapper.findById(reservationId);

        if (reservation == null) {
            return "redirect:/my-lessons";
        }

        if (loginAccount.getRole() == 1
                && !reservation.getUserId().equals(loginAccount.getId())) {
            return "redirect:/my-lessons";
        }

        List<LessonSubmission> submissions =
                lessonSubmissionMapper.findByReservationId(reservationId);

        model.addAttribute("submissions", submissions);
        model.addAttribute("loginAccount", loginAccount);

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
            return redirectByRole(loginAccount);
        }

        if (loginAccount.getRole() == 1
                && !submission.getUserId().equals(loginAccount.getId())) {
            return "redirect:/my-lessons";
        }

        if (loginAccount.getRole() == 2) {
            Lesson lesson =
                    lessonMapper.findById(submission.getLessonId());

            if (lesson == null
                    || !lesson.getProId().equals(loginAccount.getId())) {
                return "redirect:/submissions";
            }
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
            return "redirect:/my-lessons";
        }

        LessonSubmission submission =
                lessonSubmissionMapper.findById(id);

        if (submission == null) {
            return "redirect:/submissions";
        }

        Lesson lesson =
                lessonMapper.findById(submission.getLessonId());

        if (lesson == null) {
            return "redirect:/submissions";
        }

        if (!lesson.getProId().equals(loginAccount.getId())) {
            return "redirect:/submissions";
        }

        model.addAttribute("submission", submission);
        model.addAttribute("loginAccount", loginAccount);

        return "submissions/feedback";
    }

    @PostMapping("/submissions/{id}/feedback")
    public String saveFeedback(
            @PathVariable Long id,
            @RequestParam String feedback,
            @RequestParam(required = false) String feedbackVideoUrl,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 2) {
            return "redirect:/my-lessons";
        }

        LessonSubmission submission =
                lessonSubmissionMapper.findById(id);

        if (submission == null) {
            return "redirect:/submissions";
        }

        Lesson lesson =
                lessonMapper.findById(submission.getLessonId());

        if (lesson == null) {
            return "redirect:/submissions";
        }

        if (!lesson.getProId().equals(loginAccount.getId())) {
            return "redirect:/submissions";
        }

        submission.setFeedback(feedback);
        submission.setFeedbackVideoUrl(feedbackVideoUrl);
        submission.setStatus(3);

        lessonSubmissionMapper.updateFeedback(submission);

        return "redirect:/submissions";
    }

    @PostMapping("/submissions/{id}/delete")
    public String deleteSubmission(
            @PathVariable Long id,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 1) {
            return "redirect:/submissions";
        }

        LessonSubmission submission =
                lessonSubmissionMapper.findById(id);

        if (submission == null) {
            return "redirect:/my-lessons";
        }

        if (!submission.getUserId().equals(loginAccount.getId())) {
            return "redirect:/my-lessons";
        }

        if (!Integer.valueOf(1).equals(submission.getStatus())) {
            return "redirect:/submissions/reservation/"
                    + submission.getReservationId();
        }

        lessonSubmissionMapper.deleteById(id);

        return "redirect:/my-lessons";
    }

    private String redirectByRole(Account loginAccount) {
        if (loginAccount.getRole() == 2) {
            return "redirect:/submissions";
        }

        return "redirect:/my-lessons";
    }
}