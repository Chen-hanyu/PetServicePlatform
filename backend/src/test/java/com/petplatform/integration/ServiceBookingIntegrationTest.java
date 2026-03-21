package com.petplatform.integration;

import com.petplatform.entity.Merchant;
import com.petplatform.entity.MerchantReview;
import com.petplatform.entity.ServiceBooking;
import com.petplatform.mapper.MerchantMapper;
import com.petplatform.mapper.MerchantReviewMapper;
import com.petplatform.mapper.ServiceBookingMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {
        "classpath:sql/integration-test-schema.sql",
        "classpath:sql/service-integration-data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ServiceBookingIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceBookingMapper serviceBookingMapper;

    @Autowired
    private MerchantReviewMapper merchantReviewMapper;

    @Autowired
    private MerchantMapper merchantMapper;

    @Test
    @DisplayName("管理员处理预约后应更新预约状态和备注")
    void adminShouldUpdateBookingStatus() throws Exception {
        mockMvc.perform(put("/api/v1/admin/services/bookings/1")
                        .with(currentAdmin(1L))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "CONFIRMED",
                                  "remark": "已电话确认到店时间"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.data.remark").value("已电话确认到店时间"));

        ServiceBooking booking = serviceBookingMapper.selectById(1L);
        assertThat(booking.getStatus()).isEqualTo("CONFIRMED");
        assertThat(booking.getRemark()).isEqualTo("已电话确认到店时间");
    }

    @Test
    @DisplayName("完成服务的用户评价商家后应入库并重算商家评分")
    void completedBookingUserShouldCreateReviewAndUpdateMerchantScore() throws Exception {
        mockMvc.perform(post("/api/v1/services/merchants/1/reviews")
                        .with(currentUser(3L, "USER", "13800000002"))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "score": 5,
                                  "content": "服务耐心，洗护过程很细致"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.score").value(5))
                .andExpect(jsonPath("$.data.merchant_score").value(5.0));

        List<MerchantReview> reviews = merchantReviewMapper.selectList(null);
        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getMerchantId()).isEqualTo(1L);
        assertThat(reviews.get(0).getUserId()).isEqualTo(3L);
        assertThat(reviews.get(0).getContent()).isEqualTo("服务耐心，洗护过程很细致");

        Merchant merchant = merchantMapper.selectById(1L);
        assertThat(merchant.getScore()).isEqualByComparingTo("5.0");
    }
}
