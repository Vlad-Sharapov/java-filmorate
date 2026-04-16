package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.utils.EventType;
import ru.yandex.practicum.filmorate.utils.Operation;

import java.util.List;

public interface FeedStorage {

    void addFeed(Long entityId, Long userId, Long timeStamp, EventType eventType, Operation operation);

    List<Feed> getFeed(Long id);

}
