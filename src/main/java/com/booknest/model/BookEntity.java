package com.booknest.model;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Builder
@NoArgsConstructor // 인자없는 생성자 생성
@AllArgsConstructor
@Data // getter/setter 추가
@Entity // entity 클래스 지정

@Table(name="book")
public class BookEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id;

    private String userId;
    private String publisher;
    private String title;
    private String author;
    private String isbn;
    private LocalDate publishDate;
    private String category;
    private String language;
    private int price;
    private int stock;
    private int soldCount;
}
