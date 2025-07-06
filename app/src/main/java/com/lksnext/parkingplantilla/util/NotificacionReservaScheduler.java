package com.lksnext.parkingplantilla.util;

import android.content.Context;
import android.util.Log;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificacionReservaScheduler {
    public static void programarNotificaciones(Context context, String fecha, long horaInicio, String plaza) {
        try {
            Log.d("NotificacionReserva", "[DEBUG] Entrada: fecha=" + fecha + ", horaInicio(segundos)=" + horaInicio + ", plaza=" + plaza);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fechaReserva = sdf.parse(fecha);
            Log.d("NotificacionReserva", "[DEBUG] fechaReserva.getTime() (ms desde epoch)=" + fechaReserva.getTime());
            long millisReserva = fechaReserva.getTime() + horaInicio * 1000L;
            long millisActual = System.currentTimeMillis();
            Log.d("NotificacionReserva", "[DEBUG] millisReserva (ms)=" + millisReserva + " (" + new java.util.Date(millisReserva) + ")");
            Log.d("NotificacionReserva", "[DEBUG] millisActual (ms)=" + millisActual + " (" + new java.util.Date(millisActual) + ")");

            // Notificación 30 minutos antes
            long diff30 = millisReserva - millisActual - 30 * 60 * 1000;
            Log.d("NotificacionReserva", "[DEBUG] diff30 (ms)=" + diff30 + " (minutos: " + (diff30/60000) + ")");
            if (diff30 > 0) {
                Data data30 = new Data.Builder()
                        .putString(NotificacionReservaWorker.TITLE_KEY, "Reserva de parking")
                        .putString(NotificacionReservaWorker.MESSAGE_KEY, "Tu reserva en la plaza " + plaza + " empieza en 30 minutos.")
                        .build();
                OneTimeWorkRequest work30 = new OneTimeWorkRequest.Builder(NotificacionReservaWorker.class)
                        .setInitialDelay(diff30, TimeUnit.MILLISECONDS)
                        .setInputData(data30)
                        .build();
                WorkManager.getInstance(context).enqueue(work30);
                Log.d("NotificacionReserva", "Notificación programada para 30 minutos antes. Fecha: " + fecha + ", horaInicio: " + horaInicio + ", plaza: " + plaza);
            } else {
                Log.d("NotificacionReserva", "No se programa notificación 30 min: tiempo pasado. Fecha: " + fecha + ", horaInicio: " + horaInicio);
            }

            // Notificación 15 minutos antes
            long diff15 = millisReserva - millisActual - 15 * 60 * 1000;
            Log.d("NotificacionReserva", "[DEBUG] diff15 (ms)=" + diff15 + " (minutos: " + (diff15/60000) + ")");
            if (diff15 > 0) {
                Data data15 = new Data.Builder()
                        .putString(NotificacionReservaWorker.TITLE_KEY, "Reserva de parking")
                        .putString(NotificacionReservaWorker.MESSAGE_KEY, "Tu reserva en la plaza " + plaza + " empieza en 15 minutos.")
                        .build();
                OneTimeWorkRequest work15 = new OneTimeWorkRequest.Builder(NotificacionReservaWorker.class)
                        .setInitialDelay(diff15, TimeUnit.MILLISECONDS)
                        .setInputData(data15)
                        .build();
                WorkManager.getInstance(context).enqueue(work15);
                Log.d("NotificacionReserva", "Notificación programada para 15 minutos antes. Fecha: " + fecha + ", horaInicio: " + horaInicio + ", plaza: " + plaza);
            } else {
                Log.d("NotificacionReserva", "No se programa notificación 15 min: tiempo pasado. Fecha: " + fecha + ", horaInicio: " + horaInicio);
            }
        } catch (Exception e) {
            Log.e("NotificacionReserva", "Error al programar notificación: " + e.getMessage(), e);
        }
    }
}
