package com.ghjansen.checkout.api.rest.controller;

import com.ghjansen.checkout.persistence.model.Product;
import com.ghjansen.checkout.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private ProductService productService;

    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    @GetMapping(value = {""})
    public @NotNull Iterable<Product> getProducts() {
        return this.productService.getAllProducts();
    }

    @GetMapping(value = {"/{id}"})
    public @NotNull Product getProduct(@PathVariable("id") Long id) {
        return this.productService.getProduct(id);
    }
}
