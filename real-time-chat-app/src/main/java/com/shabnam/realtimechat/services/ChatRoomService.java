package com.shabnam.realtimechat.services;

import com.shabnam.realtimechat.exception.RoomAlreadyExistsException;
import com.shabnam.realtimechat.exception.RoomNotFoundException;
import com.shabnam.realtimechat.model.ChatRoom;
import com.shabnam.realtimechat.repositories.RoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatRoomService {

    private final RoomRepo repo;

   public ChatRoomService(RoomRepo repo){
       this.repo=repo;
   }

   public ChatRoom getRoom(String roomId){
       return repo.findByRoomId(roomId)
               .orElseThrow(()-> new RoomNotFoundException("Room doesn't exists"));
    }

    public  ChatRoom createRoom( String roomId,String createdBy){
       if(repo.existsByRoomId(roomId)){
           throw new RoomAlreadyExistsException("Room already exists");
       }
       ChatRoom room =new ChatRoom();
       room.setRoomId(roomId);
       room.setCreatedBy(createdBy);
       return repo.save(room);

    }

}
