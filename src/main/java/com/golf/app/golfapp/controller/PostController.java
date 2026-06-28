package com.golf.app.golfapp.controller;

import com.golf.app.golfapp.mapper.AccountMapper;
import com.golf.app.golfapp.mapper.LessonMapper;
import com.golf.app.golfapp.mapper.PostMapper;
import com.golf.app.golfapp.mapper.ReservationMapper;
import com.golf.app.golfapp.model.Account;
import com.golf.app.golfapp.model.Lesson;
import com.golf.app.golfapp.model.Post;
import com.golf.app.golfapp.model.Reservation;
import com.golf.app.golfapp.service.CloudinaryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class PostController {

    private final PostMapper postMapper;
    private final LessonMapper lessonMapper;
    private final AccountMapper accountMapper;
    private final ReservationMapper reservationMapper;
    private final CloudinaryService cloudinaryService;

    public PostController(
            PostMapper postMapper,
            LessonMapper lessonMapper,
            AccountMapper accountMapper,
            ReservationMapper reservationMapper,
            CloudinaryService cloudinaryService
    ) {
        this.postMapper = postMapper;
        this.lessonMapper = lessonMapper;
        this.accountMapper = accountMapper;
        this.reservationMapper = reservationMapper;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/posts")
    public String index(
            Model model,
            HttpSession session
    ) {

        List<Post> posts = postMapper.findAll();

        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        model.addAttribute("posts", posts);
        model.addAttribute("postCount", posts.size());
        model.addAttribute("loginAccount", loginAccount);

        return "posts/index";
    }

    @GetMapping("/posts/{id}")
    public String detail(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {
        Post post = postMapper.findById(id);

        if (post == null) {
            return "redirect:/posts";
        }

        Account pro = accountMapper.findById(post.getProId());

        List<Lesson> lessons =
                lessonMapper.findByCategory(post.getCategory());

        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        model.addAttribute("post", post);
        model.addAttribute("pro", pro);
        model.addAttribute("lessons", lessons);
        model.addAttribute("loginAccount", loginAccount);

        return "posts/detail";
    }

    @GetMapping("/posts/new")
    public String newPost(HttpSession session) {

        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 2) {
            return "redirect:/user/home";
        }

        return "posts/new";
    }

    @PostMapping("/posts")
    public String createPost(
            String title,
            String content,
            MultipartFile imageFile,
            String cause,
            String improvement,
            String practice,
            String category,
            HttpSession session
    ) throws Exception {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 2) {
            return "redirect:/user/home";
        }

        String imageUrl = null;

        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl =
                    cloudinaryService.uploadFile(imageFile);

        }

        Post post = new Post();

        post.setProId(loginAccount.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setImage(imageUrl);
        post.setCause(cause);
        post.setImprovement(improvement);
        post.setPractice(practice);
        post.setCategory(category);

        postMapper.insert(post);

        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}/edit")
    public String editPost(
            @PathVariable Long id,
            Model model,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 2) {
            return "redirect:/user/home";
        }

        Post post = postMapper.findById(id);

        if (post == null) {
            return "redirect:/posts";
        }

        if (!post.getProId().equals(loginAccount.getId())) {
            return "redirect:/posts";
        }

        model.addAttribute("post", post);

        return "posts/edit";
    }

    @PostMapping("/posts/{id}/update")
    public String updatePost(
            @PathVariable Long id,
            String title,
            String content,
            MultipartFile imageFile,
            String cause,
            String improvement,
            String practice,
            String category,
            HttpSession session
    ) throws Exception {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 2) {
            return "redirect:/user/home";
        }

        Post oldPost = postMapper.findById(id);

        if (oldPost == null) {
            return "redirect:/posts";
        }

        if (!oldPost.getProId().equals(loginAccount.getId())) {
            return "redirect:/posts";
        }

        String imageUrl = oldPost.getImage();

        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl =
                    cloudinaryService.uploadFile(imageFile);
        }

        Post post = new Post();

        post.setId(id);
        post.setProId(oldPost.getProId());
        post.setTitle(title);
        post.setContent(content);
        post.setImage(imageUrl);
        post.setCause(cause);
        post.setImprovement(improvement);
        post.setPractice(practice);
        post.setCategory(category);

        postMapper.update(post);

        return "redirect:/posts";
    }

    @PostMapping("/lessons/{id}/reserve")
    public String reserveLesson(
            @PathVariable Long id,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        Reservation reservation = new Reservation();

        reservation.setLessonId(id);
        reservation.setUserId(loginAccount.getId());
        reservation.setStatus(1);

        reservationMapper.save(reservation);

        return "redirect:/user/home";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(
            @PathVariable Long id,
            HttpSession session
    ) {
        Account loginAccount =
                (Account) session.getAttribute("loginAccount");

        if (loginAccount == null) {
            return "redirect:/login";
        }

        if (loginAccount.getRole() != 2) {
            return "redirect:/user/home";
        }

        Post post = postMapper.findById(id);

        if (post == null) {
            return "redirect:/posts";
        }

        if (!post.getProId().equals(loginAccount.getId())) {
            return "redirect:/posts";
        }

        postMapper.deleteById(id);

        return "redirect:/posts";
    }
}