package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.model.Account;
import com.golf.app.golfapp.repository.InMemoryAccountRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AccountController {

    private final InMemoryAccountRepository accountRepository =
            new InMemoryAccountRepository();

    @GetMapping("/accounts/{id}")
    public String profile(
            @PathVariable Long id,
            Model model
    ) {

        Account account =
                accountRepository.findById(id)
                        .orElseThrow();

        model.addAttribute("account", account);

        return "accounts/profile";
    }

}
