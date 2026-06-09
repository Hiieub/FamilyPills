package com.example.familypills.ui.cabinet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.familypills.R;
import com.example.familypills.data.model.Medicine;
import com.example.familypills.data.remote.RetrofitClient;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    public interface OnMedicineActionListener {
        void onEdit(Medicine medicine);
        void onDelete(Medicine medicine);
    }

    private List<Medicine> medicineList = new ArrayList<>();
    private OnMedicineActionListener listener;
    private boolean showMenu = true;

    public void setShowMenu(boolean showMenu) {
        this.showMenu = showMenu;
    }

    public void setOnMedicineActionListener(OnMedicineActionListener listener) {
        this.listener = listener;
    }

    public void setMedicineList(List<Medicine> medicineList) {
        this.medicineList = medicineList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.tvName.setText(medicine.getName());
        bindMedicineImage(holder, medicine.getImagePath());
        
        String quantity = medicine.getQuantity();
        if (quantity != null && !quantity.isEmpty()) {
            holder.tvQuantity.setVisibility(View.VISIBLE);
            holder.tvQuantity.setText(quantity.startsWith("Còn") ? quantity : "Còn " + quantity);
        } else {
            holder.tvQuantity.setVisibility(View.GONE);
        }

        String expiryFormatted = formatDate(medicine.getExpiryDate());
        holder.tvExpiry.setText("HSD: " + expiryFormatted);
        
        String lastUpdated = formatDate(medicine.getLastUpdated());
        if (lastUpdated != null && !lastUpdated.isEmpty()) {
            holder.tvLastUpdated.setVisibility(View.VISIBLE);
            holder.tvLastUpdated.setText("Cập nhật lần cuối: " + lastUpdated);
        } else {
            holder.tvLastUpdated.setVisibility(View.GONE);
        }

        holder.ivMore.setVisibility(showMenu ? View.VISIBLE : View.GONE);
        holder.ivMore.setOnClickListener(v -> showPopupMenu(v, medicine));
    }

    private void bindMedicineImage(@NonNull MedicineViewHolder holder, String imagePath) {
        holder.ivMedicineIcon.setTag(imagePath);

        if (imagePath == null || imagePath.trim().isEmpty()) {
            showDefaultMedicineIcon(holder);
            return;
        }

        File localFile = new File(imagePath);
        if (localFile.exists()) {
            holder.ivMedicineIcon.setAlpha(1f);
            holder.ivMedicineIcon.setPadding(0, 0, 0, 0);
            holder.ivMedicineIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.ivMedicineIcon.setImageURI(Uri.fromFile(localFile));
            return;
        }

        holder.ivMedicineIcon.setAlpha(1f);
        holder.ivMedicineIcon.setPadding(0, 0, 0, 0);
        holder.ivMedicineIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.ivMedicineIcon.setImageResource(R.drawable.ic_medical);

        String imageUrl = RetrofitClient.getAbsoluteUrl(imagePath);
        new Thread(() -> {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(new URL(imageUrl).openStream());
                holder.ivMedicineIcon.post(() -> {
                    if (imagePath.equals(holder.ivMedicineIcon.getTag())) {
                        holder.ivMedicineIcon.setImageBitmap(bitmap);
                    }
                });
            } catch (Exception ignored) {
                holder.ivMedicineIcon.post(() -> {
                    if (imagePath.equals(holder.ivMedicineIcon.getTag())) {
                        showDefaultMedicineIcon(holder);
                    }
                });
            }
        }).start();
    }

    private void showDefaultMedicineIcon(@NonNull MedicineViewHolder holder) {
        holder.ivMedicineIcon.setAlpha(0.2f);
        int padding = (int) (24 * holder.itemView.getResources().getDisplayMetrics().density);
        holder.ivMedicineIcon.setPadding(padding, padding, padding, padding);
        holder.ivMedicineIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        holder.ivMedicineIcon.setImageResource(R.drawable.ic_medical);
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty() || dateStr.equals("null")) return "";
        try {
            java.text.SimpleDateFormat inputFormat;
            if (dateStr.contains("T")) {
                inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            } else {
                inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            }
            java.util.Date date = inputFormat.parse(dateStr);
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr; // fallback if parsing fails
        }
    }

    private void showPopupMenu(View view, Medicine medicine) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.getMenuInflater().inflate(R.menu.menu_medicine_item, popup.getMenu());


        popup.setOnMenuItemClickListener(item -> {
            if (listener == null) return false;
            
            int id = item.getItemId();
            if (id == R.id.action_edit) {
                listener.onEdit(medicine);
                return true;
            } else if (id == R.id.action_delete) {
                listener.onDelete(medicine);
                return true;
            }
            return false;
        });
        popup.show();
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvExpiry, tvLastUpdated;
        ImageView ivMedicineIcon, ivMore;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMedicineIcon = itemView.findViewById(R.id.ivMedicineIcon);
            tvName = itemView.findViewById(R.id.tvMedicineName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            tvLastUpdated = itemView.findViewById(R.id.tvLastUpdated);
            ivMore = itemView.findViewById(R.id.ivMore);
        }
    }
}
