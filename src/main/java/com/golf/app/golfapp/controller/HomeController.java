package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.model.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/user/home")
    public String userHome(
            HttpSession session,
            Model model
    ) {

        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        model.addAttribute("account", loginAccount);

        return "user/home";
    }

}