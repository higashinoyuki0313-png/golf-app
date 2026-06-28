package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.AccountMapper;
import com.golf.app.golfapp.model.Account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AccountController {

    private final AccountMapper accountMapper;

    public AccountController(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @GetMapping("/accounts/{id}")
    public String profile(
            @PathVariable Long id,
            Model model
    ) {

        Account account = accountMapper.findById(id);

        if (account == null) {
            return "redirect:/login";
        }

        model.addAttribute("account", account);

        return "accounts/profile";
    }

}
