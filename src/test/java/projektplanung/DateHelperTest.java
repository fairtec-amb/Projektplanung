package projektplanung;

import projektplanung.Persistence.Entities.Projekt;
import org.junit.jupiter.api.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DateHelperTest {
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void stringConverter()
    {
        String dateStr = "2000-01-01";
        Date testDate = DateHelper.stringToDate(dateStr);
        assertEquals(dateStr, format.format(testDate));
        assertEquals(dateStr, DateHelper.dateToString(DateHelper.stringToDate(dateStr)));
    }

    @Test
    public void testCheckIfOverlap()
    {
        Projekt mockP_1 = mock(Projekt.class);
        when(mockP_1.getStartDatum()).thenReturn("2020-01-01");
        when(mockP_1.getEndDatum()).thenReturn("2020-01-01");

        Projekt mockP_2 = mock(Projekt.class);
        when(mockP_2.getStartDatum()).thenReturn("2025-01-01");
        when(mockP_2.getEndDatum()).thenReturn("2025-01-01");

        Projekt mockP_3 = mock(Projekt.class);
        when(mockP_3.getStartDatum()).thenReturn("2019-01-01");
        when(mockP_3.getEndDatum()).thenReturn("2020-01-01");

        Projekt mockP_4 = mock(Projekt.class);
        when(mockP_4.getStartDatum()).thenReturn("2019-12-01");
        when(mockP_4.getEndDatum()).thenReturn("2025-01-01");

        assert(!DateHelper.checkIfOverlap(mockP_1, mockP_2));

        assert(DateHelper.checkIfOverlap(mockP_1, mockP_1));
        assert(DateHelper.checkIfOverlap(mockP_1, mockP_3));
        assert(DateHelper.checkIfOverlap(mockP_3, mockP_4));
        assert(DateHelper.checkIfOverlap(mockP_1, mockP_4));
        assert(DateHelper.checkIfOverlap(mockP_4, mockP_1));


    }

}