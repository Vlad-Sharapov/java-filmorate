package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MpaController {

    private final MpaStorage mpaStorage;

    @GetMapping("/mpa")
    public List<Mpa> mpas() {
        return mpaStorage.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa mpa(@PathVariable Integer id) {
        return mpaStorage.getMpa(id);
    }

}
