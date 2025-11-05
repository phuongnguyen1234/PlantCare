package com.example.plantcare.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.plantcare.data.entity.Plant;

import java.util.List;

@Dao
public interface PlantDao {

    @Insert
    long insert(Plant plant);

    @Update
    void update(Plant plant);

    @Delete
    void delete(Plant plant);

    @Query("SELECT * FROM Plant ORDER BY name ASC")
    List<Plant> getAllPlants();

    @Query("SELECT * FROM Plant WHERE plantId = :id LIMIT 1")
    Plant getPlantById(int id);
}
