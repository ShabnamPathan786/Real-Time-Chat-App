package com.shabnam.realtime_chat_app.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage{
    private String roomId;
    private String sender;
    private String content;
    private MessageType type;
    private LocalDateTime timestamp;

}