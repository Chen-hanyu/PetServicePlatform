package com.petplatform.service;

import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.admin.ReviewAdoptionApplicationRequest;
import com.petplatform.dto.admin.ReviewAdoptionApplicationResponse;
import com.petplatform.entity.AdoptionApplication;
import com.petplatform.entity.AdoptionPet;
import com.petplatform.mapper.AdoptionApplicationMapper;
import com.petplatform.mapper.AdoptionPetMapper;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.CurrentUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminAdoptionServiceTest {

    @Mock
    private AdoptionApplicationMapper adoptionApplicationMapper;

    @Mock
    private AdoptionPetMapper adoptionPetMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AdminAdoptionService adminAdoptionService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("审核通过领养申请后应更新申请状态并把宠物改为已领养")
    void shouldApproveApplicationAndUpdatePetStatus() {
        mockAdmin(99L);
        AdoptionApplication application = pendingApplication(10L, 3L);
        AdoptionPet pet = new AdoptionPet();
        pet.setId(3L);
        pet.setStatus("ONLINE");
        when(adoptionApplicationMapper.selectById(10L)).thenReturn(application);
        when(adoptionPetMapper.selectById(3L)).thenReturn(pet);

        ReviewAdoptionApplicationResponse response = adminAdoptionService.reviewApplication(
                10L,
                new ReviewAdoptionApplicationRequest("APPROVED", "资料完整，安排接宠")
        );

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.status()).isEqualTo("APPROVED");
        assertThat(response.reviewRemark()).isEqualTo("资料完整，安排接宠");
        assertThat(application.getStatus()).isEqualTo("APPROVED");
        assertThat(application.getReviewedBy()).isEqualTo(99L);
        assertThat(application.getReviewedAt()).isNotNull();
        assertThat(pet.getStatus()).isEqualTo("ADOPTED");
        verify(adoptionApplicationMapper).updateById(application);
        verify(adoptionPetMapper).updateById(pet);
    }

    @Test
    @DisplayName("重复审核已处理申请时应抛出已处理异常")
    void shouldRejectReviewWhenApplicationAlreadyProcessed() {
        mockAdmin(99L);
        AdoptionApplication application = pendingApplication(10L, 3L);
        application.setStatus("APPROVED");
        when(adoptionApplicationMapper.selectById(10L)).thenReturn(application);

        assertThatThrownBy(() -> adminAdoptionService.reviewApplication(
                10L,
                new ReviewAdoptionApplicationRequest("REJECTED", "重复申请")
        ))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.ALREADY_REVIEWED.getCode()));
    }

    private void mockAdmin(Long userId) {
        CurrentUser currentUser = new CurrentUser(userId, "ADMIN", "13900000000");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.authorities())
        );
    }

    private AdoptionApplication pendingApplication(Long applicationId, Long petId) {
        AdoptionApplication application = new AdoptionApplication();
        application.setId(applicationId);
        application.setPetId(petId);
        application.setStatus("PENDING");
        return application;
    }
}
