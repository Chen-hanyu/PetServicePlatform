package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.admin.AdminAdoptionApplicationResponse;
import com.petplatform.dto.admin.ReviewAdoptionApplicationRequest;
import com.petplatform.dto.admin.ReviewAdoptionApplicationResponse;
import com.petplatform.dto.adoption.AdoptionPetSummaryResponse;
import com.petplatform.dto.user.UserProfileResponse;
import com.petplatform.entity.AdoptionApplication;
import com.petplatform.entity.AdoptionPet;
import com.petplatform.entity.User;
import com.petplatform.mapper.AdoptionApplicationMapper;
import com.petplatform.mapper.AdoptionPetMapper;
import com.petplatform.mapper.UserMapper;
import com.petplatform.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdminAdoptionService {

    private final AdoptionApplicationMapper adoptionApplicationMapper;
    private final AdoptionPetMapper adoptionPetMapper;
    private final UserMapper userMapper;

    public AdminAdoptionService(
            AdoptionApplicationMapper adoptionApplicationMapper,
            AdoptionPetMapper adoptionPetMapper,
            UserMapper userMapper
    ) {
        this.adoptionApplicationMapper = adoptionApplicationMapper;
        this.adoptionPetMapper = adoptionPetMapper;
        this.userMapper = userMapper;
    }

    public PageResponse<AdminAdoptionApplicationResponse> getApplicationPage(
            String status,
            Long petId,
            int page,
            int pageSize
    ) {
        Page<AdoptionApplication> pager = new Page<>(page, pageSize);
        IPage<AdoptionApplication> applicationPage = adoptionApplicationMapper.selectPage(
                pager,
                new LambdaQueryWrapper<AdoptionApplication>()
                        .eq(StringUtils.hasText(status), AdoptionApplication::getStatus, status)
                        .eq(petId != null, AdoptionApplication::getPetId, petId)
                        .orderByDesc(AdoptionApplication::getCreatedAt)
        );

        Map<Long, AdoptionPet> pets = loadPets(applicationPage.getRecords().stream().map(AdoptionApplication::getPetId).toList());
        Map<Long, User> users = loadUsers(applicationPage.getRecords().stream().map(AdoptionApplication::getUserId).toList());

        List<AdminAdoptionApplicationResponse> list = applicationPage.getRecords().stream()
                .map(application -> new AdminAdoptionApplicationResponse(
                        application.getId(),
                        AdoptionPetSummaryResponse.from(pets.get(application.getPetId())),
                        UserProfileResponse.from(users.get(application.getUserId())),
                        application.getContactPhone(),
                        application.getExperienceDesc(),
                        application.getLivingConditionDesc(),
                        application.getStatus(),
                        application.getReviewRemark(),
                        application.getCreatedAt(),
                        application.getReviewedAt()
                ))
                .toList();

        return new PageResponse<>(list, applicationPage.getTotal(), page, pageSize);
    }

    @Transactional
    public ReviewAdoptionApplicationResponse reviewApplication(Long applicationId, ReviewAdoptionApplicationRequest request) {
        AdoptionApplication application = adoptionApplicationMapper.selectById(applicationId);
        if (application == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "领养申请不存在");
        }
        if (!"PENDING".equals(application.getStatus())) {
            throw new BusinessException(ResultCode.ALREADY_REVIEWED, "领养申请审核已处理");
        }
        if (!"APPROVED".equals(request.status()) && !"REJECTED".equals(request.status())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "审核状态仅支持 APPROVED 或 REJECTED");
        }

        application.setStatus(request.status());
        application.setReviewRemark(request.reviewRemark());
        application.setReviewedBy(SecurityUtils.getCurrentUser().id());
        application.setReviewedAt(LocalDateTime.now());
        adoptionApplicationMapper.updateById(application);

        if ("APPROVED".equals(request.status())) {
            AdoptionPet pet = adoptionPetMapper.selectById(application.getPetId());
            if (pet != null) {
                pet.setStatus("ADOPTED");
                adoptionPetMapper.updateById(pet);
            }
        }

        return new ReviewAdoptionApplicationResponse(application.getId(), application.getStatus(), application.getReviewRemark());
    }

    private Map<Long, AdoptionPet> loadPets(List<Long> petIds) {
        List<Long> distinctIds = petIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return adoptionPetMapper.selectBatchIds(distinctIds).stream()
                .collect(Collectors.toMap(AdoptionPet::getId, Function.identity()));
    }

    private Map<Long, User> loadUsers(List<Long> userIds) {
        List<Long> distinctIds = userIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(distinctIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));
    }
}
