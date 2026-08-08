package com.shabnam.realtime_chat_app.controller;

import com.shabnam.realtime_chat_app.model.ChatMessage;
import com.shabnam.realtime_chat_app.model.MessageType;
import com.shabnam.realtime_chat_app.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    public ChatController(SimpMessagingTemplate messagingTemplate, MessageService messageService) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage, Principal principal) {
        String sender = (principal != null) ? principal.getName() : chatMessage.getSender();

        // Persist message in MongoDB
        messageService.saveMessage(
                chatMessage.getRoomId(),
                sender,
                chatMessage.getContent(),
                chatMessage.getType()
        );

        // Populate fields and broadcast to room
        chatMessage.setSender(sender);
        chatMessage.setTimestamp(LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/" + chatMessage.getRoomId(), chatMessage);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        String sender = (principal != null) ? principal.getName() : chatMessage.getSender();

        if (headerAccessor.getSessionAttributes() != null) {
            headerAccessor.getSessionAttributes().put("username", sender);
            headerAccessor.getSessionAttributes().put("roomId", chatMessage.getRoomId());
        }

        // Persist system join message
        messageService.saveMessage(
                chatMessage.getRoomId(),
                sender,
                sender + " joined the chat",
                MessageType.JOIN
        );

        chatMessage.setSender(sender);
        chatMessage.setContent(sender + " joined!");
        chatMessage.setType(MessageType.JOIN);
        chatMessage.setTimestamp(LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/" + chatMessage.getRoomId(), chatMessage);
    }
}
