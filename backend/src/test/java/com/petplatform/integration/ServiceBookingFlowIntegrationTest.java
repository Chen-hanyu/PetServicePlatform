package com.petplatform.integration;

import com.petplatform.entity.ServiceBooking;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {
        "classpath:sql/integration-test-schema.sql",
        "classpath:sql/service-integration-data.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ServiceBookingFlowIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceBookingMapper serviceBookingMapper;

    @Test
    @DisplayName("同商家同服务同时间已有待处理预约时应拒绝新预约")
    void createBookingShouldRejectConflictingBookingTime() throws Exception {
        mockMvc.perform(post("/api/v1/services/bookings")
                        .with(currentUser(2L))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "merchant_id": 1,
                                  "merchant_service_id": 1,
                                  "booking_time": "2026-03-20T14:00:00",
                                  "contact_name": "李女士",
                                  "contact_phone": "13800000001",
                                  "remark": "想预约洗护"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(10010))
                .andExpect(jsonPath("$.message").value("预约时间冲突"));
    }

    @Test
    @DisplayName("用户取消自己的预约后应更新为已取消")
    void cancelBookingShouldUpdateBookingStatus() throws Exception {
        mockMvc.perform(post("/api/v1/services/bookings/1/cancel")
                        .with(currentUser(2L)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));

        ServiceBooking booking = serviceBookingMapper.selectById(1L);
        assertThat(booking.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    @DisplayName("创建不冲突预约后应成功入库")
    void createBookingShouldPersistWhenTimeIsAvailable() throws Exception {
        mockMvc.perform(post("/api/v1/services/bookings")
                        .with(currentUser(2L))
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "merchant_id": 1,
                                  "merchant_service_id": 1,
                                  "booking_time": "2026-03-21T16:00:00",
                                  "contact_name": "李女士",
                                  "contact_phone": "13800000001",
                                  "remark": "预约周末洗护"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("PENDING"));

        List<ServiceBooking> bookings = serviceBookingMapper.selectList(null);
        assertThat(bookings).hasSize(3);
        assertThat(bookings)
                .anyMatch(booking ->
                        booking.getUserId().equals(2L)
                                && booking.getBookingTime().toString().equals("2026-03-21T16:00")
                                && "PENDING".equals(booking.getStatus()));
    }
}
