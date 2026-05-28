package com.example.familypills.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.familypills.data.model.Reminder;

import java.util.List;

@Dao
public interface ReminderDao {
    @Insert
    long insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);

    @Query("SELECT * FROM reminders WHERE medicineId = :medicineId")
    LiveData<List<Reminder>> getRemindersForMedicine(int medicineId);

    @Query("SELECT * FROM reminders")
    LiveData<List<Reminder>> getAllReminders();
}
