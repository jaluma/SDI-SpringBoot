package com.uniovi.repositories;

import com.uniovi.entities.Chat;
import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatRepository extends CrudRepository<Chat, Long> {

	@Query("SELECT c FROM Chat c join c.users u join c.messages m WHERE u = ?1 AND c.item = ?2 ORDER BY m.time DESC")
	List<Chat> getChat(User sender, Item item);

	@Query("SELECT c FROM Chat c join c.users u WHERE u = ?1")
	Page<Chat> findAll(Pageable pageable, User currentUser);
}
