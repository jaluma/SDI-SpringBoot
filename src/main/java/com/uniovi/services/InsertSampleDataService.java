package com.uniovi.services;

import com.mifmif.common.regex.Generex;
import com.uniovi.entities.Association;
import com.uniovi.entities.Item;
import com.uniovi.entities.Message;
import com.uniovi.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class InsertSampleDataService {
	private final UsersService usersService;
	private final RolesService rolesService;
	private final ChatsService chatsService;

	@Autowired
	public InsertSampleDataService(UsersService usersService, RolesService rolesService, ChatsService chatsService) {
		this.usersService = usersService;
		this.rolesService = rolesService;
		this.chatsService = chatsService;
	}

	@PostConstruct
	public void init() {
		User user1 = new User("admin@email.com", "Admin", "Admin");
		user1.setPassword("admin");
		user1.setRole(rolesService.getRoles()[1]);
		user1.setMoney(100);
		User user2 = createUser("javiermartinezalvarez98@gmail.com", "Javier", "Martinez");

		Item item1 = new Item("Cadena de musica", "Reproductor de musica", new Date(), 150);
		Item item2 = new Item("Gorra", "De FA.", new Date(), 0.5);
		Item item3 = new Item("Silla", "Silla de estudio", new Date(), 35.99);

		Association.Sell.link(user2, item1);
		Association.Sell.link(user2, item2);
		Association.Sell.link(user2, item3);

		usersService.addUser(user1);
		usersService.addUser(user2);

		Message message1 = new Message();
		message1.setMessage("Hola");
		message1.setTime(OffsetDateTime.now());
		message1.setSender(user1);
		message1.setReceiver(user2);
		chatsService.addMessage(message1);

		Message message2 = new Message();
		message2.setMessage("Buenas tardes :)");
		message2.setTime(OffsetDateTime.now());
		message2.setSender(user2);
		message2.setReceiver(user1);
		chatsService.addMessage(message2);

		//otros datos
		List<User> users = new ArrayList<>();
		users.add(createUser("pedro@gmail.com", "Pedro", "Manrrique"));
		users.add(createUser("benjamin@gmail.com", "Benjamin", "Cuellas"));
		users.add(createUser("juan@gmail.com", "Juán", "Mayo"));

		for(int i = 0; i < users.size(); i++) {
			for(int j = 0; j < 5; j++) {
				Item item = new Item("Producto " + (j + 1) + " de User " + (i + 1), "Descripcion de Producto " + (i + 1), new Date(), Math.random() * 100);
				Association.Sell.link(users.get(i), item);
			}
			usersService.addUser(users.get(i));
		}

		//generamos valores para tener 20 registros
		String[] names = new String[]{"Maria", "Cristina", "Elena", "Ivan", "Pedro", "Fernando"};
		String[] lastnames = new String[]{"Perez", "Garcia", "Fernandez", "Alvarez", "Rodriguez"};

		Random rd = new Random();

		for(int i = 0; i < 15; i++) {
			String email = new Generex("\\w{10}\\@uniovi\\.es").random();
			String name = names[rd.nextInt(names.length)];
			String lastname = lastnames[rd.nextInt(lastnames.length)];

			User user = createUser(email, name, lastname);

			usersService.addUser(user);
		}
	}

	private User createUser(String email, String name, String lastname) {
		User user = new User(email, name, lastname);
		user.setPassword("123456");
		user.setMoney(100);
		user.setRole(rolesService.getRoles()[0]);
		return user;
	}
}