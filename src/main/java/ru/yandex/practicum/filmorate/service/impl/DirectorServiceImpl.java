package ru.yandex.practicum.filmorate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    @Override
    public Director addDirector(Director director) {
        Long id = directorStorage.addDirector(director);
        log.info("Add director with id {}", id);
        return director.toBuilder()
                .id(id)
                .build();
    }

    @Override
    public Director updateDirector(Director director) {
        log.info("Update director with id {}", director.getId());
        return directorStorage.updateDirector(director);
    }

    @Override
    public void deleteDirector(Long directorId) {
        directorStorage.deleteDirector(directorId);
        log.info("Director {} has been deleted.", directorId);
    }

    @Override
    public List<Director> getDirectors() {
        log.info("Get directors");
        return directorStorage.getDirectors();
    }

    @Override
    public Director getDirectorById(Long id) {
        log.info("Get director with id {}", id);
        return directorStorage.getDirectorById(id);
    }



}
