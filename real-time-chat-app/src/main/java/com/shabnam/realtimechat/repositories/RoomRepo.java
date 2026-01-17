package com.shabnam.realtimechat.repositories;

import com.shabnam.realtimechat.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoomRepo extends MongoRepository<ChatRoom,String> {
    Optional<ChatRoom> findByRoomId (String roomId);

    boolean existsByRoomId(String roomId);
}
