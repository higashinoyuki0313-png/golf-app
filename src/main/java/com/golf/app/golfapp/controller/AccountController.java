package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.AccountMapper;
import com.golf.app.golfapp.model.Account;

import jakarta.servlet.http.HttpSession;
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

    /**
     * 他ユーザー(主に担当プロ)のプロフィール表示。
     *
     * <p>専用テンプレート accounts/profile は存在しないため、自分のプロフィール表示と
     * 同じ profile/{pro,user}/show を使う。これらはメールアドレス等を表示せず、
     * 名前・画像・自己紹介・ベストスコア・愛用クラブのみを出す。
     */
    @GetMapping("/accounts/{id}")
    public String profile(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        Account account = accountMapper.findById(id);

        if (account == null) {
            return "redirect:/lessons";
        }

        model.addAttribute("account", account);

        if (account.getRole() == 2) {
            return "profile/pro/show";
        }

        return "profile/user/show";
    }

}
