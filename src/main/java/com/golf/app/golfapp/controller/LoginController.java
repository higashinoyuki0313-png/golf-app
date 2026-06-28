package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.AccountMapper;
import com.golf.app.golfapp.model.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    private final AccountMapper accountMapper;

    public LoginController(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session
    ) {

        Account account = accountMapper.findByEmail(email);

        if (account == null) {
            return "login";
        }

        if (account.getPassword().equals(password)) {

            session.setAttribute("loginAccount", account);

            if (account.getRole() == 1) {
                return "redirect:/user/home";
            }

            if (account.getRole() == 2) {
                return "redirect:/pro/home";
            }

            if (account.getRole() == 9) {
                return "redirect:/admin/home";
            }
        }

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}