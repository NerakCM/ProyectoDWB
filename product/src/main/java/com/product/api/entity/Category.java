package com.product.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Clase Category: Clase que representa a una categoría.
 * @author Karen Cristóbal Morales.
 * @version 1.0, Febrero de 2023.
 */

@Entity
@Table(name ="category")
public class Category {
	
	// ---------------- ATRIBUTOS --------------------

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "category_id")
		private Integer category_id;

		@NotNull
		@Column(name = "category")
		private String category;

		@Column(name = "acronym")
		private String acronym;
		
		@Column(name = "status")
		@Min(value = 0, message="status must be 0 or 1")
		@Max(value = 1, message="status must be 0 or 1")
		@JsonIgnore
		private Integer status;


		// ---------------- CONSTRUCTORES --------------------
		
		/** 
		 * Constructor de una categoría por omisión.
		 */
		public Category(){
		}

		/** 
		 * Constructor de una categoría con parámetros
		 */
		public Category(Integer category_id, String category, String acronym, Integer status){
			this.category_id = category_id;
			this.category = category;
			this.acronym = acronym;
			this.status = status;
		}

		/**
		 * Nos permite obtener el id de una categoría.
		 * @return id de la categoría.
		 */
		public Integer getCategory_id() {
			return category_id;
		}

		/**
		 * Modifica el id de una categoría.
		 * @param category_id es el nuevo id que tendrá la categoría.
		 */
		public void setCategory_id(Integer category_id) {
			this.category_id = category_id;
		}

		/**
		 * Nos permite obtener el nombre de una categoría.
		 * @return información de la categoría.
		 */
		public String getCategory() {
			return category;
		}

		/**
		 * Modifica el nombre de la categoría.
		 * @param category es la nueva categoría que tendrá la categoría.
		 */
		public void setCategory(String category) {
			this.category = category;
		}

		/**
		 * Nos permite obtener el acrónimo de una categoría.
		 * @return acrónimo de la categoría.
		 */
		public String getAcronym() {
			return acronym;
		}

		/**
		 * Modifica el acrónimo de la categoría.
		 * @param acronym es el nuevo acrónimo que tendrá la categoría.
		 */
		public void setAcronym(String acronym) {
			this.acronym = acronym;
		}

		/**
		 * Nos permite obtener el estaus de una categoría.
		 * @return estatus de la categoría.
		 */
		public Integer getStatus() {
			return status;
		}

		/**
		 * Modifica el estatus de la categoría.
		 * @param status es el nuevo estatus que tendrá la categoría.
		 */
		public void setStatus(Integer status) {
			this.status = status;
		}

		@Override
		public String toString() {
			return "Category [category_id=" + category_id + ", category=" + category + ", "
					+ "acronym=" + acronym + ", status=" + status + "]";
		}	
}
