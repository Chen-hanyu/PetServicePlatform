package com.petplatform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.shop.AddCartItemRequest;
import com.petplatform.dto.shop.CreateOrderRequest;
import com.petplatform.dto.shop.OrderDetailResponse;
import com.petplatform.dto.shop.OrderSummaryResponse;
import com.petplatform.dto.shop.UpdateCartItemRequest;
import com.petplatform.entity.CartItem;
import com.petplatform.entity.Product;
import com.petplatform.entity.ProductCategory;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    @DisplayName("分类、商品列表和商品详情应返回在售商品")
    void shouldReturnCategoriesProductPageAndDetail() {
        ProductCategory category = new ProductCategory();
        category.setId(1L);
        category.setName("主粮");
        category.setPetType("CAT");
        category.setSort(1);
        category.setStatus("ACTIVE");
        Product product = product(101L, "猫粮", "39.90", 10);
        Page<Product> page = new Page<>(1, 10);
        page.setRecords(List.of(product));
        page.setTotal(1);
        when(productCategoryMapper.selectList(any())).thenReturn(List.of(category));
        when(productMapper.selectPage(any(), any())).thenReturn(page);
        when(productMapper.selectById(101L)).thenReturn(product);

        assertThat(shopService.getCategories()).extracting("name").containsExactly("主粮");
        assertThat(shopService.getProductPage(1L, "猫", "price_asc", "CAT", 1, 10).list())
                .extracting("name")
                .containsExactly("猫粮");
        assertThat(shopService.getProductDetail(101L).name()).isEqualTo("猫粮");
    }

    @Test
    @DisplayName("购物车查询和更新应计算勾选商品总价")
    void shouldReturnAndUpdateCart() {
        mockCurrentUser(7L);
        Product product = product(101L, "猫粮", "39.90", 10);
        CartItem item = cartItem(8L, 7L, 101L, 2, true);
        when(cartItemMapper.selectList(any())).thenReturn(List.of(item));
        when(productMapper.selectByIds(List.of(101L))).thenReturn(List.of(product));
        when(cartItemMapper.selectById(8L)).thenReturn(item);
        when(productMapper.selectById(101L)).thenReturn(product);

        assertThat(shopService.getCart().totalAmount()).isEqualByComparingTo("79.80");
        var response = shopService.updateCartItem(8L, new UpdateCartItemRequest(3, false));

        assertThat(response.items().get(0).checked()).isFalse();
        verify(cartItemMapper).updateById(item);
    }

    @Test
    @DisplayName("并发首次加购触发唯一键冲突时应合并数量")
    void shouldMergeQuantityWhenConcurrentInsertConflicts() {
        mockCurrentUser(7L);
        Product product = product(101L, "猫粮", "39.90", 10);

        when(productMapper.selectById(101L)).thenReturn(product);
        when(cartItemMapper.selectOne(any())).thenReturn(null, cartItem(9L, 7L, 101L, 2, true));
        when(cartItemMapper.insert(any(CartItem.class))).thenThrow(new DuplicateKeyException("duplicate"));
        when(cartItemMapper.lockById(9L)).thenReturn(9L);
        when(cartItemMapper.selectById(9L)).thenReturn(cartItem(9L, 7L, 101L, 2, true));
        when(cartItemMapper.updateById(any(CartItem.class))).thenReturn(1);
        when(cartItemMapper.selectList(any())).thenReturn(List.of(cartItem(9L, 7L, 101L, 4, true)));
        when(productMapper.selectByIds(List.of(101L))).thenReturn(List.of(product));

        var response = shopService.addCartItem(new AddCartItemRequest(101L, 2));

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).quantity()).isEqualTo(4);
        verify(cartItemMapper).lockById(9L);
    }

    @Test
    @DisplayName("首次加购成功时应插入购物车项")
    void shouldInsertCartItemWhenNotExisting() {
        mockCurrentUser(7L);
        Product product = product(101L, "猫粮", "39.90", 10);
        when(productMapper.selectById(101L)).thenReturn(product);
        when(cartItemMapper.selectOne(any())).thenReturn(null);
        when(cartItemMapper.selectList(any())).thenReturn(List.of(cartItem(8L, 7L, 101L, 2, true)));
        when(productMapper.selectByIds(List.of(101L))).thenReturn(List.of(product));

        assertThat(shopService.addCartItem(new AddCartItemRequest(101L, 2)).items())
                .extracting("quantity")
                .containsExactly(2);
        verify(cartItemMapper).insert(any(CartItem.class));
    }

    @Test
    @DisplayName("已存在购物车项时应加锁后累加数量")
    void shouldLockAndRefreshExistingCartItemBeforeUpdate() {
        mockCurrentUser(7L);
        Product product = product(101L, "猫粮", "39.90", 10);

        when(productMapper.selectById(101L)).thenReturn(product);
        when(cartItemMapper.selectOne(any())).thenReturn(cartItem(8L, 7L, 101L, 1, true));
        when(cartItemMapper.lockById(8L)).thenReturn(8L);
        when(cartItemMapper.selectById(8L)).thenReturn(cartItem(8L, 7L, 101L, 1, true));
        when(cartItemMapper.updateById(any(CartItem.class))).thenReturn(1);
        when(cartItemMapper.selectList(any())).thenReturn(List.of(cartItem(8L, 7L, 101L, 3, true)));
        when(productMapper.selectByIds(List.of(101L))).thenReturn(List.of(product));

        var response = shopService.addCartItem(new AddCartItemRequest(101L, 2));

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).quantity()).isEqualTo(3);
        verify(cartItemMapper).lockById(8L);
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
    @DisplayName("库存不足时不允许下单")
    void shouldRejectOrderWhenStockIsInsufficient() {
        mockCurrentUser(7L);
        CartItem cartItem = cartItem(1L, 7L, 101L, 20, true);
        Product product = product(101L, "猫粮", "39.90", 10);
        when(cartItemMapper.selectByIds(List.of(1L))).thenReturn(List.of(cartItem));
        when(productMapper.selectByIds(List.of(101L))).thenReturn(List.of(product));

        assertThatThrownBy(() -> shopService.createOrder(new CreateOrderRequest(
                List.of(1L),
                "张三",
                "13800000000",
                "上海市徐汇区",
                "请白天派送"
        )))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.OUT_OF_STOCK.getCode()));
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
        when(productMapper.decrementStockSafely(101L, 2)).thenReturn(1);
        when(productMapper.decrementStockSafely(102L, 1)).thenReturn(1);
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
        verify(productMapper).decrementStockSafely(101L, 2);
        verify(productMapper).decrementStockSafely(102L, 1);
        verify(cartItemMapper).deleteById(1L);
        verify(cartItemMapper).deleteById(2L);

        ArgumentCaptor<ShopOrderItem> orderItemCaptor = ArgumentCaptor.forClass(ShopOrderItem.class);
        verify(shopOrderItemMapper, org.mockito.Mockito.times(2)).insert(orderItemCaptor.capture());
        assertThat(orderItemCaptor.getAllValues())
                .extracting(ShopOrderItem::getSubtotalAmount)
                .containsExactly(new BigDecimal("79.80"), new BigDecimal("29.90"));
    }

    @Test
    @DisplayName("订单列表和详情应只返回当前用户订单")
    void shouldReturnOrderPageAndDetail() {
        mockCurrentUser(7L);
        ShopOrder order = order(500L, 7L);
        Page<ShopOrder> page = new Page<>(1, 10);
        page.setRecords(List.of(order));
        page.setTotal(1);
        ShopOrderItem item = new ShopOrderItem();
        item.setId(1L);
        item.setOrderId(500L);
        item.setProductId(101L);
        item.setProductName("猫粮");
        item.setProductImageUrl("/uploads/101.png");
        item.setUnitPrice(new BigDecimal("39.90"));
        item.setQuantity(2);
        item.setSubtotalAmount(new BigDecimal("79.80"));
        when(shopOrderMapper.selectPage(any(), any())).thenReturn(page);
        when(shopOrderMapper.selectById(500L)).thenReturn(order);
        when(shopOrderItemMapper.selectList(any())).thenReturn(List.of(item));

        assertThat(shopService.getOrderPage("PENDING", 1, 10).list())
                .extracting("id")
                .containsExactly(500L);
        OrderDetailResponse detail = shopService.getOrderDetail(500L);
        assertThat(detail.items()).extracting("productName").containsExactly("猫粮");
    }

    @Test
    @DisplayName("访问他人订单应返回资源不存在")
    void shouldRejectForeignOrderDetail() {
        mockCurrentUser(7L);
        when(shopOrderMapper.selectById(500L)).thenReturn(order(500L, 99L));

        assertThatThrownBy(() -> shopService.getOrderDetail(500L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.RESOURCE_NOT_FOUND.getCode()));
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

    private ShopOrder order(Long id, Long userId) {
        ShopOrder order = new ShopOrder();
        order.setId(id);
        order.setUserId(userId);
        order.setOrderNo("PSP202604240001");
        order.setTotalAmount(new BigDecimal("79.80"));
        order.setPayAmount(new BigDecimal("79.80"));
        order.setStatus("PENDING");
        order.setReceiverName("张三");
        order.setReceiverPhone("13800000000");
        order.setReceiverAddress("上海市徐汇区");
        order.setCreatedAt(LocalDateTime.of(2026, 4, 24, 10, 0));
        return order;
    }
}

