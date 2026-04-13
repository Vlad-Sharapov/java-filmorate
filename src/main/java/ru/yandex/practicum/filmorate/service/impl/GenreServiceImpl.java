package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreStorage genreStorage;


    @Override
    public Genre getGenre(Integer id) {
        return genreStorage.getGenre(id);

    }

    @Override
    public List<Genre> getAllGenre() {
        return genreStorage.getAllGenre();
    }

    @Override
    public List<Genre> getFilmGenres(Long id) {
        return genreStorage.getFilmGenres(id);
    }
}
