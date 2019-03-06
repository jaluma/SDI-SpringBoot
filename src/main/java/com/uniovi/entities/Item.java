package com.uniovi.entities;

import org.apache.commons.math3.util.Precision;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Item {

	@Id
	@GeneratedValue
	private long id;

	private String title;
	private String description;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Transient
	private String dateFormat;
	@Temporal(TemporalType.DATE)
	private Date date;

	private double price;
	private boolean highlighter = false;

	@ManyToOne(fetch = FetchType.LAZY)
	private User sellerUser;

	@ManyToOne(fetch = FetchType.LAZY)
	private User buyerUser;

	@OneToMany(mappedBy = "item", fetch = FetchType.EAGER)
	private Set<Chat> itemChats = new HashSet<>();

	public Item(String title, String description, Date date, double price) {
		setTitle(title);
		setDescription(description);
		setDate(date);
		setPrice(price);
	}

	public Item() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDate() {
		return date;
	}

	private void setDate(Date date) {
		this.date = date;
	}

	public double getPrice() {
		return price;
	}

	private void setPrice(double price) {
		this.price = Precision.round(price, 2);
	}

	public User getSellerUser() {
		return sellerUser;
	}

	void setSellerUser(User user) {
		this.sellerUser = user;
	}

	public User getBuyerUser() {
		return buyerUser;
	}

	void setBuyerUser(User buyerUser) {
		this.buyerUser = buyerUser;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
		this.date = asDate(LocalDate.parse(dateFormat, DateTimeFormatter.ISO_DATE));
	}

	public boolean isHighlighter() {
		return highlighter;
	}

	public void setHighlighter(boolean highlighter) {
		this.highlighter = highlighter;
	}

	Set<Chat> _getItemChats() {
		return itemChats;
	}

	public Set<Chat> getItemChats() {
		return new HashSet<>(itemChats);
	}

	public void setItemChats(Set<Chat> itemChats) {
		this.itemChats = itemChats;
	}

	private Date asDate(LocalDate localDate) {
		return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;
		Item item = (Item) o;
		return Double.compare(item.price, price) == 0 && title.equals(item.title) && description.equals(item.description);
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, description, date);
	}
}
