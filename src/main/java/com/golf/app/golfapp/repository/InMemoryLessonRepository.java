package com.golf.app.golfapp.repository;

import com.golf.app.golfapp.model.Lesson;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryLessonRepository implements LessonRepository{

    private final List<Lesson> lessons = new ArrayList<>();

    public InMemoryLessonRepository(){

        Lesson lesson1 = new Lesson();
        lesson1.setId(1L);
        lesson1.setTitle("飛距離アップレッスン");
        lesson1.setContent("飛距離を伸ばしたい方向けのレッスンです");
        lesson1.setCategory("distance");
        lesson1.setProName("東野");

        Lesson lesson2 = new Lesson();
        lesson2.setId(2L);
        lesson2.setTitle("スライス改善レッスン");
        lesson2.setContent("右に曲がるミスを改善するレッスンです");
        lesson2.setCategory("slice");
        lesson2.setProName("東野");

        Lesson lesson3 = new Lesson();
        lesson3.setId(3L);
        lesson3.setTitle("フック改善レッスン");
        lesson3.setContent("左に曲がるミスを改善するレッスンです");
        lesson3.setCategory("hook");
        lesson3.setProName("東野");

        Lesson lesson4 = new Lesson();
        lesson4.setId(4L);
        lesson4.setTitle("ミート率アップレッスン");
        lesson4.setContent("芯でボールを捉えるためのレッスンです");
        lesson4.setCategory("contact");
        lesson4.setProName("東野");

        Lesson lesson5 = new Lesson();
        lesson5.setId(5L);
        lesson5.setTitle("ショートゲームレッスン");
        lesson5.setContent("アプローチとパターを改善するレッスンです");
        lesson5.setCategory("shortgame");
        lesson5.setProName("東野");

        lessons.add(lesson1);
        lessons.add(lesson2);
        lessons.add(lesson3);
        lessons.add(lesson4);
        lessons.add(lesson5);



    }

    @Override
    public List<Lesson> findAll() {
        return lessons;
    }

    @Override
    public Optional<Lesson> findById(Long id) {
        return lessons.stream()
                .filter(lesson -> lesson.getId().equals(id))
                .findFirst();
    }

    @Override
    public void save(Lesson lesson) {
        lessons.add(lesson);

    }

    @Override
    public void update(Lesson lesson) {

        findById(lesson.getId()).ifPresent(existingLesson -> {
            existingLesson.setTitle(lesson.getTitle());
            existingLesson.setContent(lesson.getContent());
            existingLesson.setCategory(lesson.getCategory());
        });

    }

    @Override
    public void deleteById(Long id) {
        lessons.removeIf(lesson -> lesson.getId().equals(id));

    }

    @Override
    public List<Lesson> findByCategory(String category) {

        return lessons.stream()
                .filter(lesson -> lesson.getCategory().equals(category))
                .toList();
    }

}
