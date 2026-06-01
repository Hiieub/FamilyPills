package com.example.familypills.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.familypills.R;
import com.example.familypills.data.model.ApiResponse;
import com.example.familypills.data.model.StatsResponse;
import com.example.familypills.data.repository.MedicineRepository;
import com.example.familypills.ui.main.MainActivity;
import com.example.familypills.utils.Constants;

import java.io.IOException;

import retrofit2.Response;

public class MedicineNotificationWorker extends Worker {

    private static final String TAG = "MedicineNotifWorker";

    public MedicineNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        MedicineRepository repository = new MedicineRepository(context);

        try {
            // Execute synchronous API call
            Response<ApiResponse<StatsResponse>> response = repository.getStats(context).execute();

            if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                StatsResponse stats = response.body().getData();
                
                if (stats.getExpiredCount() > 0) {
                    showNotification(
                            Constants.NOTIFICATION_ID_EXPIRED,
                            "Thuốc đã hết hạn",
                            "Bạn có " + stats.getExpiredCount() + " loại thuốc đã hết hạn. Hãy kiểm tra tủ thuốc ngay!"
                    );
                }

                if (stats.getExpiredSoon() > 0) {
                    showNotification(
                            Constants.NOTIFICATION_ID_EXPIRING_SOON,
                            "Thuốc sắp hết hạn",
                            "Bạn có " + stats.getExpiredSoon() + " loại thuốc sắp hết hạn!"
                    );
                }

                return Result.success();
            } else {
                Log.e(TAG, "API call failed or empty response");
                return Result.retry();
            }
        } catch (IOException e) {
            Log.e(TAG, "Network error", e);
            return Result.retry();
        } catch (Exception e) {
            Log.e(TAG, "Unknown error", e);
            return Result.failure();
        }
    }

    private void showNotification(int notificationId, String title, String content) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "medicine_alerts")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        try {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(notificationId, builder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Permission POST_NOTIFICATIONS missing", e);
        }
    }
}
