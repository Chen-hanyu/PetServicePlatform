package com.petplatform.integration;

import com.petplatform.entity.CartItem;
import com.petplatform.entity.Product;
import com.petplatform.entity.ShopOrder;
import com.petplatform.entity.ShopOrderItem;
import com.petplatform.mapper.CartItemMapper;
import com.petplatform.mapper.ProductMapper;
import com.petplatform.mapper.ShopOrderItemMapper;
import com.petplatform.mapper.ShopOrderMapper;
import com.petplatform.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {
        "classpath:sql/integration-test-schema.sql",
        "classpath:sql/shop-integration-data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ShopOrderIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private ShopOrderMapper shopOrderMapper;

    @Autowired
    private ShopOrderItemMapper shopOrderItemMapper;

    @Test
    @DisplayName("下单后应创建订单明细、扣减库存并清空已下单购物车项")
    void createOrderShouldPersistOrderAndUpdateInventory() throws Exception {
        mockMvc.perform(post("/api/v1/shop/orders")
                        .with(currentUser(2L))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "item_ids": [1, 2],
                                  "receiver_name": "李女士",
                                  "receiver_phone": "13800000001",
                                  "receiver_address": "上海市浦东新区示例路 188 号",
                                  "remark": "请工作日送达"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.total_amount").value(129.70));

        Product firstProduct = productMapper.selectById(1L);
        Product secondProduct = productMapper.selectById(2L);
        assertThat(firstProduct.getStock()).isEqualTo(8);
        assertThat(secondProduct.getStock()).isEqualTo(4);

        List<CartItem> remainingItems = cartItemMapper.selectList(null);
        assertThat(remainingItems).isEmpty();

        List<ShopOrder> orders = shopOrderMapper.selectList(null);
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getOrderNo()).startsWith("PSP");

        List<ShopOrderItem> orderItems = shopOrderItemMapper.selectList(null);
        assertThat(orderItems).hasSize(2);
    }
}
