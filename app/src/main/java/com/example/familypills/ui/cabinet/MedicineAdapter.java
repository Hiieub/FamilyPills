package com.example.familypills.ui.cabinet;

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
        
        String quantity = medicine.getQuantity();
        if (quantity != null && !quantity.isEmpty()) {
            holder.tvQuantity.setVisibility(View.VISIBLE);
            holder.tvQuantity.setText(quantity.startsWith("Còn") ? quantity : "Còn " + quantity);
        } else {
            holder.tvQuantity.setVisibility(View.GONE);
        }

        holder.tvExpiry.setText("HSD: " + medicine.getExpiryDate());
        
        String lastUpdated = medicine.getLastUpdated();
        if (lastUpdated != null && !lastUpdated.equals("null")) {
            holder.tvLastUpdated.setVisibility(View.VISIBLE);
            holder.tvLastUpdated.setText("Cập nhật lần cuối: " + lastUpdated);
        } else {
            holder.tvLastUpdated.setVisibility(View.GONE);
        }

        if (medicine.isRunningLow()) {
            holder.tvStatusBadge.setVisibility(View.VISIBLE);
            holder.tvStatusBadge.setText("Sắp hết");
        } else if (medicine.isExpired()) {
            holder.tvStatusBadge.setVisibility(View.VISIBLE);
            holder.tvStatusBadge.setText("Hết hạn");
        } else {
            holder.tvStatusBadge.setVisibility(View.GONE);
        }

        holder.ivMore.setVisibility(showMenu ? View.VISIBLE : View.GONE);
        holder.ivMore.setOnClickListener(v -> showPopupMenu(v, medicine));
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
        TextView tvName, tvQuantity, tvExpiry, tvLastUpdated, tvStatusBadge;
        ImageView ivMore;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMedicineName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
            tvLastUpdated = itemView.findViewById(R.id.tvLastUpdated);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            ivMore = itemView.findViewById(R.id.ivMore);
        }
    }
}
