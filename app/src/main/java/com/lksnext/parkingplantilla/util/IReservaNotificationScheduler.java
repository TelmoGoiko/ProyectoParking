package com.lksnext.parkingplantilla.util;

public interface IReservaNotificationScheduler {
    void programarNotificaciones(String fecha, long horaInicio, String plaza);
}
