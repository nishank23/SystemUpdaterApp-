package com.lighko.updates.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.lighko.updates.Models.SettingItem;
import com.lighko.updates.R;


import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {

    private List<SettingItem> items;
    private SettingItemListener listener;
    private Context context;

    public interface SettingItemListener {
        void onItemSwitchChanged(SettingItem item, boolean isChecked,int position);
        void onItemFocused(SettingsAdapter.ViewHolder holder);
    }

    public SettingsAdapter(Context context,List<SettingItem> items, SettingItemListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_settings_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SettingItem item = items.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.switchCompat.setChecked(item.isChecked());
        holder.ivIcon.setImageDrawable(item.getIcon());
        holder.switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setChecked(isChecked);
            listener.onItemSwitchChanged(item, isChecked,position);
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.itemView.requestFocus();
                holder.switchCompat.toggle(); // Toggle the SwitchCompat when the item is clicked
                 // Request focus for the item view
                listener.onItemFocused(holder);
            }
        });
        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                holder.mCardView.setCardBackgroundColor(context.getResources().getColor(R.color.recyclerItemColor));
                // Delegate the action to the activity
                listener.onItemFocused(holder);
            } else {
                holder.mCardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        ImageView ivIcon;
        public SwitchCompat switchCompat;
        CardView mCardView;


        public ViewHolder(@NonNull View view) {
            super(view);
            mCardView = itemView.findViewById(R.id.m_card_view);
            ivIcon = itemView.findViewById(R.id.ic_update_icon);
            tvTitle = itemView.findViewById(R.id.tv_settings_title);
            tvDescription = itemView.findViewById(R.id.tv_settings_desc);
            switchCompat = itemView.findViewById(R.id.switchWidget);
        }
        public void toggleSwitch() {
            switchCompat.setChecked(true);
        }
    }
}
