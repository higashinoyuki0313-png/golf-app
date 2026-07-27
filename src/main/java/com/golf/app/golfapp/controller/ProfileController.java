package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.AccountMapper;
import com.golf.app.golfapp.model.Account;
import com.golf.app.golfapp.service.CloudinaryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class ProfileController {

    private final AccountMapper accountMapper;
    private final CloudinaryService cloudinaryService;

    public ProfileController(
            AccountMapper accountMapper,
            CloudinaryService cloudinaryService
    ) {
        this.accountMapper = accountMapper;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/profile")
    public String showProfile(
            HttpSession session,
            Model model
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        Account account =
                accountMapper.findById(loginAccount.getId());

        model.addAttribute("account", account);

        if (account.getRole() == 2) {
            return "profile/pro/show";
        }

        return "profile/user/show";
    }

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

        Account account =
                accountMapper.findById(loginAccount.getId());

        model.addAttribute("account", account);

        if (account.getRole() == 2) {
            return "profile/pro/edit";
        }

        return "profile/user/edit";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(
            HttpSession session,
            Model model,
            @RequestParam("name") String name,
            @RequestParam("profile") String profile,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "favoriteClub", required = false) String favoriteClub,
            @RequestParam(value = "bestScore", required = false) String bestScore,
            @RequestParam(value = "sns", required = false) String sns
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
        account.setFavoriteClub(favoriteClub);

        if (loginAccount.getRole() == 2) {
            account.setSns(sns);
        }

        // 名前チェック
        if (name == null || name.isBlank()) {
            return showError(model, account, loginAccount,
                    "名前を入力してください");
        }

        if (name.length() > 50) {
            return showError(model, account, loginAccount,
                    "名前は50文字以内で入力してください");
        }

        // プロフィールチェック
        if (profile != null && profile.length() > 500) {
            return showError(model, account, loginAccount,
                    "プロフィールは500文字以内で入力してください");
        }

        // ベストスコア
        Integer bestScoreValue = null;

        if (bestScore != null && !bestScore.isBlank()) {
            bestScoreValue = Integer.parseInt(bestScore);
        }

        if (bestScoreValue != null &&
                (bestScoreValue < 0 || bestScoreValue > 200)) {

            return showError(model, account, loginAccount,
                    "ベストスコアは0～200で入力してください");
        }

        // 画像アップロード
        String imageUrl = loginAccount.getImage();

        if (image != null && !image.isEmpty()) {

            String contentType = image.getContentType();

            if (contentType == null || !contentType.startsWith("image/")) {
                return showError(model, account, loginAccount,
                        "画像ファイルを選択してください");
            }

            try {
                imageUrl = cloudinaryService.uploadFile(image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        account.setImage(imageUrl);
        account.setBestScore(bestScoreValue);

        // 更新
        if (loginAccount.getRole() == 2) {
            accountMapper.updateProProfile(account);
        } else {
            accountMapper.updateUserProfile(account);
        }

        Account updatedAccount =
                accountMapper.findById(loginAccount.getId());

        session.setAttribute("loginAccount", updatedAccount);

        return "redirect:/profile";
    }

    private String showError(
            Model model,
            Account account,
            Account loginAccount,
            String message
    ) {
        model.addAttribute("errorMessage", message);
        model.addAttribute("account", account);

        if (loginAccount.getRole() == 2) {
            return "profile/pro/edit";
        }

        return "profile/user/edit";
    }

}