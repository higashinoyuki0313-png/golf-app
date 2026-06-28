package com.golf.app.golfapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Reservation {

    private Long id;

    private Long lessonId;

    private Long userId;

    private Integer status;
}