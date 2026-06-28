package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.AccountMapper;
import com.golf.app.golfapp.model.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminController {

    private final AccountMapper accountMapper;

    public AdminController(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @GetMapping("/admin/home")
    public String home(
            HttpSession session,
            Model model
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 9) {
            return "redirect:/user/home";
        }

        model.addAttribute(
                "accounts",
                accountMapper.findAll()
        );

        return "admin/home";
    }

    @PostMapping("/admin/accounts/{id}/delete")
    public String deleteAccount(
            @PathVariable Long id,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 9) {
            return "redirect:/user/home";
        }

        Account targetAccount = accountMapper.findById(id);

        if (targetAccount == null) {
            return "redirect:/admin/home";
        }

        if (targetAccount.getRole() == 9) {
            return "redirect:/admin/home";
        }

        accountMapper.deleteById(id);

        return "redirect:/admin/home";
    }
}