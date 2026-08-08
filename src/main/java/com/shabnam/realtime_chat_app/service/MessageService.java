package com.shabnam.realtime_chat_app.service;

import com.shabnam.realtime_chat_app.exception.RoomNotFoundException;
import com.shabnam.realtime_chat_app.model.Message;
import com.shabnam.realtime_chat_app.model.MessageType;
import com.shabnam.realtime_chat_app.repository.ChatRoomRepository;
import com.shabnam.realtime_chat_app.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public MessageService(MessageRepository messageRepository, ChatRoomRepository chatRoomRepository) {
        this.messageRepository = messageRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public Message saveMessage(String roomId, String sender, String content, MessageType type) {
        // Verify room exists
        chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with ID: " + roomId));

        Message message = Message.builder()
                .roomId(roomId)
                .sender(sender)
                .content(content)
                .type(type)
                .timestamp(LocalDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    public Page<Message> getMessages(String roomId, int page, int size) {
        // Verify room exists
        chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with ID: " + roomId));

        return messageRepository.findByRoomId(
                roomId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"))
        );
    }
}
