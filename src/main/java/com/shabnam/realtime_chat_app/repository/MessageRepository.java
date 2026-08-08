package com.shabnam.realtime_chat_app.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.shabnam.realtime_chat_app.model.Message;

@Repository
public interface MessageRepository extends MongoRepository<Message,String>{
      Page<Message> findByRoomId(String roomId,Pageable pageable);
}
