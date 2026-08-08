package com.shabnam.realtime_chat_app.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="chat_rooms")
public class ChatRoom{
    @Id
    private String id;
    private String roomId;
    private String createdBy;
    private LocalDateTime createdAt;


}
