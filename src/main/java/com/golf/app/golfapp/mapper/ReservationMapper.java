package com.golf.app.golfapp.mapper;

import com.golf.app.golfapp.model.Reservation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationMapper {

    List<Reservation> findAll();

    Reservation findById(Long id);

    List<Reservation> findByLessonId(Long lessonId);

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findMyLessons(Long userId);

    void save(Reservation reservation);

    void updateStatus(
            @Param("id") Long id,
            @Param("status") Integer status
    );

    Reservation findByLessonIdAndUserId(
            @Param("lessonId") Long lessonId,
            @Param("userId") Long userId
    );
}