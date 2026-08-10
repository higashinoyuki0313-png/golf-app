package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.LessonMapper;
import com.golf.app.golfapp.model.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProController {

    private final LessonMapper lessonMapper;

    public ProController(LessonMapper lessonMapper) {
        this.lessonMapper = lessonMapper;
    }

    @GetMapping("/pro/home")
    public String home(
            HttpSession session,
            Model model
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 2) {
            return "redirect:/user/home";
        }

        int lessonCount =
                lessonMapper.countByProId(loginAccount.getId());

        model.addAttribute("lessonCount", lessonCount);
        model.addAttribute("loginAccount", loginAccount);

        return "pro/home";
    }
}