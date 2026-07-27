package com.golf.app.golfapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LessonSubmission {

    private Long id;

    private Long reservationId;
    private Long lessonId;
    private Long userId;

    private String userName;
    private String lessonTitle;

    private String comment;
    private String videoUrl;

    private String feedback;
    private String feedbackVideoUrl;

    private Integer status;
}