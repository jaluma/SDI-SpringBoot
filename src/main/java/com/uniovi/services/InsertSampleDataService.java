package com.uniovi.services;

import com.mifmif.common.regex.Generex;
import com.uniovi.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class InsertSampleDataService {
	private final UsersService usersService;
	private final RolesService rolesService;
	private final ChatsService chatsService;
	private final MessagesService messagesService;
	private final ItemsService itemsService;

	@Autowired
	public InsertSampleDataService(UsersService usersService, RolesService rolesService, ChatsService chatsService, MessagesService messagesService, ItemsService itemsService) {
		this.usersService = usersService;
		this.rolesService = rolesService;
		this.chatsService = chatsService;
		this.messagesService = messagesService;
		this.itemsService = itemsService;
	}

	@PostConstruct
	public void init() {
		User user1 = new User("admin@email.com", "Admin", "Admin");
		user1.setPassword("admin");
		user1.setRole(rolesService.getRoles()[1]);
		user1.setMoney(100);
		User user2 = createUser("javier@gmail.com", "Javier", "Martinez");

		Item item1 = new Item("Cadena de musica", "Reproductor de musica", new Date(), 150);
		Item item2 = new Item("Gorra", "De FA.", new Date(), 0.5);
		Item item3 = new Item("Silla", "Silla de estudio", new Date(), 35.99);

		item1.setHighlighter(true);
		item2.setHighlighter(true);

		Association.Sell.link(user2, item1);
		Association.Sell.link(user2, item2);
		Association.Sell.link(user2, item3);

		usersService.addUser(user1);
		usersService.addUser(user2);

		Set<User> set = new HashSet<>();
		set.add(user1);
		set.add(user2);

		Chat chat = new Chat(item1, set);
		chatsService.addChat(chat);

		Message message1 = new Message("Hola", OffsetDateTime.now());
		Association.Chats.sendMessage(user1, user2, chat, message1);
		messagesService.addMessage(message1);

		Message message2 = new Message("Buenas tardes :)", OffsetDateTime.now());
		Association.Chats.sendMessage(user2, user1, chat, message2);
		messagesService.addMessage(message2);

		chatsService.addChat(chat);

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