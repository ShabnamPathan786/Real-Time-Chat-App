package com.shabnam.realtimechat.controller;

import com.shabnam.realtimechat.model.Message;
import com.shabnam.realtimechat.request_response.MessageRequest;
import com.shabnam.realtimechat.services.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/messages")
public class MessageController {
 private final MessageService service;

 public MessageController(MessageService service){
  this.service=service;
 }
@GetMapping("/{roomId}")
 public ResponseEntity<Page<Message>>getMessages(@PathVariable String  roomId, @RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue="20") int size){
        return ResponseEntity.ok(service.getMessage(roomId,page,size));
}

@PostMapping("/{roomId}")
public ResponseEntity<Message> saveMessage(@PathVariable String roomId, @RequestBody MessageRequest request){
        Message saveMessage=service.saveMessage(roomId,request.getContent(), request.getSender());
        return ResponseEntity.status(HttpStatus.CREATED).body(saveMessage);
}
}
