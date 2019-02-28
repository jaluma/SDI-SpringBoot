package com.uniovi.repositories;

import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ItemsRepository extends CrudRepository<Item, Long> {

	@Query("SELECT r FROM Item r WHERE LOWER(r.title) LIKE LOWER(?1) OR LOWER(r.description) LIKE LOWER(?1) OR LOWER(r.user.name) LIKE LOWER(?1)")
	Page<Item> searchByTitleDescriptionAndUser(Pageable pageable, String seachtext);

	Page<Item> findAll(Pageable pageable);

	@Query("SELECT r FROM Item r WHERE r.user = ?1 ORDER BY r.id ASC")
	Page<Item> findItemsbyEmail(Pageable pageable, User user);
}