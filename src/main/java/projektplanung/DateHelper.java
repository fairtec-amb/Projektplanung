package projektplanung;

import projektplanung.Persistence.Entities.Projekt;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static Date stringToDate(String date_as_string) {
        Date date = null;
        try {
            date = format.parse(date_as_string);
        } catch (ParseException e) {
            // e.printStackTrace();
        }

        return date;
    }

    public static String dateToString(Date date) {
        String date_as_string = "";
        date_as_string = format.format(date);

        return date_as_string;
    }

    public static boolean checkIfOverlap(Projekt first_p, Projekt second_p) {
        Date start_of_first_p = stringToDate(first_p.getStartDatum());
        Date end_of_first_p = stringToDate(first_p.getEndDatum());

        Date start_of_second_p = stringToDate(second_p.getStartDatum());
        Date end_of_second_p = stringToDate(second_p.getEndDatum());

        /* We check one of the following cases is true:

        case 1: The start of the second project takes place during the first project

        case 2: The end of the second project takes place during the first project

        case 3: The first project takes place within the second project period

         */
        if ((start_of_second_p.compareTo(start_of_first_p) >= 0 &&
                start_of_second_p.compareTo(end_of_first_p) <= 0) ||
                (end_of_second_p.compareTo(start_of_first_p) >= 0 &&
                        end_of_second_p.compareTo(end_of_first_p) <= 0) ||
                (start_of_second_p.compareTo(start_of_first_p) <= 0 &&
                        end_of_second_p.compareTo(end_of_first_p) >= 0)) {
            return true;
        } else {
            return false;
        }
    }
}
