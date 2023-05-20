package com.product.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.product.api.dto.ApiResponse;
import com.product.api.dto.DtoProductList;
import com.product.api.entity.Category;
import com.product.api.entity.Product;
import com.product.api.repository.RepoCategory;
import com.product.api.repository.RepoProduct;
import com.product.api.repository.RepoProductList;
import com.product.exception.ApiException;

@Service
public class SvcProductImp implements SvcProduct {

	@Autowired
	RepoProduct repo;
	
	@Autowired
	RepoCategory repoCategory;
	
	@Autowired
	RepoProductList repoProductList;
	
	@Override
	public List<DtoProductList> getProducts(Integer category_id) {
		return repoProductList.findByCategoryId(category_id);
	}

	@Override
	public ApiResponse updateProductCategory(Category category, String gtin) {
		Product product = getProduct(gtin);
		Category categoryExist = (Category) repoCategory.findByCategoryId(category.getCategory_id());
		if(product == null) {
			throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");	
		} else if(categoryExist == null) {
			throw new ApiException(HttpStatus.NOT_FOUND, "category not found");
		} else {
			if(repo.updateProductCategory(category.getCategory_id(), gtin) > 0) {
				return new ApiResponse("product category updated");
			} else {
				throw new ApiException(HttpStatus.BAD_REQUEST, "product category cannot be updated");
			}
		}
	}
	

	@Override
	public Product getProduct(String gtin) {
		Product product = repo.findByGtinAndStatus(gtin); 
		if (product != null) { 
			product.setCategory(repoCategory.findByCategoryId(product.getCategory_id()));
			return product;
		}else
			throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");
	}

	@Override
	public ApiResponse createProduct(Product in) {
		Product productByName = (Product) repo.findByProduct(in.getProduct());
		Product productByGtin = (Product) repo.findByGtin(in.getGtin());
		Category categoryExist = (Category) repoCategory.findByCategoryId(in.getCategory_id());
		
		if(categoryExist == null) {
			throw new ApiException(HttpStatus.NOT_FOUND, "category not found");
		}
		
		if(productByGtin != null) {
			if(productByGtin.getStatus() == 0) {
				repo.activateProduct(productByGtin.getGtin(), in.getProduct(), 
						in.getDescription(), in.getPrice(), in.getStock(), in.getCategory_id());
				return new ApiResponse("product activated");
			} else {
				throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exist");
			}	  		
		} 
		
		if(productByName != null) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "product name already exist");
		}
		repo.createProduct(in.getGtin(), in.getProduct(), in.getDescription(), in.getPrice(), in.getStock(), in.getCategory_id());		
		return new ApiResponse("product created");
	}
	

	@Override
	public ApiResponse updateProduct(Product in, Integer id) {
		Integer updated = 0;
		Category categoryExist = (Category) repoCategory.findByCategoryId(in.getCategory_id());
		
		if(categoryExist == null) {
			throw new ApiException(HttpStatus.NOT_FOUND, "category not found");
		}
		try {
			updated = repo.updateProduct(id, in.getGtin(), in.getProduct(), in.getDescription(), in.getPrice(), in.getStock(), in.getCategory_id());
		}catch (DataIntegrityViolationException e) {
			if (e.getLocalizedMessage().contains("gtin"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product gtin already exist");
			if (e.getLocalizedMessage().contains("product"))
				throw new ApiException(HttpStatus.BAD_REQUEST, "product name already exist");
		}
		if(updated == 0)
			throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");
		else
			return new ApiResponse("product updated");
	}
	
	@Override
	public ApiResponse deleteProduct(Integer id) {
		if (repo.deleteProduct(id) > 0)
			return new ApiResponse("product removed");
		else
			throw new ApiException(HttpStatus.BAD_REQUEST, "product cannot be deleted");
	}

	@Override
	public ApiResponse updateProductStock(String gtin, Integer stock) {
		Product product = getProduct(gtin);
		if(product == null) {
			throw new ApiException(HttpStatus.NOT_FOUND, "product does not exist");
		} else {
			if(stock > product.getStock())
				throw new ApiException(HttpStatus.BAD_REQUEST, "stock to update is invalid");
			
			repo.updateProductStock(gtin, product.getStock() - stock);
			return new ApiResponse("product stock updated");
		}		
	}
}
