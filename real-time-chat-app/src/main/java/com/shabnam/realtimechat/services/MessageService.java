package com.shabnam.realtimechat.services;

import com.shabnam.realtimechat.exception.RoomNotFoundException;
import com.shabnam.realtimechat.model.Message;
import com.shabnam.realtimechat.repositories.MessageRepo;
import com.shabnam.realtimechat.repositories.RoomRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final MessageRepo msgRepo;
    private final RoomRepo roomRepo;

     public MessageService(MessageRepo msgRepo, RoomRepo roomRepo){
         this.msgRepo=msgRepo;
         this.roomRepo=roomRepo;
     }

    public Page<Message> getMessage(String roomId, int page, int size) {
         if(!roomRepo.existsByRoomId(roomId)){
             throw new RoomNotFoundException("Room doesn't exists");
         }
        Pageable pageable= PageRequest.of(page,size, Sort.by("timestamp").descending());

         return msgRepo.findByRoomId(roomId,pageable);
    }

    public Message saveMessage(String roomId, String content,String sender){
         if(!roomRepo.existsByRoomId(roomId)){
             throw new RoomNotFoundException("Room doesn't exists");
         }
         Message message=new Message(roomId,content,sender);
         return msgRepo.save(message);

    }
}
