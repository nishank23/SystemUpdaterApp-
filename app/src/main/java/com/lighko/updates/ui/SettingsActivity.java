package com.lighko.updates.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lighko.updates.Adapters.SettingsAdapter;
import com.lighko.updates.Models.SettingItem;
import com.lighko.updates.R;
import com.lighko.updates.Utils.Extensions;
import com.lighko.updates.Utils.SharedPreferencesHelper;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferencesHelper sharedPreferences;

    private SwitchCompat autoDownloadSwitch;
    List<SettingItem> settingItems;
    SettingsAdapter adapter;
    RecyclerView recyclerView;
    MaterialButton btnBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        recyclerView = findViewById(R.id.rv_settings);

        btnBack = findViewById(R.id.btn_back);
        settingItems = new ArrayList<>();

        /* autoDownloadSwitch = findViewById(R.id.switchWidget);
         */

        sharedPreferences = new SharedPreferencesHelper(this);


        settingItems.add(new SettingItem("Automatically download", "Automatically download updates when connected to WIFI", sharedPreferences.isAutoDownloadEnabled(), ContextCompat.getDrawable(this,R.drawable.ic_download)));
        settingItems.add(new SettingItem("Automatically install", "Automatically install updates overnight", sharedPreferences.isAutoUpdateEnabled(),ContextCompat.getDrawable(this,R.drawable.ic_update)));


        adapter = new SettingsAdapter(this,settingItems, new SettingsAdapter.SettingItemListener() {
            @Override
            public void onItemSwitchChanged(SettingItem item, boolean isChecked,int position) {
                // Handle switch change, e.g., update preferences or settings
                settingItems.get(position).setChecked(isChecked);
                updateSharedPreferences(item,isChecked);
                adapter.notifyItemChanged(position,item);

                Log.d("Settings", "Switch for " + item.getTitle() + " is now " + (isChecked ? "on" : "off"));
            }

            @Override
            public void onItemFocused(SettingsAdapter.ViewHolder holder) {




            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        recyclerView.setFocusedByDefault(true);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
 /*
        autoDownloadSwitch.setChecked(sharedPreferences.isAutoDownloadEnabled());

        autoDownloadSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               sharedPreferences.setAutoDownloadEnabled(isChecked);
            }
        });*/
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            View focusedView = recyclerView.getFocusedChild();
            if (focusedView != null) {
                int position = recyclerView.getChildAdapterPosition(focusedView);
                if (position != RecyclerView.NO_POSITION) {
                    runOnUiThread(() -> {
                        if (recyclerView.isComputingLayout()) {
                           // recyclerView.post(this::runOnUiThread);
                        } else {
                            SettingsAdapter.ViewHolder holder = (SettingsAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
                            if (holder != null) {
                                holder.switchCompat.toggle();
                                SettingItem item = settingItems.get(holder.getAdapterPosition());
                                boolean newState = ((SettingsAdapter.ViewHolder) holder).switchCompat.isChecked();
                                item.setChecked(newState); // Update the item state
                                adapter.notifyItemChanged(holder.getAdapterPosition(),item); // Notify the adapter of the change
                                updateSharedPreferences(item, newState);
                            }
                        }
                    });
                    return true; // Return true as the event was handled
                }
            }
        }
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
            onBackPressed();
            return true;
        }
        return super.dispatchKeyEvent(event);

    }
    private void updateSharedPreferences(SettingItem item, boolean isChecked) {
        if (item.getTitle().contains("download")) {
            sharedPreferences.setAutoDownloadEnabled(isChecked);
        }
        if (item.getTitle().contains("install")) {
            sharedPreferences.setAutoUpdateEnabled(isChecked);
        }
    }
}