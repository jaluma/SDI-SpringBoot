package com.uniovi.services;

import com.uniovi.entities.Chat;
import com.uniovi.entities.User;
import com.uniovi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {
	private static final double DEFUALT_MONEY = 100.0;

	private final UsersRepository usersRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	public UsersService(UsersRepository usersRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	public User getUser(Long id) {
		Optional<User> optional = usersRepository.findById(id);

		return optional.orElse(null);
	}

	public void addUser(User user) {
		user.setMoney(DEFUALT_MONEY);
		encryptPassword(user);
		add(user);
	}

	public void add(User user) {
		usersRepository.save(user);
	}

	public User getUserByEmail(String email) {
		return usersRepository.findByEmail(email);
	}


	public void deleteUser(User user) {
		usersRepository.delete(user);
	}

	public Page<User> getUsers(Pageable pageable) {
		return usersRepository.findAll(pageable);
	}

	public Page<User> searchUsers(Pageable pageable, String searchText) {
		return usersRepository.findAllByUsernameAndFullName(pageable, searchText);
	}

	public void deleteAll() {
		usersRepository.deleteAll();
	}

	void encryptPassword(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
	}

	void addAll(Iterable<User> usersList) {
		usersRepository.saveAll(usersList);
	}

	public List<User> getUsers(Chat chat) {
		return usersRepository.getUsersByChat(chat);
	}

	public List<User> getUsersDistintByChat(Chat chat, User user) {
		return usersRepository.getUsersDistintByChat(chat, user);
	}
}
