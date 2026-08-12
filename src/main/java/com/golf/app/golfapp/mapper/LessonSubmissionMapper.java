package com.golf.app.golfapp.mapper;

import com.golf.app.golfapp.model.LessonSubmission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LessonSubmissionMapper {

    List<LessonSubmission> findAll();

    List<LessonSubmission> findByProId(Long proId);

    List<LessonSubmission> findByUserId(Long userId);

    List<LessonSubmission> findByReservationId(Long reservationId);

    LessonSubmission findById(Long id);

    void insert(LessonSubmission lessonSubmission);

    void updateFeedback(LessonSubmission lessonSubmission);

    void deleteById(Long id);

    int countPendingByProId(Long id);

}