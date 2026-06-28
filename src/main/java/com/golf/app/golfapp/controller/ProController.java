package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.model.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProController {

    @GetMapping("/pro/home")
    public String home(HttpSession session) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 2) {
            return "redirect:/user/home";
        }

        return "pro/home";
    }
}