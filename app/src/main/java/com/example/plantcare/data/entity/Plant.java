package com.example.plantcare.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.plantcare.data.enums.FrequencyUnit;

import java.time.LocalDate;

@Entity(tableName = "Plant")
public class Plant {
    @PrimaryKey(autoGenerate = true)
    private int plantId;

    @NonNull
    private String name;

    @NonNull
    private LocalDate datePlanted = LocalDate.now();

    private String imageUrl;

    private String temperatureRange; // dạng mô tả, ví dụ "20–25°C"
    private String humidityRange;    // dạng mô tả, ví dụ "60–70%"

    private int waterFrequency;
    private int fertilizerFrequency;
    private int lightFrequency;

    //unit nay la don vi tan suat (gio/lan, ngay/lan...)

    private FrequencyUnit waterUnit;
    private FrequencyUnit fertilizerUnit;
    private FrequencyUnit lightUnit;

    private String note;

    public Plant() {
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public LocalDate getDatePlanted() {
        return datePlanted;
    }

    public void setDatePlanted(@NonNull LocalDate datePlanted) {
        this.datePlanted = datePlanted;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTemperatureRange() {
        return temperatureRange;
    }

    public void setTemperatureRange(String temperatureRange) {
        this.temperatureRange = temperatureRange;
    }

    public String getHumidityRange() {
        return humidityRange;
    }

    public void setHumidityRange(String humidityRange) {
        this.humidityRange = humidityRange;
    }

    public int getWaterFrequency() {
        return waterFrequency;
    }

    public void setWaterFrequency(int waterFrequency) {
        this.waterFrequency = waterFrequency;
    }

    public int getFertilizerFrequency() {
        return fertilizerFrequency;
    }

    public void setFertilizerFrequency(int fertilizerFrequency) {
        this.fertilizerFrequency = fertilizerFrequency;
    }

    public int getLightFrequency() {
        return lightFrequency;
    }

    public void setLightFrequency(int lightFrequency) {
        this.lightFrequency = lightFrequency;
    }

    public FrequencyUnit getWaterUnit() {
        return waterUnit;
    }

    public void setWaterUnit(FrequencyUnit waterUnit) {
        this.waterUnit = waterUnit;
    }

    public FrequencyUnit getFertilizerUnit() {
        return fertilizerUnit;
    }

    public void setFertilizerUnit(FrequencyUnit fertilizerUnit) {
        this.fertilizerUnit = fertilizerUnit;
    }

    public FrequencyUnit getLightUnit() {
        return lightUnit;
    }

    public void setLightUnit(FrequencyUnit lightUnit) {
        this.lightUnit = lightUnit;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
