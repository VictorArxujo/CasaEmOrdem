package br.com.casaemordem.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Operações de data usadas pelas regras de recorrência, sempre em granularidade de dia. */
public final class Datas {
    public static final Locale BR = new Locale("pt", "BR");
    private static final long UM_DIA = 24L * 60L * 60L * 1000L;

    private Datas() {
    }

    /** Zera horas, minutos e segundos para que a comparação entre datas seja por dia. */
    public static long inicioDoDia(long millis) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(millis);
        calendario.set(Calendar.HOUR_OF_DAY, 0);
        calendario.set(Calendar.MINUTE, 0);
        calendario.set(Calendar.SECOND, 0);
        calendario.set(Calendar.MILLISECOND, 0);
        return calendario.getTimeInMillis();
    }

    public static long hoje() {
        return inicioDoDia(System.currentTimeMillis());
    }

    /** Quantidade de dias inteiros entre duas datas. O arredondamento absorve o horário de verão. */
    public static int diasEntre(long inicio, long fim) {
        double diferenca = (double) (inicioDoDia(fim) - inicioDoDia(inicio)) / UM_DIA;
        return (int) Math.round(diferenca);
    }

    public static String formatarCurto(long millis) {
        return new SimpleDateFormat("dd/MM/yyyy", BR).format(new Date(millis));
    }

    public static String formatarDiaPorExtenso(long millis) {
        String texto = new SimpleDateFormat("EEEE, d 'de' MMMM", BR).format(new Date(millis));
        return texto.substring(0, 1).toUpperCase(BR) + texto.substring(1);
    }
}
