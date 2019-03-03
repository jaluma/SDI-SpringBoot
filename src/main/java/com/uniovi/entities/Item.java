package com.uniovi.entities;

import org.apache.commons.math3.util.Precision;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

@Entity
public class Item {

	@Id
	@GeneratedValue
	private Long id;

	private String title;
	private String description;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Transient
	private String dateFormat;
	@Temporal(TemporalType.DATE)
	private Date date;

	private double price;

	@ManyToOne(fetch = FetchType.LAZY)
	//	@JoinColumn(name = "user_id")
	private User sellerUser;

	@ManyToOne(fetch = FetchType.LAZY)
	//	@JoinColumn(name = "user_id")
	private User buyerUser;

	public Item(String title, String description, Date date, double price) {
		setTitle(title);
		setDescription(description);
		setDate(date);
		setPrice(price);
	}

	public Item() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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
