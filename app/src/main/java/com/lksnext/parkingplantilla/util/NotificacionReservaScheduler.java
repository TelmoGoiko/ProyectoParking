package com.lksnext.parkingplantilla.util;

import android.content.Context;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificacionReservaScheduler {
    public static void programarNotificaciones(Context context, String fecha, long horaInicio, String plaza) {
        // fecha: "dd/MM/yyyy", horaInicio: segundos desde 00:00
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date fechaReserva = sdf.parse(fecha);
            long millisReserva = fechaReserva.getTime() + horaInicio * 1000L;
            long millisActual = System.currentTimeMillis();

            // Notificación 30 minutos antes
            long diff30 = millisReserva - millisActual - 30 * 60 * 1000;
            if (diff30 > 0) {
                Data data = new Data.Builder()
                        .putString(NotificacionReservaWorker.TITLE_KEY, "Reserva de parking")
                        .putString(NotificacionReservaWorker.MESSAGE_KEY, "Tu reserva en la plaza " + plaza + " empieza en 30 minutos.")
                        .build();
                OneTimeWorkRequest work30 = new OneTimeWorkRequest.Builder(NotificacionReservaWorker.class)
                        .setInitialDelay(diff30, TimeUnit.MILLISECONDS)
                        .setInputData(data)
                        .build();
                WorkManager.getInstance(context).enqueue(work30);
            }

            // Notificación 15 minutos antes
            long diff15 = millisReserva - millisActual - 15 * 60 * 1000;
            if (diff15 > 0) {
                Data data = new Data.Builder()
                        .putString(NotificacionReservaWorker.TITLE_KEY, "Reserva de parking")
                        .putString(NotificacionReservaWorker.MESSAGE_KEY, "Tu reserva en la plaza " + plaza + " empieza en 15 minutos.")
                        .build();
                OneTimeWorkRequest work15 = new OneTimeWorkRequest.Builder(NotificacionReservaWorker.class)
                        .setInitialDelay(diff15, TimeUnit.MILLISECONDS)
                        .setInputData(data)
                        .build();
                WorkManager.getInstance(context).enqueue(work15);
            }
        } catch (Exception e) {
            // Error de parseo, no se programa notificación
        }
    }
}
