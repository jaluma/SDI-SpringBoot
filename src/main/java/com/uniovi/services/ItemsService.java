package com.uniovi.services;

import com.uniovi.entities.Association;
import com.uniovi.entities.Item;
import com.uniovi.entities.User;
import com.uniovi.repositories.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
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

	public void add(Item item) {
		itemsRepository.save(item);
	}

	public void addItem(Item item, User user) {
		// restamos 20€ si el item esta destacado
		if(item.isHighlighter()) {
			highlighter(item, user);
		}
		Association.Sell.link(user, item);

		add(item);
	}

	public void highlighter(Item item, User user) {
		user.setMoney(user.getMoney() - 20);
		item.setHighlighter(true);
	}

	public void deleteItem(Item item) {
		Association.Sell.unlink(item);
		itemsRepository.deleteById(item.getId());
	}

	public Page<Item> searchItemsByTitleDescriptionAndUsernameByBuyerUser(Pageable pageable, String searchText, User user) {
		searchText = "%" + searchText + "%";
		return itemsRepository.searchByTitleDescriptionAndUserBuyerUser(pageable, searchText, user);
	}

	public Page<Item> getItemsByBuyerUser(Pageable pageable, User user) {
		return itemsRepository.findAllBuyerUser(pageable, user);
	}

	public Page<Item> searchItemsByTitleDescriptionAndUsernameBySellerUser(Pageable pageable, String searchText, User user) {
		searchText = "%" + searchText + "%";
		return itemsRepository.searchByTitleDescriptionAndUserDistintUser(pageable, searchText, user);
	}

	public Page<Item> getItemsBySellerUser(Pageable pageable, User user) {
		return itemsRepository.findAllDistintUser(pageable, user);
	}

	public Page<Item> searchItemsByTitleDescriptionAndUsernameByUser(Pageable pageable, String searchText, User user) {
		searchText = "%" + searchText + "%";
		return itemsRepository.searchByTitleDescriptionAndUserByEmail(pageable, searchText, user);
	}

	public Page<Item> getItemsBySellerUser(Pageable pageable) {
		return itemsRepository.findAll(pageable);
	}

	public List<Item> getHighlighterItems(User user) {
		return itemsRepository.findHighlighterItems(user);
	}

	public void setHighlighter(Item item) {
		item.setHighlighter(true);
		add(item);
	}

	public void buy(User buyerUser, Item item) {
		Association.Buy.link(buyerUser, item);

		buyerUser.setMoney(buyerUser.getMoney() - item.getPrice());
		add(item);
	}
}
