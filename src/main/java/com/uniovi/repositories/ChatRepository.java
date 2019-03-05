package com.uniovi.repositories;

import com.uniovi.entities.Message;
import com.uniovi.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatRepository extends CrudRepository<Message, Long> {

	@Query("SELECT m FROM Message m WHERE (m.sender = ?1 AND m.receiver = ?2) OR (m.sender = ?2 AND m.receiver = ?1) ORDER BY m.time")
	List<Message> findAll(User sender, User receiver);
}
