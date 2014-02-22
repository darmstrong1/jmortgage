package co.da.jmtg.pmt.extra;

import static org.junit.Assert.assertTrue;

import org.joda.time.Period;
import org.junit.Test;

import co.da.jmtg.amort.PmtKey;
import co.da.jmtg.amort.PmtKeys;
import co.da.jmtg.pmt.PmtPeriod;

public class DefaultExtraPmtTest {

    @Test
    public void testHashCode() {
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(PmtPeriod.MONTHLY, 15);
        double amount = 1000.00;
        ExtraPmt extraPmt1 = ExtraPmts.getDefaultExtraPmt(pmtKey, amount);
        ExtraPmt extraPmt2 = ExtraPmts.getDefaultExtraPmt(pmtKey, amount);
        ExtraPmt extraPmt3 = ExtraPmts.getDefaultExtraPmt(pmtKey, 1500.00);

        assertTrue(extraPmt1.hashCode() == extraPmt2.hashCode());
        assertTrue(extraPmt2.hashCode() != extraPmt3.hashCode());
    }

    @Test
    public void testGetPmtKey() {
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(PmtPeriod.MONTHLY, 15);
        double amount = 1000.00;

        ExtraPmt extraPmt = ExtraPmts.getDefaultExtraPmt(pmtKey, amount);
        PmtKey returnedPmtKey = extraPmt.getPmtKey();

        assertTrue(pmtKey == returnedPmtKey);
    }

    @Test
    public void testGetAmount() {
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(PmtPeriod.MONTHLY, 15);
        double amount = 1000.00;

        ExtraPmt extraPmt = ExtraPmts.getDefaultExtraPmt(pmtKey, amount);
        double returnedAmount = extraPmt.getAmount();

        assertTrue(amount == returnedAmount);
    }

    @Test
    public void testSetPmtKey() {
        PmtKey pmtKey1 = PmtKeys.getDefaultPmtKeyForYears(PmtPeriod.MONTHLY, 15);
        double amount = 1000.00;

        ExtraPmt extraPmt1 = ExtraPmts.getDefaultExtraPmt(pmtKey1, amount);
        PmtKey pmtKey2 = pmtKey1.setFirstKey(pmtKey1.getFirstKey().plus(Period.months(6)));
        ExtraPmt extraPmt2 = extraPmt1.setPmtKey(pmtKey2);

        PmtKey returnedPmtKey1 = extraPmt1.getPmtKey();
        PmtKey returnedPmtKey2 = extraPmt2.getPmtKey();

        assertTrue(pmtKey1 == returnedPmtKey1);
        assertTrue(pmtKey2 == returnedPmtKey2);
    }

    @Test
    public void testSetAmount() {
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(PmtPeriod.MONTHLY, 15);
        double amount1 = 1000.00;

        ExtraPmt extraPmt1 = ExtraPmts.getDefaultExtraPmt(pmtKey, amount1);
        double amount2 = 800.00;
        ExtraPmt extraPmt2 = extraPmt1.setAmount(amount2);

        double returnedAmount1 = extraPmt1.getAmount();
        double returnedAmount2 = extraPmt2.getAmount();

        assertTrue(amount1 == returnedAmount1);
        assertTrue(amount2 == returnedAmount2);
    }

    @Test
    public void testEqualsObject() {
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(PmtPeriod.MONTHLY, 15);
        double amount = 1000.00;
        ExtraPmt extraPmt1 = ExtraPmts.getDefaultExtraPmt(pmtKey, amount);
        ExtraPmt extraPmt2 = ExtraPmts.getDefaultExtraPmt(pmtKey, amount);
        ExtraPmt extraPmt3 = ExtraPmts.getDefaultExtraPmt(pmtKey, 1500.00);

        // Test to make sure that equal objects are also the same object. That should be true for DefaultExtraPmt
        // objects.
        assertTrue(extraPmt1 == extraPmt2);
        assertTrue(extraPmt1.equals(extraPmt2));
        assertTrue(extraPmt2 == extraPmt1);
        assertTrue(extraPmt2.equals(extraPmt1));

        assertTrue(extraPmt1 != extraPmt3);
        assertTrue(extraPmt1.equals(extraPmt3) == false);
        assertTrue(extraPmt3 != extraPmt1);
        assertTrue(extraPmt3.equals(extraPmt1) == false);
        assertTrue(extraPmt2 != extraPmt3);
        assertTrue(extraPmt2.equals(extraPmt3) == false);
        assertTrue(extraPmt3 != extraPmt2);
        assertTrue(extraPmt3.equals(extraPmt2) == false);
    }

    @Test
    public void testCompareTo() {
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(PmtPeriod.MONTHLY, 15);
        double amount = 1000.00;
        ExtraPmt extraPmt1 = ExtraPmts.getDefaultExtraPmt(pmtKey, amount);
        ExtraPmt extraPmt2 = ExtraPmts.getDefaultExtraPmt(pmtKey, amount);
        ExtraPmt extraPmt3 = ExtraPmts.getDefaultExtraPmt(pmtKey, 1500.00);

        assertTrue(extraPmt1 == extraPmt2);
        assertTrue(extraPmt1.equals(extraPmt2));
        assertTrue(extraPmt1.compareTo(extraPmt2) == 0);
        assertTrue(extraPmt2 == extraPmt1);
        assertTrue(extraPmt2.equals(extraPmt1));
        assertTrue(extraPmt2.compareTo(extraPmt1) == 0);

        assertTrue(extraPmt1 != extraPmt3);
        assertTrue(extraPmt1.equals(extraPmt3) == false);
        assertTrue(extraPmt1.compareTo(extraPmt3) < 0);
        assertTrue(extraPmt3 != extraPmt1);
        assertTrue(extraPmt3.equals(extraPmt1) == false);
        assertTrue(extraPmt3.compareTo(extraPmt1) > 0);
        assertTrue(extraPmt2 != extraPmt3);
        assertTrue(extraPmt2.equals(extraPmt3) == false);
        assertTrue(extraPmt2.compareTo(extraPmt3) < 0);
        assertTrue(extraPmt3 != extraPmt2);
        assertTrue(extraPmt3.equals(extraPmt2) == false);
        assertTrue(extraPmt3.compareTo(extraPmt2) > 0);
    }

}
