package com.shabnam.realtime_chat_app.service;

import com.shabnam.realtime_chat_app.exception.RoomAlreadyExistsException;
import com.shabnam.realtime_chat_app.exception.RoomNotFoundException;
import com.shabnam.realtime_chat_app.model.ChatRoom;
import com.shabnam.realtime_chat_app.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public ChatRoom createRoom(String roomId, String createdBy) {
        if (chatRoomRepository.existsByRoomId(roomId)) {
            throw new RoomAlreadyExistsException("Room with ID " + roomId + " already exists.");
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .roomId(roomId)
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();

        return chatRoomRepository.save(chatRoom);
    }

    public ChatRoom getRoom(String roomId) {
        return chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new RoomNotFoundException("Room not found with ID: " + roomId));
    }

    public List<ChatRoom> getAllRooms() {
        return chatRoomRepository.findAll();
    }
}
