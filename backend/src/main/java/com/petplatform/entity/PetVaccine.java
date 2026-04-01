package com.petplatform.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("pet_vaccines")
public class PetVaccine {

    private Long id;
    private Long petId;
    private String vaccineName;
    private LocalDate vaccinatedAt;
    private LocalDate nextDueAt;
    private String remark;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPetId() {
        return petId;
    }

    public void setPetId(Long petId) {
        this.petId = petId;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public LocalDate getVaccinatedAt() {
        return vaccinatedAt;
    }

    public void setVaccinatedAt(LocalDate vaccinatedAt) {
        this.vaccinatedAt = vaccinatedAt;
    }

    public LocalDate getNextDueAt() {
        return nextDueAt;
    }

    public void setNextDueAt(LocalDate nextDueAt) {
        this.nextDueAt = nextDueAt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
