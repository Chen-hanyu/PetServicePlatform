package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.petplatform.common.PageResponse;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.adoption.AdoptionApplicationSummaryResponse;
import com.petplatform.dto.adoption.AdoptionPetDetailResponse;
import com.petplatform.dto.adoption.AdoptionPetSummaryResponse;
import com.petplatform.dto.adoption.AdoptionProcessResponse;
import com.petplatform.dto.adoption.CreateAdoptionApplicationRequest;
import com.petplatform.dto.adoption.CreateAdoptionApplicationResponse;
import com.petplatform.entity.AdoptionApplication;
import com.petplatform.entity.AdoptionPet;
import com.petplatform.mapper.AdoptionApplicationMapper;
import com.petplatform.mapper.AdoptionPetMapper;
import com.petplatform.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AdoptionService {

    private final AdoptionPetMapper adoptionPetMapper;
    private final AdoptionApplicationMapper adoptionApplicationMapper;

    public AdoptionService(AdoptionPetMapper adoptionPetMapper, AdoptionApplicationMapper adoptionApplicationMapper) {
        this.adoptionPetMapper = adoptionPetMapper;
        this.adoptionApplicationMapper = adoptionApplicationMapper;
    }

    public PageResponse<AdoptionPetSummaryResponse> getPetPage(
            String type,
            String city,
            String gender,
            int page,
            int pageSize
    ) {
        Page<AdoptionPet> pager = new Page<>(page, pageSize);
        IPage<AdoptionPet> petPage = adoptionPetMapper.selectPage(
                pager,
                new LambdaQueryWrapper<AdoptionPet>()
                        .eq(AdoptionPet::getStatus, "ONLINE")
                        .eq(StringUtils.hasText(type), AdoptionPet::getType, type)
                        .eq(StringUtils.hasText(city), AdoptionPet::getCity, city)
                        .eq(StringUtils.hasText(gender), AdoptionPet::getGender, gender)
                        .orderByDesc(AdoptionPet::getCreatedAt)
        );
        List<AdoptionPetSummaryResponse> list = petPage.getRecords().stream()
                .map(AdoptionPetSummaryResponse::from)
                .toList();
        return new PageResponse<>(list, petPage.getTotal(), page, pageSize);
    }

    public AdoptionPetDetailResponse getPetDetail(Long petId) {
        AdoptionPet pet = adoptionPetMapper.selectById(petId);
        if (pet == null || !"ONLINE".equals(pet.getStatus())) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "待领养宠物不存在");
        }
        return AdoptionPetDetailResponse.from(pet);
    }

    public AdoptionProcessResponse getProcess() {
        return new AdoptionProcessResponse(
                List.of(
                        "浏览待领养宠物并查看详情",
                        "提交领养申请并完善联系方式与养宠经验",
                        "等待平台审核并保持电话畅通",
                        "审核通过后沟通接宠与回访安排"
                ),
                List.of(
                        "提交申请前请确认家庭成员已知情并同意领养",
                        "建议提前准备基础用品和稳定居住环境",
                        "若申请被拒绝，可根据审核备注完善资料后再次申请"
                )
        );
    }

    @Transactional
    public CreateAdoptionApplicationResponse createApplication(CreateAdoptionApplicationRequest request) {
        Long userId = SecurityUtils.getCurrentUser().id();
        AdoptionPet pet = adoptionPetMapper.selectById(request.petId());
        if (pet == null || !"ONLINE".equals(pet.getStatus())) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "待领养宠物不存在");
        }

        Long existingCount = adoptionApplicationMapper.selectCount(new LambdaQueryWrapper<AdoptionApplication>()
                .eq(AdoptionApplication::getPetId, request.petId())
                .eq(AdoptionApplication::getUserId, userId)
                .eq(AdoptionApplication::getStatus, "PENDING"));
        if (existingCount > 0) {
            throw new BusinessException(ResultCode.DUPLICATE_DATA, "请勿重复提交待审核中的领养申请");
        }

        AdoptionApplication application = new AdoptionApplication();
        application.setPetId(request.petId());
        application.setUserId(userId);
        application.setContactPhone(request.contactPhone());
        application.setExperienceDesc(request.experienceDesc().trim());
        application.setLivingConditionDesc(request.livingConditionDesc().trim());
        application.setStatus("PENDING");
        adoptionApplicationMapper.insert(application);

        return new CreateAdoptionApplicationResponse(application.getId(), application.getStatus());
    }

    public PageResponse<AdoptionApplicationSummaryResponse> getMyApplications(String status, int page, int pageSize) {
        Long userId = SecurityUtils.getCurrentUser().id();
        Page<AdoptionApplication> pager = new Page<>(page, pageSize);
        IPage<AdoptionApplication> applicationPage = adoptionApplicationMapper.selectPage(
                pager,
                new LambdaQueryWrapper<AdoptionApplication>()
                        .eq(AdoptionApplication::getUserId, userId)
                        .eq(StringUtils.hasText(status), AdoptionApplication::getStatus, status)
                        .orderByDesc(AdoptionApplication::getCreatedAt)
        );

        Map<Long, AdoptionPet> pets = loadPets(applicationPage.getRecords().stream().map(AdoptionApplication::getPetId).toList());
        List<AdoptionApplicationSummaryResponse> list = applicationPage.getRecords().stream()
                .map(application -> new AdoptionApplicationSummaryResponse(
                        application.getId(),
                        AdoptionPetSummaryResponse.from(pets.get(application.getPetId())),
                        application.getStatus(),
                        application.getReviewRemark(),
                        application.getCreatedAt()
                ))
                .toList();

        return new PageResponse<>(list, applicationPage.getTotal(), page, pageSize);
    }

    @Transactional
    public void cancelApplication(Long applicationId) {
        Long userId = SecurityUtils.getCurrentUser().id();
        AdoptionApplication application = adoptionApplicationMapper.selectById(applicationId);
        if (application == null || !application.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "领养申请不存在");
        }
        if (!"PENDING".equals(application.getStatus())) {
            throw new BusinessException(ResultCode.INVALID_OPERATION, "当前申请状态不支持撤销");
        }
        application.setStatus("CANCELLED");
        adoptionApplicationMapper.updateById(application);
    }

    private Map<Long, AdoptionPet> loadPets(List<Long> petIds) {
        List<Long> distinctIds = petIds.stream().filter(Objects::nonNull).distinct().toList();
        if (distinctIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return adoptionPetMapper.selectByIds(distinctIds).stream()
                .collect(Collectors.toMap(AdoptionPet::getId, Function.identity()));
    }
}

