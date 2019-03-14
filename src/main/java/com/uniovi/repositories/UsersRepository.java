package com.uniovi.repositories;

import com.uniovi.entities.Chat;
import com.uniovi.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UsersRepository extends CrudRepository<User, Long> {

	User findByEmail(String email);

	@Query("SELECT s FROM User s ORDER BY s.id ASC")
	Page<User> findAll(Pageable pageable);

	@Query("SELECT s FROM User s WHERE LOWER(s.email) LIKE LOWER(?1) OR LOWER(s.name) LIKE LOWER(?1) OR LOWER(s.lastName) LIKE LOWER(?1) ORDER BY s.id ASC")
	Page<User> findAllByUsernameAndFullName(Pageable pageable, String searchText);

	@Query("SELECT m.user FROM Chat c join c.messages m WHERE c = ?1")
	List<User> getUsersByChat(Chat chat);

	@Query("SELECT m.user FROM Chat c join c.messages m WHERE c = ?1 and m.user <> ?1")
	List<User> getUsersDistintByChat(Chat chat, User user);
}