package com.uniovi.entities;

public class Association {

	public static class Buy {
		public static void link(User user, Item item) {
			item.setBuyerUser(user);
			user._getBuyerItems().add(item);
		}

		public static void unlink(Item item) {
			item.getBuyerUser()._getBuyerItems().remove(item);
			item.setBuyerUser(null);
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
		static void createChat(Chat chat, Item item) {
			chat.setItem(item);
			item._getChats().add(chat);
		}

		public static void removeChat(Chat chat) {
			chat.getItem()._getChats().remove(chat);
			chat.setItem(null);
		}

		public static void sendMessage(User sender, Chat chat, Message message) {
			message.setChat(chat);
			message.setUser(sender);
			sender._getMessages().add(message);
			chat._getMessages().add(message);
		}

		public static void removeMessage(User sender, Chat chat, Message message) {
			sender._getMessages().remove(message);
			chat._getMessages().remove(message);
			message.setChat(null);
			message.setUser(null);
		}

		public static void removeMessages(Chat chat) {
			for(Message message : chat._getMessages()) {
				message.setChat(null);
				message.setUser(null);
			}
			chat._getMessages().clear();
		}
	}
}
