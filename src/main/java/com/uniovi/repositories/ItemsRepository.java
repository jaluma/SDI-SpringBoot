package com.uniovi.repositories;

import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemsRepository extends CrudRepository<Item, Long> {

	@Query("SELECT r FROM Item r WHERE LOWER(r.title) LIKE LOWER(?1) OR LOWER(r.description) LIKE LOWER(?1) OR LOWER(r.buyerUser.name) LIKE LOWER(?1) and r.buyerUser = ?2")
	Page<Item> searchByTitleDescriptionAndUserBuyerUser(Pageable pageable, String seachtext, User user);

	@Query("SELECT r FROM Item r WHERE r.buyerUser = ?1")
	Page<Item> findAllBuyerUser(Pageable pageable, User user);

	@Query("SELECT r FROM Item r WHERE LOWER(r.title) LIKE LOWER(?1) OR LOWER(r.description) LIKE LOWER(?1) OR LOWER(r.sellerUser.name) LIKE LOWER(?1) and r.sellerUser <> ?2")
	Page<Item> searchByTitleDescriptionAndUserDistintUser(Pageable pageable, String seachtext, User user);

	@Query("SELECT r FROM Item r WHERE r.sellerUser <> ?1")
	Page<Item> findAllDistintUser(Pageable pageable, User user);

	Page<Item> findAll(Pageable pageable);

	@Query("SELECT r FROM Item r WHERE r.sellerUser = ?1 ORDER BY r.id ASC")
	Page<Item> findItemsbyEmail(Pageable pageable, User user);

	@Query("SELECT r FROM Item r WHERE LOWER(r.title) LIKE LOWER(?1) OR LOWER(r.description) LIKE LOWER(?1) OR LOWER(r.sellerUser.name) LIKE LOWER(?1) AND r.sellerUser = ?2")
	Page<Item> searchByTitleDescriptionAndUserByEmail(Pageable pageable, String searchText, User user);

	@Query("SELECT r FROM Item r WHERE r.sellerUser <> ?1 AND r.highlighter = true")
	List<Item> findHighlighterItems(User user);
}