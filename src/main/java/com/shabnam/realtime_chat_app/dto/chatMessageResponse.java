package com.shabnam.realtime_chat_app.dto;

import java.time.LocalDateTime;

import com.shabnam.realtime_chat_app.model.MessageType;

import lombok.Data;

@Data
public class chatMessageResponse{
    private String id;
    private String sender;
    private String content;
    private String createdBy;
    private LocalDateTime timestamp;
    private MessageType type;
}
