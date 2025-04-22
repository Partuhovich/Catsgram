package ru.yandex.practicum.catsgram.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.User;
import ru.yandex.practicum.catsgram.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll(@RequestParam(defaultValue = "10") Long size,
                                    @RequestParam(defaultValue = "0") Long from,
                                    @RequestParam(defaultValue = "desc") String sort) {
        return userService.findAll(size, from, sort);
    }

    @GetMapping("/{userId}")
    public User find(@PathVariable Long userId) {
        return  userService.find(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        return userService.update(user);
    }
}