package utilities;

import android.os.Build;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

public class DateConvert {

    private String F_PAST; // 26 de enero del 2009
    private String F_AÑO; // 6 ene
    private String F_SEMANA; // 3 d
    private String F_HORA; // 4 h
    private String F_DIA; // 15 min

    private int YN;
    private int MN;
    private int DN;
    private int HN;
    private int mN;

    private int YC;
    private int MC;
    private int DC;
    private int HC;
    private int mC;

    String createdAt;

    public DateConvert(){

    }

    public DateConvert(String createdAt) {
        this.createdAt = createdAt;
        YC = Integer.parseInt(createdAt.substring(0, 4));
        MC = Integer.parseInt(createdAt.substring(5, 7));
        DC = Integer.parseInt(createdAt.substring(8, 10));
        HC = Integer.parseInt(createdAt.substring(11, 13));
        mC = Integer.parseInt(createdAt.substring(14, 16));
        evalFech();
    }

    public void evalFech() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long created = Instant.parse(createdAt).toEpochMilli();
            Date date = new Date(created);
            LocalDateTime localDateTime = convertToLocalDateTimeViaMilisecond(date);
            YN = localDateTime.getYear();
            MN = localDateTime.getMonthValue();
            DN = localDateTime.getDayOfMonth();
            HN = localDateTime.getHour();
            mN = localDateTime.getMinute();
        }
    }

    public LocalDateTime getLocalDateTime(String param) {
        LocalDateTime localDateTime = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long created = Instant.parse(param).toEpochMilli();
            Date date = new Date(created);
            localDateTime = convertToLocalDateTimeViaMilisecond(date);
        }
        return localDateTime;
    }

    public String getAgo() {
        String month = "mes";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime now = LocalDateTime.now();
            YN = now.getYear();
            MN = now.getMonthValue();
            DN = now.getDayOfMonth();
            HN = now.getHour();
            mN = now.getMinute();
        }

        System.out.println(YN + " - " + MN + " - " + DN + "  " + HN + " : " + mN);

        switch (MC) {
            case 1:
                month = "enero";
                break;
            case 2:
                month = "febrero";
                break;
            case 3:
                month = "marzo";
                break;
            case 4:
                month = "abril";
                break;
            case 5:
                month = "mayo";
                break;
            case 6:
                month = "junio";
                break;
            case 7:
                month = "julio";
                break;
            case 8:
                month = "agosto";
                break;
            case 9:
                month = "septiembre";
                break;
            case 10:
                month = "octubre";
                break;
            case 11:
                month = "noviembre";
                break;
            case 12:
                month = "diciembre";
                break;
        }

        // FORMATOS
        F_PAST = DC + " de " + month + " del " + YC; // 26 de enero del 2009
        F_AÑO = DC + " " + month; // 6 ene
        //F_SEMANA = ; // 3 d
        //F_HORA = ; // 15 min
        //F_DIA = ; // 8 h

        //System.out.printf("%nEl valor de la variable cantidad es %02.0f", cantidad);

        if (YN > YC) {
            return F_PAST;
        } else {
            if (MN == MC) {
                if (DN > DC) {
                    int D = DN - DC;
                    if (D > 7) {
                        return F_AÑO;
                    } else {
                        F_SEMANA = "Hace " + D + " d";
                        return F_SEMANA;
                    }
                } else {
                    int H = HN - HC;
                    if (HN > HC) {
                        if (H < 0) {
                            H = H * -1;
                            H = 24 - H;
                        }

                        F_DIA = "Hace " + H + " h";
                        return F_DIA;
                    } else {
                        int m = mN - mC;

                        if (m < 0) {
                            m = m * -1;
                            m = 60 - m;
                        }

                        F_HORA = "Hace " + m + " min";
                        return F_HORA;
                    }
                }
            } else {
                return F_AÑO;
            }
        }
    }

    public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return dateToConvert.toInstant()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();
        } else {
            return null;
        }
    }

    public LocalDateTime convertToLocalDateTimeViaMilisecond(Date dateToConvert) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int sec = 25200;
            return Instant.ofEpochMilli(dateToConvert.getTime() - (sec * 1000L))
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        } else {
            return null;
        }
    }

    public String getSimpleFech(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date(time);
        String base = simpleDateFormat.format(date);
        String[] arrOfStr = base.split(" ", 4);
        // año -> arrOfStr[2]
        return arrOfStr[0] + " de " + arrOfStr[1] + " a las " + arrOfStr[3];
    }

    public String getSimpleAgo(long time, long now) {

        long res = now - time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date(res);
        String base = simpleDateFormat.format(date);

        String[] arrOfStr = base.split(":", 2);

        if (arrOfStr[0].equals("00")) {
            return "Hace " + arrOfStr[1] + " nimutos.";
        } else {
            int hour = Integer.parseInt(arrOfStr[1]);
            if (hour > 1) {
                return "Hace " + arrOfStr[0] + " horas.";
            } else {
                return "Hace " + arrOfStr[0] + " hora.";
            }
        }
    }


}
