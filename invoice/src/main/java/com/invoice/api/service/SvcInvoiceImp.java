package com.invoice.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.invoice.api.dto.ApiResponse;
import com.invoice.api.entity.Cart;
import com.invoice.api.entity.Invoice;
import com.invoice.api.entity.Item;
import com.invoice.api.repository.RepoCart;
import com.invoice.api.repository.RepoInvoice;
import com.invoice.api.repository.RepoItem;
import com.invoice.configuration.client.ProductClient;
import com.invoice.exception.ApiException;

@Service
public class SvcInvoiceImp implements SvcInvoice {

	@Autowired
	RepoInvoice repo;
	
	@Autowired
	RepoItem repoItem;
	
	@Autowired
	RepoCart repoCart;
	
	@Autowired
	ProductClient productCl;
	

	@Override
	public List<Invoice> getInvoices(String rfc) {
		return repo.findByRfcAndStatus(rfc, 1);
	}

	@Override
	public List<Item> getInvoiceItems(Integer invoice_id) {
		return repoItem.getInvoiceItems(invoice_id);
	}

	/*
	 * Ejercicio 5
	 * Implementar el método para generar una factura. 
	 */
	@Override
	public ApiResponse generateInvoice(String rfc) {
		
		/* REQUERIMIENTO 5.1
		 * Verifica que el carrito tiene productos
		 */
		List<Cart> carritos = repoCart.findCartsByRfc(rfc);
		if (carritos.isEmpty()) {
			throw new ApiException(HttpStatus.NOT_FOUND, "cart has nos items");
		} 
		
		System.out.println("\n AQUÍ SE IMPRIMEN EL GTIN DEL LOS CARRITOS");
		for(Cart carAux: carritos) {
			System.out.println(carAux.getGtin());
		}
		
		/* REQUERIMIENTO 5.2
		 * Por cada producto se genera un articulo en la factura.
		 */
		Invoice factura = new Invoice();
		modificaValores(factura, rfc, 0.0, 0.0, 0.0, LocalDateTime.now(), 1);
		repo.save(factura);
			
		List<Item> articulos = new ArrayList<Item>();
		for (Cart car: carritos) {
			// Obtenemos los atributos del producto en un articulo auxiliar
			Item artAux = new Item();
			artAux.setId_invoice(factura.getInvoice_id());
			artAux.setGtin(car.getGtin());
			artAux.setQuantity(car.getQuantity());
			double precio = productCl.getProduct(car.getGtin()).getBody().getPrice();
			artAux.setUnit_price(precio);
			double total = car.getQuantity() * precio;
			artAux.setTotal(total);
			artAux.setTaxes(total * 0.16);
			artAux.setSubtotal(total - (artAux.getTaxes()));
			artAux.setStatus(1);
			articulos.add(artAux);
			
			/* REQUERIMIENTO 5.6
			 * Registramos la factura en la base de datos.
			 */
			repoItem.save(artAux);
			
			/* REQUERIMIENTO 5.4
			 * Actualizamos el stock del producto.
			 */
			productCl.updateProductStock(car.getGtin(),car.getQuantity());
			
			/* REQUERIMIENTO 5.5
			 * Vaciamos el carrito de compras del cliente.
			 */
			repoCart.clearCart(car.getRfc());
		}
		
		
		/* REQUERIMIENTO 5.3
		 * Generamos la factura.
		 */
		double total = 0.0;
		double impuestos = 0.0;
		double subtotal = 0.0;
		for (Item art: articulos) {
			total += art.getTotal();
			impuestos += art.getTaxes();
			subtotal += art.getSubtotal();
		}
		modificaValores(factura, rfc, impuestos, total, subtotal, LocalDateTime.now(), 1);
		
		/* REQUERIMIENTO 5.6
		 * Registramos la factura en la base de datos.
		 */
		repo.save(factura);
		
		return new ApiResponse("invoice generated");
	}

	// MÉTODO AUXILIAR QUE MODIFICA LOS VALORES DE UNA FACTURA/INVOICE
	private Invoice modificaValores(Invoice factura, String rfc, Double taxes, Double total, Double subTotal, LocalDateTime time, Integer status) {
		factura.setRfc(rfc);
		factura.setTaxes(taxes);
		factura.setTotal(total);
		factura.setSubtotal(subTotal);
		factura.setCreated_at(time);
		factura.setStatus(status);
		return factura;
	}
}
