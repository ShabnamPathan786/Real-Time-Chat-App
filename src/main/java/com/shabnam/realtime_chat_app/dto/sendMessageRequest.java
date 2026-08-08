package com.shabnam.realtime_chat_app.dto;

import com.shabnam.realtime_chat_app.model.MessageType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class sendMessageRequest{

    @NotBlank(message="Room ID is required")
    private String roomId;

    @NotBlank(message="sender is required")
     private String sender;

    @NotBlank(message="content is required")
    private String content;

    @NotNull(message="message type is required")
    private MessageType type;

}
