package ru.cardiacare.cardiacare;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/* Виджет "ТРЕВОГА" */

public class SosWidget extends AppWidgetProvider {

    final static String ACTION_WIDGET = "ru.cardiacare.cardiacare.open_from_widget";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // Обновление всех экземпляров виджета
        for (int i : appWidgetIds) {
            updateWidget(context, appWidgetManager, i);
        }
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    static void updateWidget(Context ctx, AppWidgetManager appWidgetManager,
                             int widgetID) {
        RemoteViews widgetView = new RemoteViews(ctx.getPackageName(),
                R.layout.widget_sos);

        // Обработка нажатия на виджет
        Intent widgetIntent = new Intent(ctx, MainActivity.class);
        widgetIntent.setAction(ACTION_WIDGET);
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        PendingIntent pIntent = PendingIntent.getActivity(ctx, widgetID,
                widgetIntent, 0);
        widgetView.setOnClickPendingIntent(R.id.SOSButton, pIntent);

        // Обновление виджета
        appWidgetManager.updateAppWidget(widgetID, widgetView);
    }

    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}