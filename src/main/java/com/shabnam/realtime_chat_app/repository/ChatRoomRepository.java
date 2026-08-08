package com.shabnam.realtime_chat_app.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shabnam.realtime_chat_app.model.ChatRoom;

@Repository
public interface ChatRoomRepository extends MongoRepository<ChatRoom , String>{
    Optional<ChatRoom> findByRoomId(String roomId);

    boolean existsByRoomId(String roomId);
}
