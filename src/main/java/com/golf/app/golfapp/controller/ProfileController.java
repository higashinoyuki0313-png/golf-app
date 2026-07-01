package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.AccountMapper;
import com.golf.app.golfapp.model.Account;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

    private final AccountMapper accountMapper;

    public ProfileController(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    // プロフィール編集画面を表示
    @GetMapping("/profile/edit")
    public String editProfile(
            HttpSession session,
            Model model
    ) {

        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        model.addAttribute("account", loginAccount);

        return "profile/edit";
    }

    // プロフィール更新
    @PostMapping("/profile/edit")
    public String updateProfile(
            HttpSession session,
            @RequestParam("name") String name,
            @RequestParam("profile") String profile,
            @RequestParam("specialty") String specialty,
            @RequestParam("bestScore") Integer bestScore,
            @RequestParam("sns") String sns
    ) {

        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        Account account = new Account();

        account.setId(loginAccount.getId());
        account.setName(name);
        account.setProfile(profile);
        account.setSpecialty(specialty);
        account.setBestScore(bestScore);
        account.setSns(sns);

        accountMapper.updateProfile(account);

        return "redirect:/profile";
    }
}