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
}
