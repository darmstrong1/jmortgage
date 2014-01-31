package co.da.jmtg.pmt;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DefaultPmtCalculatorTest {

    @Test
    public void testGetPmt() {
        // source for rates: http://www.bankrate.com/calculators/mortgages/mortgage-calculator.aspx

        // The monthly payment for this loan should be $1013.37.
        double expected = 1013.37;
        PmtCalculator pmtCalculator = PmtCalculators.getDefaultPmtCalculator(PmtPeriod.MONTHLY, 200000.00, 4.5, 30);
        double pmt = pmtCalculator.getPmt();
        assertThat(pmt, is(expected));

        // The monthly payment for this loan should be $1097.75.
        expected = 1097.75;
        pmtCalculator = PmtCalculators.getDefaultPmtCalculator(PmtPeriod.MONTHLY, 165000.00, 7.0, 30);
        pmt = pmtCalculator.getPmt();
        assertThat(pmt, is(expected));

        // The monthly payment for this loan should be $1271.79.
        expected = 1271.79;
        pmtCalculator = PmtCalculators.getDefaultPmtCalculator(PmtPeriod.MONTHLY, 183000, 5.625, 20);
        pmt = pmtCalculator.getPmt();
        assertThat(pmt, is(expected));
    }

    @Test
    public void testEquality() {
        // Two DefaultPmtCalculator variables with the same value should be the same object.
        PmtCalculator pmtCalc1 = PmtCalculators.getDefaultPmtCalculator(PmtPeriod.MONTHLY, 200000.00, 4.5, 30);
        PmtCalculator pmtCalc2 = PmtCalculators.getDefaultPmtCalculator(PmtPeriod.MONTHLY, 200000.00, 4.5, 30);

        // Both statements should be true. Clients won't need to ever call equals() because no two objects with the same
        // value will exist.
        assertTrue(pmtCalc1.equals(pmtCalc2));
        assertTrue(pmtCalc1 == pmtCalc2);
    }

    @Test
    public void testInequality() {
        PmtCalculator pmtCalc1 = PmtCalculators.getDefaultPmtCalculator(PmtPeriod.MONTHLY, 200000.00, 4.5, 30);
        PmtCalculator pmtCalc2 = PmtCalculators.getDefaultPmtCalculator(PmtPeriod.MONTHLY, 200000.00, 4.5, 20);

        // Both statements should be false.
        assertFalse(pmtCalc1.equals(pmtCalc2));
        assertFalse(pmtCalc1 == pmtCalc2);
    }

}
