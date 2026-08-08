package com.shabnam.realtime_chat_app.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class chatRoomResponse{
      private String id;
      private String roomId;
      private String createdBy;
      private LocalDateTime createdAt;
    }

