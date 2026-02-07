package com.lighko.updates.Adapters;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.core.content.ContextCompat;

import com.lighko.updates.R;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class SimplePopup extends PopupWindow {
    public SimplePopup(Context context, List<SimplePopupValue> popupList, PopupMenuItemListener menuItemListener) {
        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.layout_simple_popup, null);
        setContentView(layout);
        setWidth(getDp(context, 220.0f));

        LinearLayout llPopup = layout.findViewById(R.id.ll_popup);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
        );

        for (int i = 0; i < popupList.size(); i++) {
            View itemView = inflater.inflate(R.layout.item_simple_popup, null, false);
            MaterialButton btn = itemView.findViewById(R.id.mybtn);
            btn.setText(popupList.get(i).getTitle());
            Drawable icon = ContextCompat.getDrawable(context, popupList.get(i).getResId());
            btn.setIcon(icon);
            int finalI = i;
            btn.setOnClickListener(view -> {
                menuItemListener.onMenuItemClick(view, popupList.get(finalI), finalI);
                dismiss();
            });
            llPopup.addView(itemView, layoutParams);
        }

        layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(layout.getMeasuredHeight());

        setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_menu));
    }

    private int getDp(Context context, float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    public interface PopupMenuItemListener {
        void onMenuItemClick(View view, SimplePopupValue value, int position);
    }
}

