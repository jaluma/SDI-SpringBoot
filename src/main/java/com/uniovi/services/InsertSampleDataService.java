package com.uniovi.services;

import com.mifmif.common.regex.Generex;
import com.uniovi.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Random;

@Service
public class InsertSampleDataService {
	private final UsersService usersService;
	private final RolesService rolesService;

	@Autowired
	public InsertSampleDataService(UsersService usersService, RolesService rolesService) {
		this.usersService = usersService;
		this.rolesService = rolesService;
	}

	@PostConstruct
	public void init() {
		User user1 = new User("admin@email.com", "Admin", "Admin");
		user1.setPassword("admin");
		user1.setRole(rolesService.getRoles()[1]);
		User user2 = new User("javiermartinezalvarez98@gmail.com", "Javier", "Martinez");
		user2.setPassword("123456");
		user2.setRole(rolesService.getRoles()[0]);

		usersService.addUser(user1);
		usersService.addUser(user2);

		String[] names = new String[]{"Maria", "Cristina", "Elena", "Ivan", "Pedro", "Fernando"};
		String[] lastnames = new String[]{"Perez", "Garcia", "Fernandez", "Cuellas", "Rodriguez"};

		Random rd = new Random();

		for(int i = 0; i < 18; i++) {
			String email = new Generex("\\w{10}\\@uniovi\\.es").random();
			String name = names[rd.nextInt(names.length)];
			String lastname = lastnames[rd.nextInt(lastnames.length)];

			User user = new User(email, name, lastname);
			user.setPassword("123456");
			user.setRole(rolesService.getRoles()[0]);

			usersService.addUser(user);
		}
	}
}