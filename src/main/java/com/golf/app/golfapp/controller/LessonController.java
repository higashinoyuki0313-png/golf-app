package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.LessonMapper;
import com.golf.app.golfapp.mapper.ReservationMapper;
import com.golf.app.golfapp.model.Account;
import com.golf.app.golfapp.model.Lesson;
import com.golf.app.golfapp.model.Reservation;
import com.golf.app.golfapp.service.CloudinaryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.util.List;

@Controller
public class LessonController {

    private final LessonMapper lessonMapper;
    private final ReservationMapper reservationMapper;
    private final CloudinaryService cloudinaryService;
    private Account getLoginAccount(HttpSession session) {
        return (Account) session.getAttribute("loginAccount");
    }

    private boolean isPro(Account account) {
        return account.getRole() == 2;
    }

    private boolean canEditLesson(Lesson lesson, Account account) {
        return lesson != null
                && lesson.getProId().equals(account.getId());
    }

    public LessonController(
            LessonMapper lessonMapper,
            ReservationMapper reservationMapper,
            CloudinaryService cloudinaryService
    ) {
        this.lessonMapper = lessonMapper;
        this.reservationMapper = reservationMapper;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/lessons")
    public String index(
            Model model,
            HttpSession session
    ) {

        Account loginAccount = getLoginAccount(session);

        if (loginAccount == null) {
            return "redirect:/login";
        }

        List<Lesson> lessons = lessonMapper.findAll();

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
        Account loginAccount = getLoginAccount(session);

        if(loginAccount == null) {
            return "redirect:/login";
        }

        Lesson lesson = lessonMapper.findById(id);

        if (lesson == null) {
            if (isPro(loginAccount)) {
                return "redirect:/pro/lessons";
            }

            return "redirect:/lessons";
        }

        Reservation myReservation = null;

        if (loginAccount.getRole() == 1) {
            myReservation =
                    reservationMapper.findByLessonIdAndUserId(
                            id,
                            loginAccount.getId()
                    );
        }

        model.addAttribute("lesson", lesson);
        model.addAttribute("loginAccount", loginAccount);
        model.addAttribute("myReservation", myReservation);
        model.addAttribute(
                "reservationCount",
                reservationMapper.findByLessonId(id).size()
        );

        if (loginAccount.getRole() == 2) {
            model.addAttribute(
                    "reservations",
                    reservationMapper.findByLessonId(id)
            );
        }

        return "lessons/detail";
    }

    @GetMapping("/lessons/new")
    public String newLesson(
            HttpSession session,
            Model model
    ) {

        Account loginAccount = getLoginAccount(session);

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (!isPro(loginAccount)) {
            return "redirect:/user/home";
        }

        model.addAttribute("lesson", new Lesson());

        return "lessons/new";
    }

    @PostMapping("/lessons")
    public String createLesson(
            @Valid @ModelAttribute("lesson") Lesson lesson,
            BindingResult bindingResult,
            @RequestParam(value = "media", required = false) MultipartFile media,
            HttpSession session,
            Model model
    ){

        Account loginAccount = getLoginAccount(session);

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (!isPro(loginAccount)) {
            return "redirect:/user/home";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("loginAccount", loginAccount);
            return "lessons/new";
        }

        String imageUrl = null;
        String videoUrl = null;

        if (media != null && !media.isEmpty()) {
            String contentType = media.getContentType();

            if (contentType == null
                    || (!contentType.startsWith("image/")
                    && !contentType.startsWith("video/"))) {


                bindingResult.reject(
                        "media.invalid",
                        "画像または動画を選択してください。"
                );

                model.addAttribute("loginAccount", loginAccount);
                return "lessons/new";
            }

            try {
                String url = cloudinaryService.uploadFile(media);

                if (contentType.startsWith("image/")) {
                    imageUrl = url;
                } else {
                    videoUrl = url;
                }

            } catch (IOException e) {
                bindingResult.reject(
                        "media.upload",
                        "ファイルのアップロードに失敗しました。"
                );

                model.addAttribute("loginAccount", loginAccount);
                return "lessons/new";
            }
        }

        lesson.setProId(loginAccount.getId());
        lesson.setImage(imageUrl);
        lesson.setVideo(videoUrl);

        lessonMapper.insert(lesson);

        return "redirect:/pro/lessons";
    }

    @GetMapping("/pro/lessons")
    public String proLessons(
            Model model,
            HttpSession session
    ) {
        Account loginAccount = getLoginAccount(session);

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (!isPro(loginAccount)) {
            return "redirect:/user/home";
        }

        List<Lesson> lessons =
                lessonMapper.findByProId(loginAccount.getId());

        model.addAttribute("lessons", lessons);
        model.addAttribute("loginAccount", loginAccount);

        return "pro/lessons";
    }

    @GetMapping("/lessons/{id}/edit")
    public String edit(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {

        Account loginAccount = getLoginAccount(session);

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (!isPro(loginAccount)) {
            return "redirect:/user/home";
        }

        Lesson lesson = lessonMapper.findById(id);

        if (lesson == null) {
            return "redirect:/pro/lessons";
        }

        if (!lesson.getProId().equals(loginAccount.getId())) {
            return "redirect:/pro/lessons";
        }

        model.addAttribute("lesson", lesson);

        return "lessons/edit";
    }

    @PostMapping("/lessons/{id}/edit")
    public String updateLesson(
            @PathVariable Long id,
            @Valid @ModelAttribute("lesson") Lesson lesson,
            BindingResult bindingResult,
            @RequestParam(value = "media", required = false) MultipartFile media,
            HttpSession session,
            Model model
    ) {

        Account loginAccount = getLoginAccount(session);

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (!isPro(loginAccount)) {
            return "redirect:/user/home";
        }

        Lesson existingLesson = lessonMapper.findById(id);

        if (!canEditLesson(existingLesson, loginAccount)) {
            return "redirect:/pro/lessons";
        }

        lesson.setId(id);
        lesson.setProId(loginAccount.getId());
        lesson.setImage(existingLesson.getImage());
        lesson.setVideo(existingLesson.getVideo());

        if (bindingResult.hasErrors()) {
            model.addAttribute("loginAccount", loginAccount);
            model.addAttribute("lesson", lesson);
            return "lessons/edit";
        }

        String imageUrl = existingLesson.getImage();
        String videoUrl = existingLesson.getVideo();

        if (media != null && !media.isEmpty()) {
            String contentType = media.getContentType();

            if (contentType == null
                    || (!contentType.startsWith("image/")
                    && !contentType.startsWith("video/"))) {

                bindingResult.reject(
                        "media.invalid",
                        "画像または動画を選択してください。"
                );

                model.addAttribute("loginAccount", loginAccount);
                return "lessons/edit";
            }

            try {
                String url = cloudinaryService.uploadFile(media);

                if (contentType.startsWith("image/")) {
                    imageUrl = url;
                    videoUrl = null;
                } else {
                    videoUrl = url;
                    imageUrl = null;
                }

            } catch (IOException e) {
                bindingResult.reject(
                        "media.upload",
                        "ファイルのアップロードに失敗しました。"
                );

                model.addAttribute("loginAccount", loginAccount);
                return "lessons/edit";
            }
        }

        lesson.setImage(imageUrl);
        lesson.setVideo(videoUrl);

        lessonMapper.update(lesson);

        return "redirect:/pro/lessons";
    }

    @PostMapping("/lessons/{id}/delete")
    public String deleteLesson(
            @PathVariable Long id,
            HttpSession session
    ) {

        Account loginAccount = getLoginAccount(session);

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (!isPro(loginAccount)) {
            return "redirect:/user/home";
        }

        Lesson existingLesson = lessonMapper.findById(id);

        if (!canEditLesson(existingLesson, loginAccount)) {
            return "redirect:/pro/lessons";
        }

        lessonMapper.deleteById(id);

        return "redirect:/pro/lessons";
    }

    @GetMapping("/lessons/category/{category}")
    public String category(
            @PathVariable String category,
            Model model,
            HttpSession session
    ) {

        Account loginAccount = getLoginAccount(session);

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 1) {
            return "redirect:/pro/lessons";
        }

        List<Lesson> lessons =
                lessonMapper.findByCategory(category);

        model.addAttribute("lessons", lessons);
        model.addAttribute("loginAccount", loginAccount);
        model.addAttribute("category", category);

        return "lessons/index";
    }

    @GetMapping("/lessons/complete")
    public String complete(
            HttpSession session,
            Model model
    ) {
        Account loginAccount = getLoginAccount(session);

        if (loginAccount == null) {
            return "redirect:/login";
        }

        model.addAttribute("loginAccount", loginAccount);

        return "lessons/complete";
    }
}