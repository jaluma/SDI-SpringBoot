package com.uniovi.services;

import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import com.uniovi.repositories.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class ItemsService {
	private final ItemsRepository itemsRepository;
	private final HttpSession httpSession;

	@Autowired
	public ItemsService(ItemsRepository itemsRepository, HttpSession httpSession) {
		this.itemsRepository = itemsRepository;
		this.httpSession = httpSession;
	}

	@SuppressWarnings("unchecked")
	public Item getItem(Long id) {
		Set<Item> consultedList = (Set<Item>) httpSession.getAttribute("consultedList");

		if(consultedList == null) {
			consultedList = new HashSet<>();
		}
		Optional<Item> optional = itemsRepository.findById(id);
		if(optional.isPresent()) {
			Item itemObtained = optional.get();
			httpSession.setAttribute("consultedList", consultedList);
			return itemObtained;
		}
		return null;
	}

	public Page<Item> getItemsByUser(Pageable pageable, User user) {
		return itemsRepository.findItemsbyEmail(pageable, user);
	}

	public void addItem(Item item) {
		itemsRepository.save(item);
	}

	public void deleteItem(Long id) {
		itemsRepository.deleteById(id);
	}


	public Page<Item> searchItemsByTitleDescriptionAndUsername(Pageable pageable, String searchText, User user) {
		searchText = "%" + searchText + "%";
		return itemsRepository.searchByTitleDescriptionAndUserDistintUser(pageable, searchText, user);
	}

	public Page<Item> getItems(Pageable pageable, User user) {
		return itemsRepository.findAllDistintUser(pageable, user);
	}

	public Page<Item> searchItemsByTitleDescriptionAndUsernameByUser(Pageable pageable, String searchText, User user) {
		searchText = "%" + searchText + "%";
		return itemsRepository.searchByTitleDescriptionAndUserByEmail(pageable, searchText, user);
	}

	public Page<Item> getItems(Pageable pageable) {
		return itemsRepository.findAll(pageable);
	}
}
