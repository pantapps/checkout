package com.ghjansen.checkout.service;

import com.ghjansen.checkout.api.rest.exception.InvalidStateException;
import com.ghjansen.checkout.api.rest.exception.ResourceNotFoundException;
import com.ghjansen.checkout.persistence.model.*;
import com.ghjansen.checkout.persistence.repository.CartRepository;
import com.ghjansen.checkout.persistence.repository.OrderPromotionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private CartRepository cartRepository;
    private PromotionService promotionService;
    private OrderService orderService;
    private OrderItemService orderItemService;
    private OrderPromotionRepository orderPromotionRepository;

    public CartServiceImpl(final CartRepository cartRepository, final PromotionService promotionService, final OrderService orderService, final OrderItemService orderItemService, final OrderPromotionRepository orderPromotionRepository) {
        this.cartRepository = cartRepository;
        this.promotionService = promotionService;
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.orderPromotionRepository = orderPromotionRepository;
    }

    @Override
    public @NotNull Cart save(final Cart cart) {
        return cartRepository.save(cart);
    }

    @Override
    public @NotNull Cart create() {
        Cart cart = new Cart();
        cart.setDateCreated(ZonedDateTime.now(ZoneId.of("UTC")));
        cart.setStatus(Cart.Status.open.name());
        cart.setCartItems(new ArrayList<>());
        cart.setCartPromotions(new ArrayList<>());
        cart.setTotalPrice(0D);
        return save(cart);
    }

    @Override
    public @NotNull Cart update(Cart cart) {
        cart = this.promotionService.applyPromotions(cart);
        return save(cart);
    }

    @Override
    public @NotNull Cart getCart(@Min(value = 1L, message = "Ivalid cart id") final Long id) {
        return this.cartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    @Override
    public @NotNull Iterable<Cart> getAllCarts() {
        return this.cartRepository.findAll();
    }

    @Override
    public @NotNull Cart closeCart(@Min(value = 1L, message = "Ivalid cart id") final Long id) {
        Cart cart = getCart(id);
        preventClosedCartChanges(cart);
        createOrderFromCart(cart);
        cart.setStatus(Cart.Status.closed.name());
        return save(cart);
    }

    private void createOrderFromCart(Cart cart) {
        Order order = this.orderService.create();
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getCartItems()) {
            OrderItem orderItem = new OrderItem(order, cartItem.getProduct(), cartItem.getQuantity());
            orderItem = this.orderItemService.save(orderItem);
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);
        ArrayList<OrderPromotion> orderPromotions = new ArrayList<>();
        for(CartPromotion cp : cart.getCartPromotions()){
            OrderPromotion op = new OrderPromotion(order, cp.getPromotion());
            this.orderPromotionRepository.save(op);
            orderPromotions.add(op);
        }
        order.setOrderPromotions(orderPromotions);
        order.setTotalPrice(cart.getTotalPrice());
        order.setCartId(cart.getId());
        this.orderService.save(order);
    }

    @SuppressWarnings("unchecked")
    public <X extends Throwable> void preventClosedCartChanges(Cart c) throws X {
        if (c.getStatus().equals(Cart.Status.closed.name())) {
            throw (X) new InvalidStateException("Cart " +
                    c.getId() + " was already closed. Requested operation rejected");
        }
    }
}
