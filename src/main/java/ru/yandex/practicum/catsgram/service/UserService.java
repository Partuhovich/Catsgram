package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;

@Service
public class UserService {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll(Long size, Long from, String sort) {
        if(!(size > 0)) {
            throw new ConditionsNotMetException("Размер должен быть больше нуля");
        }

        Comparator<User> dateComparator;

        if(Objects.equals(sort, "asc")) {
            dateComparator = Comparator.comparing(User::getRegistrationDate);
        } else if(Objects.equals(sort, "desc")) {
            dateComparator = Comparator.comparing(User::getRegistrationDate).reversed();
        } else {
            throw new ConditionsNotMetException("Неверный метод сортировки. Допустимые значения: asc, desc");
        }

        return users.values().stream()
                .sorted(dateComparator)
                .skip(from)
                .limit(size)
                .toList();
    }

    public User find (Long userId) {
        if(!users.containsKey(userId)) {
            throw new NotFoundException("Пост с id = " + userId + " не найден");
        }
        return users.get(userId);
    }

    public User create(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        boolean emailExists = users.values().stream()
                .anyMatch(u -> user.getEmail().equals(u.getEmail()));

        if (emailExists) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);

        return user;
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            throw new ConditionsNotMetException("Пользователь с id=" + user.getId() + " не найден");
        }

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            boolean emailUsedByOther = users.values().stream()
                    .filter(u -> !u.getId().equals(user.getId()))
                    .anyMatch(u -> user.getEmail().equals(u.getEmail()));

            if (emailUsedByOther) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }

            existingUser.setEmail(user.getEmail());
        }

        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }

        if (user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());
        }

        return existingUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}