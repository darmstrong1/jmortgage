package co.da.jmtg.amort;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.joda.time.LocalDate;
import org.junit.Test;

import co.da.jmtg.pmt.PmtPeriod;

public class DefaultPmtKeyTest {

    @Test
    public void testHashCode() {
        PmtKey pmtKey1 = PmtKeys.getDefaultPmtKey(PmtPeriod.MONTHLY, new LocalDate("2014-02-01"), 360);
        PmtKey pmtKey2 = PmtKeys.getDefaultPmtKey(PmtPeriod.BIWEEKLY, new LocalDate("2014-02-01"), 360);
        PmtKey pmtKey3 = PmtKeys.getDefaultPmtKey(PmtPeriod.MONTHLY, new LocalDate("2014-02-01"), 360);

        assertTrue(pmtKey1.hashCode() == pmtKey3.hashCode());
        assertFalse(pmtKey1.hashCode() == pmtKey2.hashCode());
    }

    @Test
    public void testGetInstancePmtPeriod() {
        PmtKey pmtKey = PmtKeys.getDefaultPmtKey(PmtPeriod.ONETIME);
        // A DefaultPmtKey instantiated with just a PmtPeriod defaults its count to 1 and its key to now.
        assertTrue(pmtKey.getCount() == 1);
        assertTrue(pmtKey.getFirstKey().equals(LocalDate.now()));
    }

    @Test
    public void testGetInstancePmtPeriodInt() {
        PmtKey pmtKey = PmtKeys.getDefaultPmtKey(PmtPeriod.RAPID_WEEKLY, 360);
        assertTrue(pmtKey.getFirstKey().equals(LocalDate.now()));
    }

    @Test
    public void testGetInstancePmtPeriodLocalDate() {
        PmtKey pmtKey = PmtKeys.getDefaultPmtKey(PmtPeriod.RAPID_WEEKLY, new LocalDate("2014-02-01"));
        // The count should be one.
        assertTrue(pmtKey.getCount() == 1);
    }

    @Test
    public void testGetInstancePmtPeriodLocalDateInt() {
        int count = 1040;
        PmtKey pmtKey = PmtKeys.getDefaultPmtKey(PmtPeriod.RAPID_WEEKLY, LocalDate.now(), count);
        assertTrue(pmtKey.getPmtPeriod() == PmtPeriod.RAPID_WEEKLY);
        assertTrue(pmtKey.getCount() == count);
        assertTrue(pmtKey.getFirstKey().equals(LocalDate.now()));
    }

    @Test
    public void testSetPmtPeriod() {
        PmtKey pmtKey1 = PmtKeys.getDefaultPmtKey(PmtPeriod.RAPID_WEEKLY, LocalDate.now(), 1040);
        PmtKey pmtKey2 = pmtKey1.setPmtPeriod(PmtPeriod.RAPID_BIWEEKLY);

        assertTrue(pmtKey2.getPmtPeriod() == PmtPeriod.RAPID_BIWEEKLY);
    }

    @Test
    public void testSetKey() {
        PmtKey pmtKey1 = PmtKeys.getDefaultPmtKey(PmtPeriod.RAPID_WEEKLY, LocalDate.now(), 1040);
        LocalDate key = new LocalDate("2014-02-01");
        PmtKey pmtKey2 = pmtKey1.setFirstKey(key);

        assertTrue(pmtKey2.getFirstKey().equals(key));
    }

    @Test
    public void testSetCount() {
        PmtKey pmtKey1 = PmtKeys.getDefaultPmtKey(PmtPeriod.RAPID_WEEKLY, LocalDate.now(), 1040);
        PmtKey pmtKey2 = pmtKey1.setCount(30);

        assertTrue(pmtKey2.getCount() == 30);
    }

    @Test
    public void testEqualsObject() {
        PmtKey pmtKey1 = PmtKeys.getDefaultPmtKey(PmtPeriod.MONTHLY, new LocalDate("2014-02-01"), 360);
        PmtKey pmtKey2 = PmtKeys.getDefaultPmtKey(PmtPeriod.BIWEEKLY, new LocalDate("2014-02-01"), 780);
        PmtKey pmtKey3 = PmtKeys.getDefaultPmtKey(PmtPeriod.MONTHLY, new LocalDate("2014-02-01"), 360);

        // Objects that are equal in value should be the same object.
        assertTrue(pmtKey1 == pmtKey3);
        assertTrue(pmtKey1.equals(pmtKey3));
        assertTrue(pmtKey3 == pmtKey1);
        assertTrue(pmtKey3.equals(pmtKey1));

        assertFalse(pmtKey1 == pmtKey2);
        assertFalse(pmtKey1.equals(pmtKey2));
    }

    @Test
    public void testCompareTo() {
        PmtKey pmtKey1 = PmtKeys.getDefaultPmtKey(PmtPeriod.MONTHLY, new LocalDate("2014-02-01"), 360);
        PmtKey pmtKey2 = PmtKeys.getDefaultPmtKey(PmtPeriod.BIWEEKLY, new LocalDate("2014-02-01"), 780);
        PmtKey pmtKey3 = PmtKeys.getDefaultPmtKey(PmtPeriod.MONTHLY, new LocalDate("2014-02-01"), 360);

        // Objects that are equal in value should be the same object.
        assertTrue(pmtKey1 == pmtKey3);
        assertTrue(pmtKey1.equals(pmtKey3));
        // compareTo must be consistent with equals.
        assertTrue(pmtKey1.compareTo(pmtKey3) == 0);
        assertTrue(pmtKey3 == pmtKey1);
        assertTrue(pmtKey3.equals(pmtKey1));
        assertTrue(pmtKey3.compareTo(pmtKey1) == 0);

        assertFalse(pmtKey1 == pmtKey2);
        assertFalse(pmtKey1.equals(pmtKey2));
        assertTrue(pmtKey1.compareTo(pmtKey2) > 0);
        assertTrue(pmtKey2.compareTo(pmtKey1) < 0);
    }

    @Test
    public void testIterable() {
        int pmtCount = 360;
        PmtKey pmtKey = PmtKeys.getDefaultPmtKey(PmtPeriod.MONTHLY, LocalDate.now(), pmtCount);

        Iterator<LocalDate> iterator = pmtKey.getKeys().iterator();
        int count = 0;
        LocalDate prevDate;
        if (iterator.hasNext()) {
            count++;
            prevDate = iterator.next();
            while (iterator.hasNext()) {
                count++;
                LocalDate date = iterator.next();
                assertTrue(prevDate.plus(pmtKey.getPmtPeriod().period()).equals(date));
                prevDate = date;
            }
        }
        assertTrue(count == pmtCount);
    }

    @Test
    public void testGetInstanceByYears() {
        int years = 30;
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(PmtPeriod.MONTHLY, years);

        assertTrue(pmtKey.getCount() == years * 12);
        int count = 0;
        for (@SuppressWarnings("unused")
        LocalDate date : pmtKey.getKeys()) {
            count++;
        }
        assertTrue(count == years * 12);
    }

}
