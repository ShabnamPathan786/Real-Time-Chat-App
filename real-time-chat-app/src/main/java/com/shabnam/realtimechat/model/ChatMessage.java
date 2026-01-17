package com.shabnam.realtimechat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage{

    private String sender;
    private String content;
    private MessageType type;
    private LocalDateTime timeStamp;

    public ChatMessage(String sender, String content, MessageType type) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.timeStamp = LocalDateTime.now();
    }





    public enum MessageType{
      CHAT,JOIN,LEAVE
  }
}
