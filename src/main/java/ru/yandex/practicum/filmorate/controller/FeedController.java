package ru.yandex.practicum.filmorate.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.service.FeedService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/users/{id}/feed")
    public List<Feed> getFeed(@PathVariable("id") Long id) {
        log.info("Get feed by userId" + id);

        return feedService.getFeed(id);
    }}
