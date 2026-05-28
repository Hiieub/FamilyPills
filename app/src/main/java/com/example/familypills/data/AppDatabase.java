package com.example.familypills.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.familypills.data.dao.MedicineDao;
import com.example.familypills.data.dao.ReminderDao;
import com.example.familypills.data.model.Medicine;
import com.example.familypills.data.model.Reminder;

@Database(entities = {Medicine.class, Reminder.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract MedicineDao medicineDao();
    public abstract ReminderDao reminderDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "family_pills_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
