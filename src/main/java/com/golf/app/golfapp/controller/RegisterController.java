package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.AccountMapper;
import com.golf.app.golfapp.model.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    private final AccountMapper accountMapper;

    public RegisterController(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            String name,
            String email,
            String password,
            Model model
    ) {
        Account exists =
                accountMapper.findByEmail(email);

        if (exists != null) {
            model.addAttribute(
                    "error",
                    "このメールアドレスは既に登録されています。"
            );

            model.addAttribute("name",name);
            model.addAttribute("email",email);

            return "register";
        }

        Account account = new Account();

        account.setName(name);
        account.setEmail(email);
        account.setPassword(password);
        account.setRole(1);

        accountMapper.insert(account);

        return "redirect:/login";
    }
}