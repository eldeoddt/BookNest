package com.booknest.service;

import com.booknest.model.BookEntity;
import com.booknest.persistence.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BookService {

    @Autowired
    private BookRepository repository;

    /**
     * book을 생성합니다.
     */
    public List<BookEntity> create(final BookEntity entity) {
        validate(entity);
        repository.save(entity);
        log.info("entity id: {} is saved", entity.getId());
        return repository.findByUserId(entity.getUserId());
    }

    /**
     * book을 수정합니다.
     */
    public List<BookEntity> update(BookEntity entity) {
        validate(entity);
        final Optional<BookEntity> original = repository.findById(entity.getId());

        original.ifPresent(book -> {
            // original이 존재하면 book entity를 덮어씌운다.
            book.setPublisher(entity.getPublisher());
            book.setTitle(entity.getTitle());
            book.setAuthor(entity.getAuthor());
            book.setIsbn(entity.getIsbn());
            book.setPublishDate(entity.getPublishDate());
            book.setCategory(entity.getCategory());
            book.setLanguage(entity.getLanguage());
            book.setPrice(entity.getPrice());
            book.setStock(entity.getStock());
            book.setSoldCount(entity.getSoldCount());
            repository.save(book);
        });

        return retrieveByUserId(entity.getUserId());
    }

    /**
     * book을 삭제합니다.
     */
    public List<BookEntity> delete(final BookEntity entity) {
        validate(entity);

        try {
            repository.delete(entity);
        } catch (Exception e) {
            log.error("error deleting entity", entity.getId(), e);

            throw new RuntimeException("error deleting entity" + entity.getId());
        }

        return retrieveByUserId(entity.getUserId());
    }

    /**
     * book을 userId로 검색합니다.
     */
    public List<BookEntity> retrieveByUserId(final String userId) {
        return repository.findByUserId(userId);
    }

    /**
     * book을 title로 검색합니다.
     */
    public List<BookEntity> retrieveByTitle(final String title) {
        return repository.findByUserId(title);
    }

    /**
     * 사용자를 검증합니다.
     */
    private void validate(BookEntity entity) {
        if (entity == null) {
            log.warn("Entity cannot be null");
            throw new RuntimeException("Entity cannot be null");
        }

        if (entity.getUserId() == null) {
            log.warn("Unknown user.");
            throw new RuntimeException("Unknown user");
        }
    }
}
