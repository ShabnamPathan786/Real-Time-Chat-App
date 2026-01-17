package com.shabnam.realtimechat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Collection;

@Document(collection ="message")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    @Id
    private String id;
    private String roomId;
    private String content;
    private String sender;
    private LocalDateTime timestamp;

    public Message(  String roomId,String content, String sender) {

        this.roomId=roomId;
        this.content = content;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
    }
}
