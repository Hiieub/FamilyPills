package com.example.familypills.ui.add_medicine;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.familypills.R;

public class AddMedicineActivity extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        viewPager = findViewById(R.id.viewPager);
        // Disable swiping if you want to control navigation only via buttons
        viewPager.setUserInputEnabled(false);

        AddMedicinePagerAdapter adapter = new AddMedicinePagerAdapter(this);
        viewPager.setAdapter(adapter);
    }

    public void setCurrentTab(int position) {
        if (viewPager != null) {
            viewPager.setCurrentItem(position, false);
        }
    }
}