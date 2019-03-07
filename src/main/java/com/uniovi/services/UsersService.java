package com.uniovi.services;

import com.uniovi.entities.User;
import com.uniovi.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		user.setMoney(DEFUALT_MONEY);
		usersRepository.save(user);
	}

	public User getUserByEmail(String email) {
		return usersRepository.findByEmail(email);
	}


	public void deleteUser(Long id) {
		usersRepository.deleteById(id);
	}

	public Page<User> getUsers(Pageable pageable) {
		return usersRepository.findAll(pageable);
	}

	public Page<User> searchUsers(Pageable pageable, String searchText) {
		return usersRepository.findAllByUsernameAndFullName(pageable, searchText);
	}

}
