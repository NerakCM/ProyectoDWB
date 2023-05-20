package com.invoice.api.dto;

import javax.persistence.Entity;
import javax.persistence.Table;

/*
 * Requerimiento 3
 * Agregar atributos de clase para la validación del producto
 */

public class DtoProduct {
	
	private String gtin;
	private Integer stock;
	private double price;
	
	public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}
	
	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}
	
	public double getPrice() {
	    return price;
	}

	public void setPrice(float price) {
	    this.price = price;
	}

}
