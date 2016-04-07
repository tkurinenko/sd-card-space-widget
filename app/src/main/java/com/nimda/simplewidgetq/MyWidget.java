package com.nimda.simplewidgetq;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.NumberFormat;
import java.util.Arrays;

public class MyWidget extends AppWidgetProvider {

    private double bytesFree;
    private double gigFree;
    private double bytesAvailable;
    private double gigAvailable;
    private double bytesTotal;
    private double gigTotal;
    private StatFs statFs;
    private String outputInfo;
    private String outputInfoUsed;
    private String outputInfoFree;
    private String externalStorageState;
    private NumberFormat numberFormat;

    final String LOG_TAG = "myLogs";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(LOG_TAG, "onEnabled");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        ComponentName thisWidget = new ComponentName(context,
                MyWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.widget_free_only);

            externalStorageState = Environment.getExternalStorageState();

            if (externalStorageState.equals(Environment.MEDIA_MOUNTED) || externalStorageState.equals(Environment.MEDIA_UNMOUNTED)
                    || externalStorageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {

                //Internal SD Card
                statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
                bytesTotal = (long) statFs.getBlockSize() * (long) statFs.getBlockCount();
                gigTotal = bytesTotal / 1073741824;
                numberFormat = NumberFormat.getInstance();
                numberFormat.setMaximumFractionDigits(2);
                numberFormat.setGroupingUsed(false);

                outputInfo = numberFormat.format(gigTotal) + " GB";
                bytesAvailable = (long) statFs.getBlockSize() * (long) statFs.getAvailableBlocks();
                gigAvailable = bytesAvailable / 1073741824;
                outputInfoUsed = numberFormat.format(gigAvailable) + " GB";

                //free
                bytesFree = bytesTotal - bytesAvailable;
                gigFree = bytesFree / 1073741824;

                outputInfoFree = numberFormat.format(gigFree) + " GB";
                remoteViews.setTextViewText(R.id.tvGbFree, String.valueOf(outputInfoUsed));
                Intent intent = new Intent(context, MyWidget.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.tvInternalSD, pendingIntent);
                appWidgetManager.updateAppWidget(widgetId, remoteViews);
            }

            super.onUpdate(context, appWidgetManager, appWidgetIds);
            Log.d(LOG_TAG, "onUpdate " + Arrays.toString(appWidgetIds));
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(LOG_TAG, "onDeleted " + Arrays.toString(appWidgetIds));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(LOG_TAG, "onDisabled");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}