package com.ghjansen.checkout;

import com.ghjansen.checkout.persistence.model.Product;
import com.ghjansen.checkout.persistence.model.Promotion;
import com.ghjansen.checkout.service.CartService;
import com.ghjansen.checkout.service.ProductService;
import com.ghjansen.checkout.service.PromotionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CheckoutApplication {

    public static void main(String args[]){
        SpringApplication.run(CheckoutApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(ProductService productService, PromotionService promotionService){
        return args -> {
            //create products
            Product voucher = productService.save(new Product("VOUCHER", "Voucher", 5.00D, "http://placehold.it/200x100"));
            Product tshirt = productService.save(new Product("TSHIRT","T-Shirt", 20.00D, "http://placehold.it/200x100"));
            productService.save(new Product("MUG","Coffee Mug", 7.50D, "http://placehold.it/200x100"));
            //create promotions
            promotionService.save(new Promotion("Buy 2 vouchers get 1 free", voucher.getId(),2L,0.5D,true));
            promotionService.save(new Promotion("Buy 3 or more t-shirts, pay 19.00\u20ac each", tshirt.getId(), 3L, 0.95D, false));
        };
    }

}
