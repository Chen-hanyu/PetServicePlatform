package com.petplatform.service;

import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.shop.CreateOrderRequest;
import com.petplatform.dto.shop.OrderSummaryResponse;
import com.petplatform.entity.CartItem;
import com.petplatform.entity.Product;
import com.petplatform.entity.ShopOrder;
import com.petplatform.entity.ShopOrderItem;
import com.petplatform.mapper.CartItemMapper;
import com.petplatform.mapper.ProductCategoryMapper;
import com.petplatform.mapper.ProductMapper;
import com.petplatform.mapper.ShopOrderItemMapper;
import com.petplatform.mapper.ShopOrderMapper;
import com.petplatform.security.CurrentUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    private ProductCategoryMapper productCategoryMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private ShopOrderMapper shopOrderMapper;

    @Mock
    private ShopOrderItemMapper shopOrderItemMapper;

    @InjectMocks
    private ShopService shopService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("未勾选购物车项时不允许下单")
    void shouldRejectOrderWhenCartItemsAreUnchecked() {
        mockCurrentUser(7L);
        CartItem cartItem = cartItem(1L, 7L, 101L, 2, false);
        when(cartItemMapper.selectByIds(List.of(1L))).thenReturn(List.of(cartItem));

        assertThatThrownBy(() -> shopService.createOrder(new CreateOrderRequest(
                List.of(1L),
                "张三",
                "13800000000",
                "上海市徐汇区",
                "请白天派送"
        )))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.INVALID_OPERATION.getCode()));
    }

    @Test
    @DisplayName("下单成功后应创建订单、扣减库存并移除购物车项")
    void shouldCreateOrderAndDeductStock() {
        mockCurrentUser(7L);
        CartItem firstItem = cartItem(1L, 7L, 101L, 2, true);
        CartItem secondItem = cartItem(2L, 7L, 102L, 1, true);
        Product firstProduct = product(101L, "猫粮", "39.90", 10);
        Product secondProduct = product(102L, "猫砂", "29.90", 5);

        when(cartItemMapper.selectByIds(List.of(1L, 2L))).thenReturn(List.of(firstItem, secondItem));
        when(productMapper.selectByIds(List.of(101L, 102L))).thenReturn(List.of(firstProduct, secondProduct));
        ShopOrder persistedOrder = new ShopOrder();
        doAnswer(invocation -> {
            ShopOrder order = invocation.getArgument(0);
            order.setId(500L);
            persistedOrder.setId(order.getId());
            persistedOrder.setOrderNo(order.getOrderNo());
            persistedOrder.setTotalAmount(order.getTotalAmount());
            persistedOrder.setPayAmount(order.getPayAmount());
            persistedOrder.setStatus(order.getStatus());
            persistedOrder.setCreatedAt(order.getCreatedAt());
            return 1;
        }).when(shopOrderMapper).insert(any(ShopOrder.class));
        when(shopOrderMapper.selectById(500L)).thenReturn(persistedOrder);

        OrderSummaryResponse response = shopService.createOrder(new CreateOrderRequest(
                List.of(1L, 2L),
                "张三",
                "13800000000",
                "上海市徐汇区",
                "请白天派送"
        ));

        assertThat(response.id()).isEqualTo(500L);
        assertThat(response.status()).isEqualTo("PENDING");
        assertThat(response.totalAmount()).isEqualByComparingTo("109.70");
        assertThat(response.orderNo()).startsWith("PSP");
        assertThat(firstProduct.getStock()).isEqualTo(8);
        assertThat(secondProduct.getStock()).isEqualTo(4);
        verify(productMapper).updateById(firstProduct);
        verify(productMapper).updateById(secondProduct);
        verify(cartItemMapper).deleteById(1L);
        verify(cartItemMapper).deleteById(2L);

        ArgumentCaptor<ShopOrderItem> orderItemCaptor = ArgumentCaptor.forClass(ShopOrderItem.class);
        verify(shopOrderItemMapper, org.mockito.Mockito.times(2)).insert(orderItemCaptor.capture());
        assertThat(orderItemCaptor.getAllValues())
                .extracting(ShopOrderItem::getSubtotalAmount)
                .containsExactly(new BigDecimal("79.80"), new BigDecimal("29.90"));
    }

    private void mockCurrentUser(Long userId) {
        CurrentUser currentUser = new CurrentUser(userId, "USER", "13800000000");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.authorities())
        );
    }

    private CartItem cartItem(Long id, Long userId, Long productId, int quantity, boolean checked) {
        CartItem item = new CartItem();
        item.setId(id);
        item.setUserId(userId);
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setChecked(checked);
        return item;
    }

    private Product product(Long id, String name, String price, int stock) {
        Product product = new Product();
        product.setId(id);
        product.setCategoryId(1L);
        product.setName(name);
        product.setSubtitle(name + "副标题");
        product.setImageUrl("/uploads/" + id + ".png");
        product.setPrice(new BigDecimal(price));
        product.setStock(stock);
        product.setPetType("CAT");
        product.setStatus("ON_SALE");
        return product;
    }
}

