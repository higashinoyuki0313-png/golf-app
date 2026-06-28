package com.golf.app.golfapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Lesson {

    private Long id;

    private String title;

    private String content;

    private String image;

    private String cause;

    private String improvement;

    private String practice;

    private String category;

    private String proName;
}