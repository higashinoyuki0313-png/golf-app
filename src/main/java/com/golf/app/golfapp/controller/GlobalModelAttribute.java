package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.model.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttribute {

    @ModelAttribute("loginAccount")
    public Account loginAccount(HttpSession session) {
        return (Account) session.getAttribute("loginAccount");
    }
}