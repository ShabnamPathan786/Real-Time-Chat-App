package com.shabnam.realtimechat.repositories;

import com.shabnam.realtimechat.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepo extends MongoRepository<Message, String> {

    Page<Message> findByRoomId(String roomId,Pageable pageable);


}
