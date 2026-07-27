package com.golf.app.golfapp.model;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@Setter
public class Lesson {

    private Long id;

    private Long proId;

    @NotBlank(message = "タイトルを入力してください")
    @Size(max = 100, message = "タイトルは100文字以内で入力してください")
    private String title;

    @NotBlank(message = "レッスン内容を入力してください")
    private String content;

    private String image;

    private String video;

    @Size(max = 1000, message = "原因は1000文字以内で入力してください")
    private String cause;

    @Size(max = 1000, message = "改善ポイントは1000文字以内で入力してください")
    private String improvement;

    @Size(max = 1000, message = "練習方法を1000文字以内で入力してください")
    private String practice;

    @NotBlank(message = "カテゴリを選択してください")
    private String category;

    private String proName;
}