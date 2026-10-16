package dev.bsv.integration;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/** Small resource-independent smoke-test adapter. Full UI follows after startup validation. */
public final class BsvMenu {
    private static final String TAG = "bsv-in-process-root";
    public static void attach(Activity activity) {
        ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
        if (decor.findViewWithTag(TAG) != null || activity.isFinishing()) return;
        FrameLayout root = new FrameLayout(activity);
        root.setTag(TAG);
        root.setClipChildren(false);
        TextView button = new TextView(activity);
        button.setText("BSV"); button.setTextColor(Color.WHITE); button.setTextSize(16);
        button.setGravity(Gravity.CENTER);
        GradientDrawable bg = new GradientDrawable(); bg.setColor(0xff8b5cf6); bg.setCornerRadius(100);
        button.setBackground(bg); button.setElevation(12);
        int size = (int)(56 * activity.getResources().getDisplayMetrics().density);
        FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(size, size, Gravity.TOP | Gravity.RIGHT);
        p.setMargins(16, 48, 24, 16); root.addView(button, p);
        button.setOnClickListener(v -> new AlertDialog.Builder(activity)
            .setTitle("BSV — Brawl Stars Visuals")
            .setMessage("Меню встроено в процесс клиента. Проверка запуска 69.414.\n\nИгровые эффекты и данные аккаунта не изменяются.")
            .setPositiveButton("Закрыть", null).show());
        decor.addView(root, new ViewGroup.LayoutParams(-1, -1));
        Log.i("BSV", "Menu attached to game Activity");
    }
}
