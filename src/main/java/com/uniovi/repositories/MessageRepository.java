package com.uniovi.repositories;

import com.uniovi.entities.Chat;
import com.uniovi.entities.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {

	@Query("SELECT c FROM Chat c join c.messages m WHERE c = ?1 ORDER BY m.time ASC")
	List<Message> getMessagesByChat(Chat chat);
}
