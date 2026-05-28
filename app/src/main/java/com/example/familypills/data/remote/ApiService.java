package com.example.familypills.data.remote;

import com.example.familypills.data.model.Medicine;
import com.example.familypills.data.model.Reminder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    // Thuốc (Medicine)
    @GET("api/medicines")
    Call<List<Medicine>> getAllMedicines();

    @POST("api/medicines")
    Call<Medicine> addMedicine(@Body Medicine medicine);

    @PUT("api/medicines/{id}")
    Call<Void> updateMedicine(@Path("id") int id, @Body Medicine medicine);

    @DELETE("api/medicines/{id}")
    Call<Void> deleteMedicine(@Path("id") int id);

    // Nhắc nhở (Reminder)
    @GET("api/reminders")
    Call<List<Reminder>> getAllReminders();
    
    @POST("api/reminders")
    Call<Reminder> addReminder(@Body Reminder reminder);
}
