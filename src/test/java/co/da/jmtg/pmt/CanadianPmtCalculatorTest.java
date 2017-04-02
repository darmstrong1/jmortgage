package co.da.jmtg.pmt;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CanadianPmtCalculatorTest {

    @Test
    public void testGetPmt() {
        // source for rates: http://www.tdcanadatrust.com/docs/mortCalc/MortgageCalculator.jsp

        // The monthly payment for this loan should be $1013.37.
        double expected = 1008.43;
        PmtCalculator pmtCalc = PmtCalculators.getCanadianPmtCalculator(PmtPeriod.MONTHLY, 200000, 4.5, 360);
        double pmt = pmtCalc.getPmt();
        assertThat(pmt, is(expected));

        // The rapid biweekly payment for this loan should be $513.29.
        expected = 513.29;
        pmtCalc = PmtCalculators.getCanadianPmtCalculator(PmtPeriod.RAPID_BIWEEKLY, 150000, 5.5, 240);
        pmt = pmtCalc.getPmt();
        assertThat(pmt, is(expected));

        // This one matches up with the rate here: https://www.rbcroyalbank.com/cgi-bin/mortgage/mpc/start.cgi/start
        // The biweekly payment for this loan should be $473.81.
        expected = 473.81;
        pmtCalc = PmtCalculators.getCanadianPmtCalculator(PmtPeriod.BIWEEKLY, 150000, 5.5, 240);
        pmt = pmtCalc.getPmt();
        assertThat(pmt, is(expected));
    }

    @Test
    public void testEquality() {
        // Two CanadianPmtCalculator variables with the same value should be the same object.
        PmtCalculator pmtCalc1 = PmtCalculators.getCanadianPmtCalculator(PmtPeriod.MONTHLY, 200000, 4.5, 30);
        PmtCalculator pmtCalc2 = PmtCalculators.getCanadianPmtCalculator(PmtPeriod.MONTHLY, 200000, 4.5, 30);

        // All statements should be true. Clients won't need to ever call equals() because no two objects with the same
        // value will exist.
        assertTrue(pmtCalc1.equals(pmtCalc2));
        assertTrue(pmtCalc1 == pmtCalc2);
        assertTrue(pmtCalc2.equals(pmtCalc1));
        assertTrue(pmtCalc2 == pmtCalc1);
    }

    @Test
    public void testInequality() {
        PmtCalculator pmtCalc1 = PmtCalculators.getCanadianPmtCalculator(PmtPeriod.MONTHLY, 200000, 4.5, 30);
        PmtCalculator pmtCalc2 = PmtCalculators.getCanadianPmtCalculator(PmtPeriod.MONTHLY, 200000, 4.5, 20);

        // All statements should be false.
        assertFalse(pmtCalc1.equals(pmtCalc2));
        assertFalse(pmtCalc1 == pmtCalc2);
        assertFalse(pmtCalc2.equals(pmtCalc1));
        assertFalse(pmtCalc2 == pmtCalc1);
    }

    @Test
    public void testCompareTo() {
        PmtCalculator pmtCalc1 = PmtCalculators.getCanadianPmtCalculator(PmtPeriod.MONTHLY, 200000.00, 4.5, 30);
        PmtCalculator pmtCalc2 = PmtCalculators.getCanadianPmtCalculator(PmtPeriod.MONTHLY, 200000.00, 4.5, 30);
        PmtCalculator pmtCalc3 = PmtCalculators.getCanadianPmtCalculator(PmtPeriod.MONTHLY, 200000.00, 4.5, 20);

        assertTrue(pmtCalc1.equals(pmtCalc2));
        assertTrue(pmtCalc1 == pmtCalc2);
        assertTrue(pmtCalc1.compareTo(pmtCalc2) == 0);
        assertTrue(pmtCalc2.equals(pmtCalc1));
        assertTrue(pmtCalc2 == pmtCalc1);
        assertTrue(pmtCalc2.compareTo(pmtCalc1) == 0);

        assertFalse(pmtCalc1.equals(pmtCalc3));
        assertFalse(pmtCalc1 == pmtCalc3);
        assertTrue(pmtCalc1.compareTo(pmtCalc3) > 0);
        assertFalse(pmtCalc3.equals(pmtCalc1));
        assertFalse(pmtCalc3 == pmtCalc1);
        assertTrue(pmtCalc3.compareTo(pmtCalc1) < 0);
    }

}
