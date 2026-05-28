package com.example.familypills.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.familypills.data.model.Medicine;

import java.util.List;

@Dao
public interface MedicineDao {
    @Insert
    long insert(Medicine medicine);

    @Update
    void update(Medicine medicine);

    @Delete
    void delete(Medicine medicine);

    @Query("SELECT * FROM medicines ORDER BY id DESC")
    LiveData<List<Medicine>> getAllMedicines();

    @Query("SELECT * FROM medicines WHERE id = :id")
    Medicine getMedicineById(int id);
}
