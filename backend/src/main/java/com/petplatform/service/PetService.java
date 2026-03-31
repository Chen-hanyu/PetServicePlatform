package com.petplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.pet.CreatePetVaccineRequest;
import com.petplatform.dto.pet.CreatePetWeightRequest;
import com.petplatform.dto.pet.CreatePetAlbumRequest;
import com.petplatform.dto.pet.PetAlbumResponse;
import com.petplatform.dto.pet.PetDetailResponse;
import com.petplatform.dto.pet.PetProfileResponse;
import com.petplatform.dto.pet.PetTimelineEventResponse;
import com.petplatform.dto.pet.PetTimelineResponse;
import com.petplatform.dto.pet.PetVaccineResponse;
import com.petplatform.dto.pet.PetWeightResponse;
import com.petplatform.dto.pet.SavePetRequest;
import com.petplatform.entity.Pet;
import com.petplatform.entity.PetAlbum;
import com.petplatform.entity.PetVaccine;
import com.petplatform.entity.PetWeight;
import com.petplatform.mapper.PetAlbumMapper;
import com.petplatform.mapper.PetMapper;
import com.petplatform.mapper.PetVaccineMapper;
import com.petplatform.mapper.PetWeightMapper;
import com.petplatform.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class PetService {

    private final PetMapper petMapper;
    private final PetVaccineMapper petVaccineMapper;
    private final PetWeightMapper petWeightMapper;
    private final PetAlbumMapper petAlbumMapper;

    public PetService(
            PetMapper petMapper,
            PetVaccineMapper petVaccineMapper,
            PetWeightMapper petWeightMapper,
            PetAlbumMapper petAlbumMapper
    ) {
        this.petMapper = petMapper;
        this.petVaccineMapper = petVaccineMapper;
        this.petWeightMapper = petWeightMapper;
        this.petAlbumMapper = petAlbumMapper;
    }

    public List<PetProfileResponse> getMyPets() {
        Long userId = SecurityUtils.getCurrentUser().id();
        return petMapper.selectList(new LambdaQueryWrapper<Pet>()
                        .eq(Pet::getUserId, userId)
                        .orderByDesc(Pet::getCreatedAt))
                .stream()
                .map(PetProfileResponse::from)
                .toList();
    }

    @Transactional
    public PetProfileResponse createPet(SavePetRequest request) {
        Pet pet = new Pet();
        pet.setUserId(SecurityUtils.getCurrentUser().id());
        applyPetChanges(pet, request);
        petMapper.insert(pet);
        return PetProfileResponse.from(petMapper.selectById(pet.getId()));
    }

    @Transactional
    public void deletePet(Long petId) {
        getOwnedPetOrThrow(petId);
        petAlbumMapper.delete(new LambdaQueryWrapper<PetAlbum>().eq(PetAlbum::getPetId, petId));
        petWeightMapper.delete(new LambdaQueryWrapper<PetWeight>().eq(PetWeight::getPetId, petId));
        petVaccineMapper.delete(new LambdaQueryWrapper<PetVaccine>().eq(PetVaccine::getPetId, petId));
        petMapper.deleteById(petId);
    }

    public PetDetailResponse getPetDetail(Long petId) {
        Pet pet = getOwnedPetOrThrow(petId);
        return new PetDetailResponse(
                PetProfileResponse.from(pet),
                getVaccines(petId),
                getWeights(petId),
                getAlbums(petId)
        );
    }

    public PetTimelineResponse getTimeline(Long petId) {
        Pet pet = getOwnedPetOrThrow(petId);
        List<PetVaccine> vaccines = listVaccines(petId);
        List<PetWeight> weights = listWeights(petId);
        List<PetAlbum> albums = listAlbums(petId);

        List<PetTimelineEventResponse> events = new ArrayList<>();
        for (PetVaccine vaccine : vaccines) {
            String description = vaccine.getRemark();
            if (description == null || description.isBlank()) {
                description = vaccine.getNextDueAt() == null
                        ? "完成一次疫苗接种"
                        : "下次接种时间：" + vaccine.getNextDueAt();
            }
            events.add(new PetTimelineEventResponse(
                    "VACCINE",
                    vaccine.getVaccineName(),
                    description,
                    vaccine.getVaccinatedAt().atStartOfDay(),
                    null
            ));
        }
        for (PetWeight weight : weights) {
            events.add(new PetTimelineEventResponse(
                    "WEIGHT",
                    "体重记录",
                    "体重 " + weight.getWeight() + " kg",
                    weight.getRecordedAt(),
                    null
            ));
        }
        for (PetAlbum album : albums) {
            events.add(new PetTimelineEventResponse(
                    "ALBUM",
                    "相册记录",
                    album.getCaption() == null || album.getCaption().isBlank() ? "上传了一张宠物照片" : album.getCaption(),
                    album.getCreatedAt(),
                    album.getImageUrl()
            ));
        }
        events.sort(Comparator.comparing(PetTimelineEventResponse::occurredAt));

        return new PetTimelineResponse(PetProfileResponse.from(pet), events);
    }

    @Transactional
    public PetProfileResponse updatePet(Long petId, SavePetRequest request) {
        Pet pet = getOwnedPetOrThrow(petId);
        applyPetChanges(pet, request);
        petMapper.updateById(pet);
        return PetProfileResponse.from(petMapper.selectById(petId));
    }

    public List<PetVaccineResponse> getVaccines(Long petId) {
        getOwnedPetOrThrow(petId);
        return listVaccines(petId).stream()
                .map(PetVaccineResponse::from)
                .toList();
    }

    @Transactional
    public PetVaccineResponse createVaccine(Long petId, CreatePetVaccineRequest request) {
        getOwnedPetOrThrow(petId);
        PetVaccine vaccine = new PetVaccine();
        vaccine.setPetId(petId);
        vaccine.setVaccineName(request.vaccineName().trim());
        vaccine.setVaccinatedAt(request.vaccinatedAt());
        vaccine.setNextDueAt(request.nextDueAt());
        vaccine.setRemark(request.remark());
        petVaccineMapper.insert(vaccine);
        return PetVaccineResponse.from(petVaccineMapper.selectById(vaccine.getId()));
    }

    public List<PetWeightResponse> getWeights(Long petId) {
        getOwnedPetOrThrow(petId);
        return listWeights(petId).stream()
                .map(PetWeightResponse::from)
                .toList();
    }

    @Transactional
    public PetAlbumResponse createAlbum(Long petId, CreatePetAlbumRequest request) {
        getOwnedPetOrThrow(petId);
        PetAlbum album = new PetAlbum();
        album.setPetId(petId);
        album.setImageUrl(request.imageUrl());
        album.setCaption(request.caption());
        petAlbumMapper.insert(album);
        return PetAlbumResponse.from(petAlbumMapper.selectById(album.getId()));
    }

    @Transactional
    public PetWeightResponse createWeight(Long petId, CreatePetWeightRequest request) {
        Pet pet = getOwnedPetOrThrow(petId);
        PetWeight petWeight = new PetWeight();
        petWeight.setPetId(petId);
        petWeight.setWeight(request.weight());
        petWeight.setRecordedAt(request.recordedAt() == null ? LocalDateTime.now() : request.recordedAt());
        petWeightMapper.insert(petWeight);

        pet.setWeight(request.weight());
        petMapper.updateById(pet);

        return PetWeightResponse.from(petWeightMapper.selectById(petWeight.getId()));
    }

    private List<PetAlbumResponse> getAlbums(Long petId) {
        return listAlbums(petId).stream()
                .map(PetAlbumResponse::from)
                .toList();
    }

    private List<PetVaccine> listVaccines(Long petId) {
        return petVaccineMapper.selectList(new LambdaQueryWrapper<PetVaccine>()
                .eq(PetVaccine::getPetId, petId)
                .orderByAsc(PetVaccine::getVaccinatedAt)
                .orderByAsc(PetVaccine::getCreatedAt));
    }

    private List<PetWeight> listWeights(Long petId) {
        return petWeightMapper.selectList(new LambdaQueryWrapper<PetWeight>()
                .eq(PetWeight::getPetId, petId)
                .orderByAsc(PetWeight::getRecordedAt)
                .orderByAsc(PetWeight::getCreatedAt));
    }

    private List<PetAlbum> listAlbums(Long petId) {
        return petAlbumMapper.selectList(new LambdaQueryWrapper<PetAlbum>()
                .eq(PetAlbum::getPetId, petId)
                .orderByAsc(PetAlbum::getCreatedAt));
    }

    private Pet getOwnedPetOrThrow(Long petId) {
        Pet pet = petMapper.selectById(petId);
        if (pet == null) {
            throw new BusinessException(ResultCode.RESOURCE_NOT_FOUND, "宠物不存在");
        }
        if (!pet.getUserId().equals(SecurityUtils.getCurrentUser().id())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权访问该宠物档案");
        }
        return pet;
    }

    private void applyPetChanges(Pet pet, SavePetRequest request) {
        pet.setName(request.name().trim());
        pet.setType(request.type().trim());
        pet.setBreed(request.breed());
        pet.setGender(request.gender());
        pet.setBirthday(request.birthday());
        pet.setWeight(request.weight());
        pet.setAvatarUrl(request.avatarUrl());
        pet.setDescription(request.description());
    }
}
