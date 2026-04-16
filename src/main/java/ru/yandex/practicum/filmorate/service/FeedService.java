package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedService {

    List<Feed> getFeed(Long id);

}
