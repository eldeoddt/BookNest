package com.booknest.persistence;

import com.booknest.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, String> {
    public List<BookEntity> findByUserId(String userId);
    public List<BookEntity> findByTitle(String title);
}
