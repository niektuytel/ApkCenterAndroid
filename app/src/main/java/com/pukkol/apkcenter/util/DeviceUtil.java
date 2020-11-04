package com.pukkol.apkcenter.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeviceUtil {

    public static Point displaySize(Window context)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new Point(metrics.widthPixels, metrics.heightPixels);
    }

    @NonNull
    @Contract("_ -> new")
    public static Point navigationBarSize(Activity activity)
    {
        if(!hasNavigationBar(activity))
        {
            return new Point(0,0);
        }

        Point usableSize = appUsableScreenSize(activity);
        Point screenSize = realScreenSize(activity);

        // navigation bar on the side
        if (usableSize.x < screenSize.x)
        {
            int bar_width = screenSize.x - usableSize.x;
            int bar_height = usableSize.y;
            return new Point(bar_width, usableSize.y);
        }

        // navigation bar at the bottom
        if (usableSize.y < screenSize.y) {

            int bar_width = usableSize.x;
            int bar_height = screenSize.y - usableSize.y;
            return new Point(bar_width, bar_height);
        }

        // navigation bar is not present
        return new Point(0, 0);
    }

    public static Point appUsableScreenSize(Context context)
    {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point realScreenSize(Context context)
    {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        display.getRealSize(size);

        return size;
    }

    public static Boolean hasNavigationBar(Activity activity)
    {
        Rect rectangle = new Rect();
        DisplayMetrics displayMetrics = new DisplayMetrics();

        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);

        int top_height = rectangle.top + rectangle.height();
        return (boolean)(displayMetrics.heightPixels != top_height);
    }

    public static String getCountry()
    {
        return Locale.getDefault().getDisplayCountry();
    }

    public static void fullDisplay(Activity activity, int layout_id)
    {
        activity.setContentView(layout_id);
        activity.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    public static float dpFromPx(final Context context, final float px)
    {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp)
    {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static void hideKeyboard(@NonNull Activity activity)
    {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(@NonNull Activity activity)
    {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void addKeyboardVisibilityListener(View rootLayout, OnKeyboardVisibilityListener onKeyboardVisibilityListener)
    {
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootLayout.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootLayout.getRootView().getHeight();

            // r.bottom is the position above soft keypad or device button.
            // if keypad is shown, the r.bottom is smaller than that before.
            int keypadHeight = screenHeight - r.bottom;

            boolean isVisible = keypadHeight > screenHeight * 0.15; // 0.15 ratio is perhaps enough to determine keypad height.
            onKeyboardVisibilityListener.onVisibilityChange(isVisible);
        });
    }

    public interface OnKeyboardVisibilityListener
    {
        void onVisibilityChange(boolean isVisible);
    }

    public static List<String> allPackages(Context context)
    {
        ArrayList<PackageInfo> res = new ArrayList<PackageInfo>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packs = pm.getInstalledPackages(0);

        List<String> return_values = new ArrayList<>();

        for(int i=0;i<packs.size();i++) {
            PackageInfo pck = packs.get(i);

            if(!isSystemPackage(pck))
            {
                String package_name = pck.applicationInfo.packageName;
                return_values.add(package_name);
            }

        }

        return return_values;
    }

    private static boolean isSystemPackage(PackageInfo pkgInfo)
    {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

}
