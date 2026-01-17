package com.shabnam.realtimechat.controller;

import com.shabnam.realtimechat.model.ChatRoom;
import com.shabnam.realtimechat.services.ChatRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/room")

public class RoomController {
    private final ChatRoomService service;

    public RoomController(ChatRoomService service){
        this.service=service;
    }

    //getRoom or join room
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoom(@PathVariable String  roomId){

        return ResponseEntity.ok(service.getRoom(roomId));
    }

    @PostMapping("/{roomId}/create")

    public ResponseEntity<?> createRoom(@PathVariable String roomId, @RequestBody String createdBy){

        return ResponseEntity.ok(service.createRoom(roomId,createdBy));
    }



}
