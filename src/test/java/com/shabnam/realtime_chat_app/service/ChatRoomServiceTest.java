package com.shabnam.realtime_chat_app.service;

import com.shabnam.realtime_chat_app.exception.RoomAlreadyExistsException;
import com.shabnam.realtime_chat_app.exception.RoomNotFoundException;
import com.shabnam.realtime_chat_app.model.ChatRoom;
import com.shabnam.realtime_chat_app.repository.ChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    private ChatRoom sampleRoom;

    @BeforeEach
    void setUp() {
        sampleRoom = ChatRoom.builder()
                .roomId("room123")
                .createdBy("creatorUser")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createRoom_ShouldSaveRoom_WhenRoomDoesNotExist() {
        when(chatRoomRepository.existsByRoomId("room123")).thenReturn(false);
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(sampleRoom);

        ChatRoom created = chatRoomService.createRoom("room123", "creatorUser");

        assertNotNull(created);
        assertEquals("room123", created.getRoomId());
        assertEquals("creatorUser", created.getCreatedBy());
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    void createRoom_ShouldThrowException_WhenRoomAlreadyExists() {
        when(chatRoomRepository.existsByRoomId("room123")).thenReturn(true);

        assertThrows(RoomAlreadyExistsException.class, () ->
            chatRoomService.createRoom("room123", "creatorUser")
        );

        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    void getRoom_ShouldReturnRoom_WhenRoomExists() {
        when(chatRoomRepository.findByRoomId("room123")).thenReturn(Optional.of(sampleRoom));

        ChatRoom found = chatRoomService.getRoom("room123");

        assertNotNull(found);
        assertEquals("room123", found.getRoomId());
    }

    @Test
    void getRoom_ShouldThrowException_WhenRoomDoesNotExist() {
        when(chatRoomRepository.findByRoomId("unknown")).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () ->
            chatRoomService.getRoom("unknown")
        );
    }

    @Test
    void getAllRooms_ShouldReturnList_WhenRoomsExist() {
        ChatRoom room2 = ChatRoom.builder().roomId("room456").createdBy("anotherUser").build();
        when(chatRoomRepository.findAll()).thenReturn(Arrays.asList(sampleRoom, room2));

        List<ChatRoom> rooms = chatRoomService.getAllRooms();

        assertEquals(2, rooms.size());
        assertEquals("room123", rooms.get(0).getRoomId());
        assertEquals("room456", rooms.get(1).getRoomId());
    }
}
