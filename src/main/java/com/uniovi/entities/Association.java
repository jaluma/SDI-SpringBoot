package com.uniovi.entities;

public class Association {

	public static class Buy {
		public static void link(User user, Item item) {
			item.setBuyerUser(user);
			user._getBuyerItems().add(item);
		}

		public static void unlink(User user, Item item) {
			user._getBuyerItems().remove(item);
			item.setBuyerUser(null);
		}
	}

	public static class Sell {
		public static void link(User user, Item item) {
			item.setSellerUser(user);
			user._getSellerItems().add(item);
		}

		public static void unlink(User user, Item item) {
			user._getSellerItems().remove(item);
			item.setSellerUser(null);
		}
	}

	public static class Chats {
		public static void createChat(Chat chat, Item item) {
			chat.setItem(item);
			item._getItemChats().add(chat);
		}

		public static void deleteChat(Chat chat, Item item) {
			item._getItemChats().remove(chat);
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

		public static void removeMessage(User sender, User receiver, Chat chat, Message message) {
			chat._getUsers().remove(sender);
			chat._getUsers().remove(receiver);
			chat._getMessages().remove(message);
			sender._getSenderMessages().remove(message);
			receiver._getReceiverMessages().remove(message);
			message.setSender(null);
			message.setReceiver(null);
			message.setChat(null);
		}
	}
}
