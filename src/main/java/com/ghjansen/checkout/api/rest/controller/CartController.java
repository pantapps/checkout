package com.ghjansen.checkout.api.rest.controller;

import com.ghjansen.checkout.persistence.model.Cart;
import com.ghjansen.checkout.service.CartService;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping(value = {""})
    public @NotNull Iterable<Cart> getCarts(){
        synchronized (this.cartService){
            return this.cartService.getAllCarts();
        }
    }

    @PostMapping(value = {""})
    public @NotNull Cart createCart(){
        synchronized (this.cartService){
            return this.cartService.create();
        }
    }

    @GetMapping(value = {"/{id}"})
    public Cart getCart(@PathVariable("id") Long id){
        synchronized (this.cartService){
            return this.cartService.getCart(id);
        }
    }

}