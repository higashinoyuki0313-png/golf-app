package com.golf.app.golfapp.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Reservation {

    private Long id;

    private Long lessonId;

    private Long userId;

    private Integer status;

    private String userName;

    private String lessonTitle;

    private String proName;

    private Integer submissionCount;

    private Integer completedSubmissionCount;

    private Integer submissionStatus;

    private List<LessonSubmission> submissions;
}