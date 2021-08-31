package application.inventory.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity: inventorydb.items
 *
 */
@Entity
@Table(name = "items")
@Getter
@Setter
@ToString
public class Inventory {
	
	// Use generated ID
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	// Item name
	@NotNull
	public String name;
	
	// Item description
	@NotNull
	private String description;
	
	// Item price
	@NotNull
	private int price;
	
	private String img_alt;
	
	// Item img
	@NotNull
	private String img;
	
	// Item stock
	@NotNull
	private int stock;
	
	public Inventory() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Inventory(long id) {
		this.id = id;
	}
	
	public Inventory(String name, String description, int price, String img_alt, String img, int stock) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.img_alt = img_alt;
		this.img = img;
		this.stock = stock;
	}
	
}