package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;

@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();

    public Collection<Post> findAll( Long size,  Long from, String sort) {
        if(!(size > 0)) {
            throw new ParameterNotValidException("size", "\"Некорректный размер выборки. Размер должен быть больше нуля\"");
        }
        if(from<0) {
            throw new ParameterNotValidException("from", "\"Некорректная точко отсчета. Точка отсчета не может быть меньше нуля\"");
        }
        Comparator<Post> dateComparator;

        if(Objects.equals(sort, "asc")){
            dateComparator = Comparator.comparing((Post::getPostDate));
        } else if(Objects.equals(sort, "desc")){
            dateComparator = Comparator.comparing((Post::getPostDate)).reversed();
        } else {
            throw new ParameterNotValidException("sort", "Неверный метод сортировки. Допустимые значения: asc, desc");
        }
        return posts.values().stream().sorted(dateComparator).skip(from).limit(size).toList();
    }

    public Post find(Long postId) {
        if(!posts.containsKey(postId)) {
            throw new NotFoundException("Пост с id = " + postId + " не найден");
        }
        return posts.get(postId);
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}