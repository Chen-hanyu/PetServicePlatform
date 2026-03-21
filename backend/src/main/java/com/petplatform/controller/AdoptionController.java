package com.petplatform.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.common.PageResponse;
import com.petplatform.dto.adoption.AdoptionApplicationSummaryResponse;
import com.petplatform.dto.adoption.AdoptionPetDetailResponse;
import com.petplatform.dto.adoption.AdoptionProcessResponse;
import com.petplatform.dto.adoption.AdoptionPetSummaryResponse;
import com.petplatform.dto.adoption.CreateAdoptionApplicationRequest;
import com.petplatform.dto.adoption.CreateAdoptionApplicationResponse;
import com.petplatform.service.AdoptionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/adoption")
public class AdoptionController {

    private final AdoptionService adoptionService;

    public AdoptionController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }

    @GetMapping("/pets")
    public ApiResponse<PageResponse<AdoptionPetSummaryResponse>> getPets(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String gender,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(name = "page_size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于1")
            @Max(value = 50, message = "每页数量不能超过50") int pageSize
    ) {
        return ApiResponse.success(adoptionService.getPetPage(type, city, gender, page, pageSize));
    }

    @GetMapping("/pets/{petId}")
    public ApiResponse<AdoptionPetDetailResponse> getPetDetail(@PathVariable Long petId) {
        return ApiResponse.success(adoptionService.getPetDetail(petId));
    }

    @GetMapping("/process")
    public ApiResponse<AdoptionProcessResponse> getProcess() {
        return ApiResponse.success(adoptionService.getProcess());
    }

    @PostMapping("/applications")
    public ApiResponse<CreateAdoptionApplicationResponse> createApplication(
            @Valid @RequestBody CreateAdoptionApplicationRequest request
    ) {
        return ApiResponse.success(adoptionService.createApplication(request));
    }

    @GetMapping("/applications")
    public ApiResponse<PageResponse<AdoptionApplicationSummaryResponse>> getMyApplications(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于等于1") int page,
            @RequestParam(name = "page_size", defaultValue = "10")
            @Min(value = 1, message = "每页数量必须大于等于1")
            @Max(value = 50, message = "每页数量不能超过50") int pageSize
    ) {
        return ApiResponse.success(adoptionService.getMyApplications(status, page, pageSize));
    }
}
