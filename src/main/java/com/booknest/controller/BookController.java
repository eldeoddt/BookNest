package com.booknest.controller;

import com.booknest.dto.BookDTO;
import com.booknest.dto.ResponseDTO;
import com.booknest.dto.TitleRequestDTO;
import com.booknest.model.BookEntity;
import com.booknest.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping
public class BookController {

    @Autowired
    private BookService service;

    // 전체 book 조회
    @GetMapping("/list")
    public ResponseEntity<?> retrieveAllBooks() {
        try {
            List<BookEntity> entities = service.getAllBooks();

            List<BookDTO> dtos = entities.stream()
                    .map(BookDTO::new)
                    .collect(Collectors.toList());

            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().data(dtos).build();
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    // book 추가
    @PostMapping("/book")
    public ResponseEntity<?> createBook(@RequestBody BookDTO dto, @AuthenticationPrincipal String userId) {
        try {
            BookEntity entity = BookDTO.toEntity(dto);
            entity.setId(null);
            entity.setUserId(userId);

            // enitity 생성
            List<BookEntity> entities = service.create(entity);
            List<BookDTO> dtos = entities.stream().map(BookDTO::new).collect(Collectors.toList());
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().data(dtos).build();

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            // 예외 처리 dto 대신 error메시지 반환
            String error = e.getMessage();
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    // book 검색
    @GetMapping("/book")
    public ResponseEntity<?> retrieveBookList(@RequestParam("title") String title) {
        List<BookEntity> entities = service.retrieveByTitle(title);

        List<BookDTO> dtos = entities.stream()
                .map(BookDTO::new)
                .collect(Collectors.toList());

        ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().data(dtos).build();
        return ResponseEntity.ok().body(response);
    }

    // book 수정
    @PutMapping("/book")
    public ResponseEntity<?> updateBook(@RequestBody BookDTO dto, @AuthenticationPrincipal String userId) {
        try {
            BookEntity entity = BookDTO.toEntity(dto);
            entity.setUserId(userId);
            // service.update로 엔티티 업데이트.
            BookEntity newEntity = service.update(entity);

            // 수정된 제품의 정보를 반환
            BookDTO newDto = new BookDTO(newEntity);
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().data(List.of(newDto)).build();

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    // book 삭제
    @DeleteMapping("/book")
    public ResponseEntity<?> deleteBook(@RequestBody BookDTO dto, @AuthenticationPrincipal String userId) {
        try {
            BookEntity entity = BookDTO.toEntity(dto);
            entity.setUserId(userId);
            // service.delete로 엔티티 삭제
            List<BookEntity> entities = service.delete(entity);
            List<BookDTO> dtos = entities.stream().map(BookDTO::new).collect(Collectors.toList());
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().data(dtos).build();

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<BookDTO> response = ResponseDTO.<BookDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

}
