package com.uniovi.entities;

import java.util.HashSet;
import java.util.Set;

public class Association {

	public static class Buy {
		public static void link(User user, Item item) {
			item.setBuyerUser(user);
			user._getBuyerItems().add(item);
		}
	}

	public static class Sell {
		public static void link(User user, Item item) {
			item.setSellerUser(user);
			user._getSellerItems().add(item);
		}

		public static void unlink(Item item) {
			item.getSellerUser()._getSellerItems().remove(item);
			item.setSellerUser(null);
		}
	}

	public static class Chats {
		static void createChat(Chat chat, Item item, Set<User> set) {
			chat.setUsers(set);
			chat.setItem(item);
			item._getItemChats().add(chat);
		}

		public static void removeChat(Chat chat) {
			chat.getItem()._getItemChats().remove(chat);
			chat.setUsers(new HashSet<>());
			chat.setItem(null);
		}

		public static void sendMessage(User sender, User receiver, Chat chat, Message message) {
			message.setChat(chat);
			message.setSender(sender);
			message.setReceiver(receiver);
			sender._getSenderMessages().add(message);
			receiver._getReceiverMessages().add(message);
			chat._getMessages().add(message);
			chat._getUsers().add(sender);
			chat._getUsers().add(receiver);
		}

		public static void removeMessages(User sender, User receiver, Chat chat) {
			for(Message message : chat._getMessages()) {
				sender._getSenderMessages().remove(message);
				receiver._getReceiverMessages().remove(message);
				message.setChat(null);
				message.setSender(null);
				message.setReceiver(null);
			}

			chat._getMessages().clear();
			chat._getUsers().clear();
		}
	}
}
