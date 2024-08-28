package com.example.algoyai.model.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author JSW
 *
 * 채팅 메시지 엔티티 클래스입니다.
 * MongoDB의 "chat_messages" 컬렉션에 저장될 문서(Document)를 나타냅니다.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Document(collection = "chat_messages")
public class ChatMessage {

  @Id
  private String id; // 메시지의 고유 식별자입니다.

  private String username; // 메시지를 보낸 사용자의 이름입니다.

  private String content; // 메시지의 내용입니다.

  private LocalDateTime createdAt; // 메시지가 생성된 시간입니다.
}