package com.petplatform.service;

import com.petplatform.common.ResultCode;
import com.petplatform.common.exception.BusinessException;
import com.petplatform.dto.pet.CreatePetAlbumRequest;
import com.petplatform.dto.pet.CreatePetVaccineRequest;
import com.petplatform.dto.pet.CreatePetWeightRequest;
import com.petplatform.dto.pet.SavePetRequest;
import com.petplatform.entity.Pet;
import com.petplatform.entity.PetAlbum;
import com.petplatform.entity.PetVaccine;
import com.petplatform.entity.PetWeight;
import com.petplatform.mapper.PetAlbumMapper;
import com.petplatform.mapper.PetMapper;
import com.petplatform.mapper.PetVaccineMapper;
import com.petplatform.mapper.PetWeightMapper;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetMapper petMapper;

    @Mock
    private PetVaccineMapper petVaccineMapper;

    @Mock
    private PetWeightMapper petWeightMapper;

    @Mock
    private PetAlbumMapper petAlbumMapper;

    @InjectMocks
    private PetService petService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("我的宠物列表应只返回当前用户宠物")
    void shouldReturnMyPets() {
        mockCurrentUser(20L);
        when(petMapper.selectList(any())).thenReturn(List.of(pet(1L, 20L)));

        assertThat(petService.getMyPets())
                .extracting("name")
                .containsExactly("Mimi");
    }

    @Test
    @DisplayName("创建和更新宠物时应保存当前用户和表单字段")
    void shouldCreateAndUpdatePet() {
        mockCurrentUser(20L);
        AtomicReference<Pet> inserted = new AtomicReference<>();
        doAnswer(invocation -> {
            Pet pet = invocation.getArgument(0);
            pet.setId(1L);
            inserted.set(pet);
            return 1;
        }).when(petMapper).insert(any(Pet.class));
        when(petMapper.selectById(1L)).thenAnswer(invocation -> inserted.get());

        assertThat(petService.createPet(savePetRequest()).name()).isEqualTo("Mimi");
        assertThat(inserted.get().getUserId()).isEqualTo(20L);
        assertThat(inserted.get().getType()).isEqualTo("CAT");

        Pet existing = pet(1L, 20L);
        when(petMapper.selectById(1L)).thenReturn(existing);
        assertThat(petService.updatePet(1L, new SavePetRequest(
                " Momo ",
                "DOG",
                "Corgi",
                "MALE",
                LocalDate.of(2024, 2, 1),
                new BigDecimal("9.10"),
                "/uploads/momo.png",
                "updated"
        )).name()).isEqualTo("Momo");
        assertThat(existing.getBreed()).isEqualTo("Corgi");
        verify(petMapper).updateById(existing);
    }

    @Test
    @DisplayName("宠物详情和时间线应合并疫苗、体重和相册记录")
    void shouldReturnPetDetailAndTimeline() {
        mockCurrentUser(20L);
        when(petMapper.selectById(1L)).thenReturn(pet(1L, 20L));
        when(petVaccineMapper.selectList(any())).thenReturn(List.of(vaccine(1L, "狂犬疫苗", null)));
        when(petWeightMapper.selectList(any())).thenReturn(List.of(weight(2L, new BigDecimal("4.30"))));
        when(petAlbumMapper.selectList(any())).thenReturn(List.of(album(3L, "")));

        assertThat(petService.getPetDetail(1L).vaccines())
                .extracting("vaccineName")
                .containsExactly("狂犬疫苗");
        assertThat(petService.getTimeline(1L).events())
                .extracting("type")
                .containsExactly("VACCINE", "WEIGHT", "ALBUM");
    }

    @Test
    @DisplayName("新增疫苗、体重和相册记录应写入宠物ID")
    void shouldCreatePetRecords() {
        mockCurrentUser(20L);
        when(petMapper.selectById(1L)).thenReturn(pet(1L, 20L));
        AtomicReference<PetVaccine> vaccineRef = new AtomicReference<>();
        AtomicReference<PetWeight> weightRef = new AtomicReference<>();
        AtomicReference<PetAlbum> albumRef = new AtomicReference<>();
        doAnswer(invocation -> {
            PetVaccine vaccine = invocation.getArgument(0);
            vaccine.setId(11L);
            vaccineRef.set(vaccine);
            return 1;
        }).when(petVaccineMapper).insert(any(PetVaccine.class));
        doAnswer(invocation -> {
            PetWeight weight = invocation.getArgument(0);
            weight.setId(12L);
            weightRef.set(weight);
            return 1;
        }).when(petWeightMapper).insert(any(PetWeight.class));
        doAnswer(invocation -> {
            PetAlbum album = invocation.getArgument(0);
            album.setId(13L);
            albumRef.set(album);
            return 1;
        }).when(petAlbumMapper).insert(any(PetAlbum.class));
        when(petVaccineMapper.selectById(11L)).thenAnswer(invocation -> vaccineRef.get());
        when(petWeightMapper.selectById(12L)).thenAnswer(invocation -> weightRef.get());
        when(petAlbumMapper.selectById(13L)).thenAnswer(invocation -> albumRef.get());

        assertThat(petService.createVaccine(1L, new CreatePetVaccineRequest(
                " 狂犬疫苗 ",
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2027, 4, 1),
                "done"
        )).id()).isEqualTo(11L);
        assertThat(petService.createWeight(1L, new CreatePetWeightRequest(
                new BigDecimal("4.50"),
                LocalDateTime.of(2026, 4, 2, 10, 0)
        )).id()).isEqualTo(12L);
        assertThat(petService.createAlbum(1L, new CreatePetAlbumRequest("/uploads/pet.png", "sunny")).id())
                .isEqualTo(13L);
        assertThat(weightRef.get().getPetId()).isEqualTo(1L);
        verify(petMapper).updateById(any(Pet.class));
    }

    @Test
    @DisplayName("删除宠物应先删除关联记录再删除宠物")
    void shouldDeletePetWithRelatedRecords() {
        mockCurrentUser(20L);
        when(petMapper.selectById(1L)).thenReturn(pet(1L, 20L));

        petService.deletePet(1L);

        verify(petAlbumMapper).delete(any());
        verify(petWeightMapper).delete(any());
        verify(petVaccineMapper).delete(any());
        verify(petMapper).deleteById(1L);
    }

    @Test
    @DisplayName("访问不存在或他人宠物应抛出业务异常")
    void shouldRejectMissingOrForeignPet() {
        mockCurrentUser(20L);
        when(petMapper.selectById(404L)).thenReturn(null);
        when(petMapper.selectById(2L)).thenReturn(pet(2L, 99L));

        assertThatThrownBy(() -> petService.getPetDetail(404L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.RESOURCE_NOT_FOUND.getCode()));
        assertThatThrownBy(() -> petService.getPetDetail(2L))
                .isInstanceOf(BusinessException.class)
                .satisfies(exception -> assertThat(((BusinessException) exception).getCode())
                        .isEqualTo(ResultCode.FORBIDDEN.getCode()));
    }

    private void mockCurrentUser(Long userId) {
        CurrentUser currentUser = new CurrentUser(userId, "USER", "13800000000");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null, currentUser.authorities())
        );
    }

    private SavePetRequest savePetRequest() {
        return new SavePetRequest(
                " Mimi ",
                "CAT",
                "British Shorthair",
                "FEMALE",
                LocalDate.of(2025, 1, 1),
                new BigDecimal("4.20"),
                "/uploads/mimi.png",
                "gentle"
        );
    }

    private Pet pet(Long id, Long userId) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setUserId(userId);
        pet.setName("Mimi");
        pet.setType("CAT");
        pet.setBreed("British Shorthair");
        pet.setGender("FEMALE");
        pet.setBirthday(LocalDate.of(2025, 1, 1));
        pet.setWeight(new BigDecimal("4.20"));
        pet.setAvatarUrl("/uploads/mimi.png");
        pet.setDescription("gentle");
        pet.setCreatedAt(LocalDateTime.of(2026, 4, 1, 9, 0));
        return pet;
    }

    private PetVaccine vaccine(Long id, String name, String remark) {
        PetVaccine vaccine = new PetVaccine();
        vaccine.setId(id);
        vaccine.setPetId(1L);
        vaccine.setVaccineName(name);
        vaccine.setVaccinatedAt(LocalDate.of(2026, 3, 1));
        vaccine.setNextDueAt(LocalDate.of(2027, 3, 1));
        vaccine.setRemark(remark);
        vaccine.setCreatedAt(LocalDateTime.of(2026, 3, 1, 9, 0));
        return vaccine;
    }

    private PetWeight weight(Long id, BigDecimal value) {
        PetWeight weight = new PetWeight();
        weight.setId(id);
        weight.setPetId(1L);
        weight.setWeight(value);
        weight.setRecordedAt(LocalDateTime.of(2026, 3, 2, 9, 0));
        weight.setCreatedAt(LocalDateTime.of(2026, 3, 2, 9, 0));
        return weight;
    }

    private PetAlbum album(Long id, String caption) {
        PetAlbum album = new PetAlbum();
        album.setId(id);
        album.setPetId(1L);
        album.setImageUrl("/uploads/album.png");
        album.setCaption(caption);
        album.setCreatedAt(LocalDateTime.of(2026, 3, 3, 9, 0));
        return album;
    }
}
