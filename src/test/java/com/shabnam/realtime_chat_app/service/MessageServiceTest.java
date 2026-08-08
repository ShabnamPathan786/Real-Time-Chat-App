package com.shabnam.realtime_chat_app.service;

import com.shabnam.realtime_chat_app.exception.RoomNotFoundException;
import com.shabnam.realtime_chat_app.model.ChatRoom;
import com.shabnam.realtime_chat_app.model.Message;
import com.shabnam.realtime_chat_app.model.MessageType;
import com.shabnam.realtime_chat_app.repository.ChatRoomRepository;
import com.shabnam.realtime_chat_app.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private MessageService messageService;

    private ChatRoom sampleRoom;
    private Message sampleMessage;

    @BeforeEach
    void setUp() {
        sampleRoom = ChatRoom.builder()
                .roomId("room123")
                .createdBy("creator")
                .createdAt(LocalDateTime.now())
                .build();

        sampleMessage = Message.builder()
                .id("msg1")
                .roomId("room123")
                .sender("user1")
                .content("hello world")
                .type(MessageType.CHAT)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Test
    void saveMessage_ShouldSaveAndReturnMessage_WhenRoomExists() {
        when(chatRoomRepository.findByRoomId("room123")).thenReturn(Optional.of(sampleRoom));
        when(messageRepository.save(any(Message.class))).thenReturn(sampleMessage);

        Message saved = messageService.saveMessage("room123", "user1", "hello world", MessageType.CHAT);

        assertNotNull(saved);
        assertEquals("room123", saved.getRoomId());
        assertEquals("user1", saved.getSender());
        assertEquals("hello world", saved.getContent());
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void saveMessage_ShouldThrowException_WhenRoomDoesNotExist() {
        when(chatRoomRepository.findByRoomId("unknown")).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () ->
            messageService.saveMessage("unknown", "user1", "hello world", MessageType.CHAT)
        );

        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void getMessages_ShouldReturnPageOfMessages_WhenRoomExists() {
        when(chatRoomRepository.findByRoomId("room123")).thenReturn(Optional.of(sampleRoom));
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<Message> msgPage = new PageImpl<>(Collections.singletonList(sampleMessage), pageable, 1);
        
        when(messageRepository.findByRoomId("room123", pageable)).thenReturn(msgPage);

        Page<Message> result = messageService.getMessages("room123", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("hello world", result.getContent().get(0).getContent());
        verify(messageRepository, times(1)).findByRoomId("room123", pageable);
    }

    @Test
    void getMessages_ShouldThrowException_WhenRoomDoesNotExist() {
        when(chatRoomRepository.findByRoomId("unknown")).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () ->
            messageService.getMessages("unknown", 0, 10)
        );
    }
}
