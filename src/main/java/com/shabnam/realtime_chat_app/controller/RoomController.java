package com.shabnam.realtime_chat_app.controller;

import com.shabnam.realtime_chat_app.dto.chatRoomResponse;
import com.shabnam.realtime_chat_app.dto.createRoomRequest;
import com.shabnam.realtime_chat_app.model.ChatRoom;
import com.shabnam.realtime_chat_app.model.Message;
import com.shabnam.realtime_chat_app.service.ChatRoomService;
import com.shabnam.realtime_chat_app.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;

    public RoomController(ChatRoomService chatRoomService, MessageService messageService) {
        this.chatRoomService = chatRoomService;
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<chatRoomResponse> createRoom(@Valid @RequestBody createRoomRequest request, Principal principal) {
        String creator = (principal != null) ? principal.getName() : request.getCreatedBy();
        ChatRoom room = chatRoomService.createRoom(request.getRoomId(), creator);
        
        chatRoomResponse response = new chatRoomResponse();
        response.setId(room.getId());
        response.setRoomId(room.getRoomId());
        response.setCreatedBy(room.getCreatedBy());
        response.setCreatedAt(room.getCreatedAt());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ChatRoom>> getAllRooms() {
        return ResponseEntity.ok(chatRoomService.getAllRooms());
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<Page<Message>> getRoomMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(messageService.getMessages(roomId, page, size));
    }
}
