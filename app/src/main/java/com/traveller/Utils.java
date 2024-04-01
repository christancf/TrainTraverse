package com.traveller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static int daysDifference(Date from) throws ParseException {
        Date now = new Date();
        long difference = from.getTime() - now.getTime();
        return (int) (difference / (1000*60*60*24));
    }
}
