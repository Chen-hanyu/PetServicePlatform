package com.petplatform.controller;

import com.petplatform.common.ApiResponse;
import com.petplatform.dto.pet.CreatePetVaccineRequest;
import com.petplatform.dto.pet.CreatePetWeightRequest;
import com.petplatform.dto.pet.CreatePetAlbumRequest;
import com.petplatform.dto.pet.PetDetailResponse;
import com.petplatform.dto.pet.PetProfileResponse;
import com.petplatform.dto.pet.PetTimelineResponse;
import com.petplatform.dto.pet.PetVaccineResponse;
import com.petplatform.dto.pet.PetWeightResponse;
import com.petplatform.dto.pet.PetAlbumResponse;
import com.petplatform.dto.pet.SavePetRequest;
import com.petplatform.service.PetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @GetMapping
    public ApiResponse<List<PetProfileResponse>> getMyPets() {
        return ApiResponse.success(petService.getMyPets());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PetProfileResponse>> createPet(@Valid @RequestBody SavePetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(petService.createPet(request)));
    }

    @GetMapping("/{petId}")
    public ApiResponse<PetDetailResponse> getPetDetail(@PathVariable Long petId) {
        return ApiResponse.success(petService.getPetDetail(petId));
    }

    @PutMapping("/{petId}")
    public ApiResponse<PetProfileResponse> updatePet(
            @PathVariable Long petId,
            @Valid @RequestBody SavePetRequest request
    ) {
        return ApiResponse.success(petService.updatePet(petId, request));
    }

    @DeleteMapping("/{petId}")
    public ApiResponse<Void> deletePet(@PathVariable Long petId) {
        petService.deletePet(petId);
        return ApiResponse.success();
    }

    @GetMapping("/{petId}/vaccines")
    public ApiResponse<List<PetVaccineResponse>> getVaccines(@PathVariable Long petId) {
        return ApiResponse.success(petService.getVaccines(petId));
    }

    @PostMapping("/{petId}/vaccines")
    public ApiResponse<PetVaccineResponse> createVaccine(
            @PathVariable Long petId,
            @Valid @RequestBody CreatePetVaccineRequest request
    ) {
        return ApiResponse.success(petService.createVaccine(petId, request));
    }

    @GetMapping("/{petId}/weights")
    public ApiResponse<List<PetWeightResponse>> getWeights(@PathVariable Long petId) {
        return ApiResponse.success(petService.getWeights(petId));
    }

    @GetMapping("/{petId}/timeline")
    public ApiResponse<PetTimelineResponse> getTimeline(@PathVariable Long petId) {
        return ApiResponse.success(petService.getTimeline(petId));
    }

    @PostMapping("/{petId}/albums")
    public ApiResponse<PetAlbumResponse> createAlbum(
            @PathVariable Long petId,
            @Valid @RequestBody CreatePetAlbumRequest request
    ) {
        return ApiResponse.success(petService.createAlbum(petId, request));
    }

    @PostMapping("/{petId}/weights")
    public ApiResponse<PetWeightResponse> createWeight(
            @PathVariable Long petId,
            @Valid @RequestBody CreatePetWeightRequest request
    ) {
        return ApiResponse.success(petService.createWeight(petId, request));
    }
}
