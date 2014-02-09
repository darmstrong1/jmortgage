package co.da.jmtg.amort;

import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import co.da.jmtg.amort.FixedAmortizationCalculator.Payment;
import co.da.jmtg.pmt.PmtCalculator;
import co.da.jmtg.pmt.PmtCalculators;
import co.da.jmtg.pmt.PmtPeriod;
import co.da.jmtg.pmt.extra.ExtraPmt;
import co.da.jmtg.pmt.extra.ExtraPmts;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

public class DefaultFixedAmortizationCalculatorTest {

    private static SortedMap<LocalDate, Payment> tableCase1;
    private static SortedMap<LocalDate, Payment> tableCase2;

    @BeforeClass
    public static void buildCorrectTables() {
        tableCase1 = buildCorrectTable_Case1();
        tableCase2 = buildCorrectTable_Case2();
    }

    @Test
    public void testHashCode() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 30;
        PmtCalculator pmtCalculator1 = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey1 = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);
        FixedAmortizationCalculator amortCalculator1 = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator1, pmtKey1);
        FixedAmortizationCalculator amortCalculator2 = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator1, pmtKey1);
        FixedAmortizationCalculator amortCalculator3 = amortCalculator1.setPmtCalculator(pmtCalculator1
                .setInterestRate(4.0));

        assertTrue(amortCalculator1.hashCode() == amortCalculator2.hashCode());
        assertTrue(amortCalculator1.hashCode() != amortCalculator3.hashCode());
        assertTrue(amortCalculator2.hashCode() != amortCalculator3.hashCode());
    }

    @Test
    public void testGetPmtCalculator() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 30;
        PmtCalculator pmtCalculator = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);
        FixedAmortizationCalculator amortCalculator = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator, pmtKey);

        PmtCalculator returnedPmtCalc = amortCalculator.getPmtCalculator();

        assertTrue(pmtCalculator == returnedPmtCalc);
    }

    @Test
    public void testGetPmtKey() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 30;
        PmtCalculator pmtCalculator = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);
        FixedAmortizationCalculator amortCalculator = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator, pmtKey);

        PmtKey returnedPmtKey = amortCalculator.getPmtKey();

        assertTrue(pmtKey == returnedPmtKey);
    }

    @Test
    public void testSetPmtCalculator() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 30;
        PmtCalculator pmtCalc1 = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);
        FixedAmortizationCalculator amortCalculator1 = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalc1, pmtKey);

        PmtCalculator pmtCalc2 = pmtCalc1.setLoanAmt(3.25);

        FixedAmortizationCalculator amortCalculator2 = amortCalculator1.setPmtCalculator(pmtCalc2);

        PmtCalculator returnedCalc1 = amortCalculator1.getPmtCalculator();
        PmtCalculator returnedCalc2 = amortCalculator2.getPmtCalculator();

        assertTrue(pmtCalc1 == returnedCalc1);
        assertTrue(pmtCalc2 == returnedCalc2);
        assertTrue(pmtCalc1 != returnedCalc2);
        assertTrue(pmtCalc2 != returnedCalc1);
    }

    @Test
    public void testSetPmtKey() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 30;
        PmtCalculator pmtCalc = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey1 = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);
        FixedAmortizationCalculator amortCalculator1 = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalc, pmtKey1);

        PmtKey pmtKey2 = pmtKey1.setFirstKey(pmtKey1.getFirstKey().plus(Period.months(3)));

        FixedAmortizationCalculator amortCalculator2 = amortCalculator1.setPmtKey(pmtKey2);

        PmtKey returnedKey1 = amortCalculator1.getPmtKey();
        PmtKey returnedKey2 = amortCalculator2.getPmtKey();

        assertTrue(pmtKey1 == returnedKey1);
        assertTrue(pmtKey2 == returnedKey2);
        assertTrue(pmtKey1 != returnedKey2);
        assertTrue(pmtKey2 != returnedKey1);
    }

    @Test
    public void testSetExtraPayment() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 30;
        PmtCalculator pmtCalculator = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);

        FixedAmortizationCalculator amortCalculator = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator, pmtKey);

        // Set a one time extra payment on the first payment.
        PmtKey pmtKeyExtra = PmtKeys.getDefaultPmtKey(PmtPeriod.ONETIME, pmtKey.getFirstKey());
        ExtraPmt extraPmt = ExtraPmts.getDefaultExtraPmt(pmtKeyExtra, 5000.00);
        amortCalculator = amortCalculator.setExtraPayment(extraPmt);

        double extraAmt = amortCalculator.getExtraPayment(pmtKey.getFirstKey());

        assertTrue(extraPmt.getAmount() == extraAmt);
    }

    @Test
    public void testSetExtraPayments() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 30;
        PmtCalculator pmtCalculator = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);

        FixedAmortizationCalculator amortCalculator = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator, pmtKey);

        // Set a one time extra payment on the first payment.
        PmtKey pmtKeyExtra = PmtKeys.getDefaultPmtKey(PmtPeriod.ONETIME, pmtKey.getFirstKey());
        ExtraPmt extraPmt = ExtraPmts.getDefaultExtraPmt(pmtKeyExtra, 5000.00);

        // Set a second one time extra payment right after the first payment.
        List<LocalDate> dateKeys = pmtKey.getKeys();
        LocalDate secondKey = dateKeys.get(1); // get the second date key.
        PmtKey pmtKeyExtra2 = pmtKeyExtra.setFirstKey(secondKey);
        ExtraPmt extraPmt2 = extraPmt.setPmtKey(pmtKeyExtra2).setAmount(3000.00);

        List<ExtraPmt> extraPmts = ImmutableList.of(extraPmt, extraPmt2);

        amortCalculator = amortCalculator.setExtraPayments(extraPmts);

        double extraAmt = amortCalculator.getExtraPayment(pmtKeyExtra.getFirstKey());
        double extraAmt2 = amortCalculator.getExtraPayment(pmtKeyExtra2.getFirstKey());

        assertTrue(extraPmt.getAmount() == extraAmt);
        assertTrue(extraPmt2.getAmount() == extraAmt2);
    }

    @Test
    public void testAddExtraPayment() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 30;
        PmtCalculator pmtCalculator = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);

        FixedAmortizationCalculator amortCalculator = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator, pmtKey);

        // Set a one time extra payment on the first payment.
        PmtKey pmtKeyExtra = PmtKeys.getDefaultPmtKey(PmtPeriod.ONETIME, pmtKey.getFirstKey());
        ExtraPmt extraPmt = ExtraPmts.getDefaultExtraPmt(pmtKeyExtra, 5000.00);
        amortCalculator = amortCalculator.setExtraPayment(extraPmt);

        // Now, add to the extra payment we just set.
        ExtraPmt extraPmt2 = extraPmt.setAmount(200.00);
        amortCalculator = amortCalculator.addExtraPayment(extraPmt2);

        double extraAmt = amortCalculator.getExtraPayment(pmtKey.getFirstKey());

        // The extra amount should now be the sum of the two amounts we added for that payment.
        assertTrue(extraPmt.getAmount() + extraPmt2.getAmount() == extraAmt);
    }

    @Test
    public void testAddExtraPayments() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 30;
        PmtCalculator pmtCalculator = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);

        FixedAmortizationCalculator amortCalculator = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator, pmtKey);

        // Set a monthly extra payment for the first twelve months.
        PmtKey pmtKeyExtra = PmtKeys.getDefaultPmtKey(PmtPeriod.MONTHLY, 12);
        double extraAmt = 500.00;
        ExtraPmt extraPmt = ExtraPmts.getDefaultExtraPmt(pmtKeyExtra, extraAmt);

        // Set a monthly extra payment for the second twelve months.
        List<LocalDate> keys = pmtKey.getKeys();
        PmtKey pmtKeyExta2 = pmtKeyExtra.setFirstKey(keys.get(12));
        double extraAmt2 = 750.00;
        ExtraPmt extraPmt2 = ExtraPmts.getDefaultExtraPmt(pmtKeyExta2, extraAmt2);
        // Add them both.
        amortCalculator = amortCalculator.setExtraPayments(ImmutableList.of(extraPmt, extraPmt2));

        // Add 250.00 to both the extra payments just added.
        double addedExtraAmt = 250;
        ExtraPmt addedExtraPmt = extraPmt.setAmount(addedExtraAmt);
        ExtraPmt addedExtraPmt2 = extraPmt2.setAmount(addedExtraAmt);

        amortCalculator = amortCalculator.addExtraPayments(ImmutableList.of(addedExtraPmt, addedExtraPmt2));

        // Make sure the extra payments are all a sum of what was first set, then added.
        Map<LocalDate, Double> extraPmts = amortCalculator.getExtraPayments();
        int ct = 0;
        Iterator<LocalDate> iterator = extraPmts.keySet().iterator();
        while (ct++ < 12) {
            assertTrue(extraPmt.getAmount() + addedExtraPmt.getAmount() == extraPmts.get(iterator.next()));
        }

        while (ct++ < 24) {
            assertTrue(extraPmt2.getAmount() + addedExtraPmt2.getAmount() == extraPmts.get(iterator.next()));
        }
    }

    @Test
    public void testRemoveExtraPayment() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 30;
        PmtCalculator pmtCalculator = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);

        FixedAmortizationCalculator amortCalculator = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator, pmtKey);

        // Set a monthly extra payment for the first twelve months.
        PmtKey pmtKeyExtra = PmtKeys.getDefaultPmtKey(PmtPeriod.MONTHLY, 12);
        double extraAmt = 500.00;
        ExtraPmt extraPmt = ExtraPmts.getDefaultExtraPmt(pmtKeyExtra, extraAmt);
        amortCalculator = amortCalculator.setExtraPayment(extraPmt);

        // Remove the first extra payment set.
        amortCalculator = amortCalculator.removeExtraPayment(pmtKey.getFirstKey());

        int ct = 0;
        assertTrue(amortCalculator.getExtraPayment(pmtKey.getFirstKey()) == 0.0);
        Iterator<LocalDate> iterator = pmtKey.iterator();
        iterator.next();
        while (ct++ < 11) {
            assertTrue(amortCalculator.getExtraPayment(iterator.next()) == extraAmt);
        }
    }

    @Test
    public void testRemoveExtraPayments() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 30;
        PmtCalculator pmtCalculator = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);

        FixedAmortizationCalculator amortCalculator = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator, pmtKey);

        // Set a monthly extra payment for the first twelve months.
        PmtKey pmtKeyExtra = PmtKeys.getDefaultPmtKey(PmtPeriod.MONTHLY, 12);
        double extraAmt = 500.00;
        ExtraPmt extraPmt = ExtraPmts.getDefaultExtraPmt(pmtKeyExtra, extraAmt);
        amortCalculator = amortCalculator.setExtraPayment(extraPmt);

        // Remove the first six extra payments.
        List<LocalDate> keys = pmtKey.getKeys();
        amortCalculator = amortCalculator.removeExtraPayments(keys.subList(0, 6));

        int ct = 0;
        Iterator<LocalDate> iterator = pmtKey.iterator();
        while (ct++ < 6) {
            assertTrue(amortCalculator.getExtraPayment(iterator.next()) == 0.0);
        }
        while (ct++ < 12) {
            assertTrue(amortCalculator.getExtraPayment(iterator.next()) == extraAmt);
        }
    }

    @Test
    public void testClearExtraPayments() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 30;
        PmtCalculator pmtCalculator = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);

        FixedAmortizationCalculator amortCalculator = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator, pmtKey);

        // Set a monthly extra payment for all payments.
        PmtKey pmtKeyExtra = PmtKeys.getDefaultPmtKey(PmtPeriod.MONTHLY, 12 * 30);
        double extraAmt = 500.00;
        ExtraPmt extraPmt = ExtraPmts.getDefaultExtraPmt(pmtKeyExtra, extraAmt);
        amortCalculator = amortCalculator.setExtraPayment(extraPmt);

        Map<LocalDate, Double> extraPmts = amortCalculator.getExtraPayments();
        // Assert that the extra payment is set for all payments.
        for (LocalDate key : extraPmts.keySet()) {
            assertTrue(extraPmts.get(key) == extraAmt);
        }

        // Clear all the extra payments in amortCalculator.
        amortCalculator = amortCalculator.clearExtraPayments();

        // Assert that the extra payment is still set for all payments in the extraPmts map. It should be because
        // getExtraPayments returns a copy of the extra payment map.
        for (LocalDate key : extraPmts.keySet()) {
            assertTrue(extraPmts.get(key) == extraAmt);
        }

        // Get the extra payments out of amortCalculator, which should be an empty map.
        extraPmts = amortCalculator.getExtraPayments();
        assertTrue(extraPmts.isEmpty());

    }

    @Test
    public void testEqualsObject() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 30;
        PmtCalculator pmtCalculator1 = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey1 = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);
        FixedAmortizationCalculator amortCalculator1 = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator1, pmtKey1);
        FixedAmortizationCalculator amortCalculator2 = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator1, pmtKey1);
        FixedAmortizationCalculator amortCalculator3 = amortCalculator1.setPmtCalculator(pmtCalculator1
                .setInterestRate(4.0));

        assertTrue(amortCalculator1.equals(amortCalculator2));
        assertTrue(amortCalculator1 == amortCalculator2);
        assertTrue(amortCalculator2.equals(amortCalculator1));
        assertTrue(amortCalculator2 == amortCalculator1);
        assertTrue(amortCalculator1.equals(amortCalculator3) == false);
        assertTrue(amortCalculator3.equals(amortCalculator1) == false);
        assertTrue(amortCalculator2.equals(amortCalculator3) == false);
        assertTrue(amortCalculator3.equals(amortCalculator2) == false);
    }

    @Test
    public void testBuildTable_Case1() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 20;
        PmtCalculator pmtCalculator = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);

        FixedAmortizationCalculator amortCalculator = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(
                        pmtCalculator, pmtKey);

        SortedMap<LocalDate, Payment> amortTable = amortCalculator.getTable();
        System.out.println(amortTable);

        System.out.println(tableCase1);

        assertTrue(amortTable.size() == tableCase1.size());

        Set<LocalDate> keys = amortTable.keySet();

        for (LocalDate key : keys) {
            Payment testPmt = amortTable.get(key);
            Payment rightPmt = tableCase1.get(key);
            // Must call rightPmt's equals for this to work because it is a TestingPayment object and its equals method
            // will compare to an object that implements the Payment interface. It does not have to be a TestingPayment
            // object.
            assertTrue(rightPmt.equals(testPmt));
        }
    }

    // Same as Case 1, but it has extra payments of 500 per month every month.
    @Test
    public void testBuildTable_Case2() {
        PmtPeriod pmtPeriod = PmtPeriod.MONTHLY;
        double loanAmt = 150000.00;
        double interestRate = 4.25;
        int years = 20;
        PmtCalculator pmtCalculator = PmtCalculators.getDefaultPmtCalculator(pmtPeriod, loanAmt, interestRate, years);
        PmtKey pmtKey = PmtKeys.getDefaultPmtKeyForYears(pmtPeriod, years);

        // Create the calculator with extra payments.
        FixedAmortizationCalculator amortCalculator = FixedAmortizationCalculators
                .getDefaultFixedAmortizationCalculator(pmtCalculator, pmtKey,
                        ExtraPmts.getDefaultExtraPmt(pmtKey, 500.0));

        SortedMap<LocalDate, Payment> amortTable = amortCalculator.getTable();
        System.out.println(amortTable);

        System.out.println(tableCase2);

        assertTrue(amortTable.size() == tableCase2.size());

        Set<LocalDate> keys = amortTable.keySet();

        for (LocalDate key : keys) {
            Payment testPmt = amortTable.get(key);
            Payment rightPmt = tableCase2.get(key);
            // Must call rightPmt's equals for this to work because it is a TestingPayment object and its equals method
            // will compare to an object that implements the Payment interface. It does not have to be a TestingPayment
            // object.
            assertTrue(rightPmt.equals(testPmt));
        }
    }

    @Ignore
    private static SortedMap<LocalDate, Payment> buildCorrectTable_Case1() {
        // Build a correct table for a loan of 150,000 with an interest rate of 4.25, monthly payment for 20 years.
        // This is what testBuildTable() should build. These values were retrieved from
        // http://www.bankrate.com/calculators/mortgages/mortgage-calculator.aspx

        ImmutableSortedMap.Builder<LocalDate, Payment> builder = new ImmutableSortedMap.Builder<>(Ordering.natural());
        Period month = Period.months(1);
        // Add one month. The current date is the start date of the mortgage, not of the first payment.
        LocalDate key = LocalDate.now();
        builder.put(key, new TestingPayment(928.85, 397.60, 531.25, 531.25, 149602.40));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 399.01, 529.84, 1061.09, 149203.39));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 400.42, 528.43, 1589.52, 148802.97));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 401.84, 527.01, 2116.53, 148401.12));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 403.26, 525.59, 2642.12, 147997.86));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 404.69, 524.16, 3166.28, 147593.17));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 406.13, 522.73, 3689.00, 147187.04));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 407.56, 521.29, 4210.29, 146779.48));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 409.01, 519.84, 4730.13, 146370.47));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 410.46, 518.40, 5248.53, 145960.01));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 411.91, 516.94, 5765.47, 145548.10));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 413.37, 515.48, 6280.95, 145134.73));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 414.83, 514.02, 6794.97, 144719.90));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 416.30, 512.55, 7307.52, 144303.60));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 417.78, 511.08, 7818.60, 143885.82));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 419.26, 509.60, 8328.19, 143466.57));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 420.74, 508.11, 8836.30, 143045.83));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 422.23, 506.62, 9342.93, 142623.59));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 423.73, 505.13, 9848.05, 142199.87));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 425.23, 503.62, 10351.68, 141774.64));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 426.73, 502.12, 10853.79, 141347.91));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 428.24, 500.61, 11354.40, 140919.66));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 429.76, 499.09, 11853.49, 140489.90));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 431.28, 497.57, 12351.06, 140058.62));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 432.81, 496.04, 12847.10, 139625.81));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 434.34, 494.51, 13341.61, 139191.46));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 435.88, 492.97, 13834.58, 138755.58));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 437.43, 491.43, 14326.00, 138318.16));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 438.97, 489.88, 14815.88, 137879.18));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 440.53, 488.32, 15304.20, 137438.65));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 442.09, 486.76, 15790.97, 136996.56));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 443.66, 485.20, 16276.16, 136552.91));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 445.23, 483.62, 16759.79, 136107.68));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 446.80, 482.05, 17241.83, 135660.88));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 448.39, 480.47, 17722.30, 135212.49));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 449.97, 478.88, 18201.18, 134762.52));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 451.57, 477.28, 18678.46, 134310.95));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 453.17, 475.68, 19154.15, 133857.78));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 454.77, 474.08, 19628.23, 133403.01));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 456.38, 472.47, 20100.69, 132946.63));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 458.00, 470.85, 20571.55, 132488.63));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 459.62, 469.23, 21040.78, 132029.01));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 461.25, 467.60, 21508.38, 131567.76));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 462.88, 465.97, 21974.35, 131104.87));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 464.52, 464.33, 22438.68, 130640.35));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 466.17, 462.68, 22901.36, 130174.19));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 467.82, 461.03, 23362.40, 129706.37));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 469.47, 459.38, 23821.77, 129236.89));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 471.14, 457.71, 24279.49, 128765.75));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 472.81, 456.05, 24735.53, 128292.95));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 474.48, 454.37, 25189.90, 127818.47));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 476.16, 452.69, 25642.60, 127342.31));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 477.85, 451.00, 26093.60, 126864.46));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 479.54, 449.31, 26542.91, 126384.92));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 481.24, 447.61, 26990.52, 125903.68));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 482.94, 445.91, 27436.43, 125420.74));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 484.65, 444.20, 27880.63, 124936.08));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 486.37, 442.48, 28323.11, 124449.71));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 488.09, 440.76, 28763.87, 123961.62));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 489.82, 439.03, 29202.90, 123471.80));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 491.56, 437.30, 29640.20, 122980.25));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 493.30, 435.56, 30075.75, 122486.95));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 495.04, 433.81, 30509.56, 121991.90));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 496.80, 432.05, 30941.62, 121495.11));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 498.56, 430.30, 31371.91, 120996.55));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 500.32, 428.53, 31800.44, 120496.23));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 502.09, 426.76, 32227.20, 119994.13));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 503.87, 424.98, 32652.18, 119490.26));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 505.66, 423.19, 33075.37, 118984.61));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 507.45, 421.40, 33496.78, 118477.16));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 509.25, 419.61, 33916.38, 117967.91));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 511.05, 417.80, 34334.19, 117456.86));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 512.86, 415.99, 34750.18, 116944.01));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 514.68, 414.18, 35164.36, 116429.33));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 516.50, 412.35, 35576.71, 115912.83));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 518.33, 410.52, 35987.23, 115394.51));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 520.16, 408.69, 36395.92, 114874.34));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 522.01, 406.85, 36802.77, 114352.34));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 523.85, 405.00, 37207.77, 113828.48));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 525.71, 403.14, 37610.91, 113302.77));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 527.57, 401.28, 38012.19, 112775.20));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 529.44, 399.41, 38411.60, 112245.76));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 531.31, 397.54, 38809.14, 111714.45));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 533.20, 395.66, 39204.80, 111181.25));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 535.08, 393.77, 39598.56, 110646.17));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 536.98, 391.87, 39990.43, 110109.19));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 538.88, 389.97, 40380.40, 109570.31));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 540.79, 388.06, 40768.47, 109029.52));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 542.71, 386.15, 41154.61, 108486.81));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 544.63, 384.22, 41538.84, 107942.18));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 546.56, 382.30, 41921.13, 107395.63));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 548.49, 380.36, 42301.49, 106847.13));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 550.43, 378.42, 42679.91, 106296.70));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 552.38, 376.47, 43056.38, 105744.32));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 554.34, 374.51, 43430.89, 105189.97));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 556.30, 372.55, 43803.43, 104633.67));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 558.27, 370.58, 44174.01, 104075.40));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 560.25, 368.60, 44542.61, 103515.15));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 562.24, 366.62, 44909.23, 102952.91));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 564.23, 364.62, 45273.85, 102388.68));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 566.23, 362.63, 45636.48, 101822.46));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 568.23, 360.62, 45997.10, 101254.23));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 570.24, 358.61, 46355.71, 100683.98));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 572.26, 356.59, 46712.30, 100111.72));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 574.29, 354.56, 47066.86, 99537.43));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 576.32, 352.53, 47419.39, 98961.11));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 578.36, 350.49, 47769.88, 98382.74));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 580.41, 348.44, 48118.32, 97802.33));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 582.47, 346.38, 48464.70, 97219.86));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 584.53, 344.32, 48809.02, 96635.33));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 586.60, 342.25, 49151.27, 96048.73));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 588.68, 340.17, 49491.44, 95460.05));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 590.76, 338.09, 49829.53, 94869.29));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 592.86, 336.00, 50165.53, 94276.43));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 594.96, 333.90, 50499.42, 93681.48));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 597.06, 331.79, 50831.21, 93084.41));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 599.18, 329.67, 51160.88, 92485.23));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 601.30, 327.55, 51488.44, 91883.93));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 603.43, 325.42, 51813.86, 91280.51));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 605.57, 323.29, 52137.14, 90674.94));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 607.71, 321.14, 52458.28, 90067.23));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 609.86, 318.99, 52777.27, 89457.36));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 612.02, 316.83, 53094.10, 88845.34));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 614.19, 314.66, 53408.76, 88231.15));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 616.37, 312.49, 53721.25, 87614.78));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 618.55, 310.30, 54031.55, 86996.23));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 620.74, 308.11, 54339.66, 86375.49));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 622.94, 305.91, 54645.57, 85752.55));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 625.14, 303.71, 54949.28, 85127.41));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 627.36, 301.49, 55250.77, 84500.05));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 629.58, 299.27, 55550.04, 83870.47));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 631.81, 297.04, 55847.08, 83238.66));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 634.05, 294.80, 56141.89, 82604.61));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 636.29, 292.56, 56434.45, 81968.32));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 638.55, 290.30, 56724.75, 81329.77));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 640.81, 288.04, 57012.79, 80688.96));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 643.08, 285.77, 57298.57, 80045.88));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 645.36, 283.50, 57582.06, 79400.53));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 647.64, 281.21, 57863.27, 78752.89));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 649.94, 278.92, 58142.19, 78102.95));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 652.24, 276.61, 58418.80, 77450.71));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 654.55, 274.30, 58693.11, 76796.17));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 656.87, 271.99, 58965.10, 76139.30));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 659.19, 269.66, 59234.76, 75480.11));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 661.53, 267.33, 59502.08, 74818.58));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 663.87, 264.98, 59767.06, 74154.71));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 666.22, 262.63, 60029.69, 73488.49));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 668.58, 260.27, 60289.97, 72819.91));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 670.95, 257.90, 60547.87, 72148.97));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 673.32, 255.53, 60803.40, 71475.64));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 675.71, 253.14, 61056.54, 70799.93));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 678.10, 250.75, 61307.29, 70121.83));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 680.50, 248.35, 61555.64, 69441.33));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 682.91, 245.94, 61801.58, 68758.41));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 685.33, 243.52, 62045.10, 68073.08));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 687.76, 241.09, 62286.19, 67385.32));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 690.20, 238.66, 62524.84, 66695.13));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 692.64, 236.21, 62761.06, 66002.49));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 695.09, 233.76, 62994.82, 65307.39));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 697.55, 231.30, 63226.11, 64609.84));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 700.03, 228.83, 63454.94, 63909.81));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 702.50, 226.35, 63681.29, 63207.31));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 704.99, 223.86, 63905.15, 62502.32));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 707.49, 221.36, 64126.51, 61794.83));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 710.00, 218.86, 64345.36, 61084.83));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 712.51, 216.34, 64561.71, 60372.32));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 715.03, 213.82, 64775.53, 59657.29));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 717.57, 211.29, 64986.81, 58939.73));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 720.11, 208.74, 65195.56, 58219.62));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 722.66, 206.19, 65401.75, 57496.96));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 725.22, 203.64, 65605.39, 56771.74));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 727.79, 201.07, 65806.45, 56043.96));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 730.36, 198.49, 66004.94, 55313.60));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 732.95, 195.90, 66200.84, 54580.65));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 735.55, 193.31, 66394.15, 53845.10));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 738.15, 190.70, 66584.85, 53106.95));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 740.76, 188.09, 66772.94, 52366.19));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 743.39, 185.46, 66958.40, 51622.80));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 746.02, 182.83, 67141.23, 50876.78));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 748.66, 180.19, 67321.42, 50128.11));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 751.31, 177.54, 67498.96, 49376.80));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 753.98, 174.88, 67673.83, 48622.82));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 756.65, 172.21, 67846.04, 47866.18));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 759.33, 169.53, 68015.57, 47106.85));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 762.01, 166.84, 68182.40, 46344.84));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 764.71, 164.14, 68346.54, 45580.12));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 767.42, 161.43, 68507.97, 44812.70));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 770.14, 158.71, 68666.68, 44042.56));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 772.87, 155.98, 68822.67, 43269.69));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 775.60, 153.25, 68975.91, 42494.09));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 778.35, 150.50, 69126.41, 41715.74));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 781.11, 147.74, 69274.16, 40934.63));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 783.87, 144.98, 69419.13, 40150.75));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 786.65, 142.20, 69561.33, 39364.10));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 789.44, 139.41, 69700.75, 38574.67));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 792.23, 136.62, 69837.37, 37782.43));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 795.04, 133.81, 69971.18, 36987.39));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 797.85, 131.00, 70102.18, 36189.54));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 800.68, 128.17, 70230.35, 35388.86));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 803.52, 125.34, 70355.68, 34585.34));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 806.36, 122.49, 70478.17, 33778.98));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 809.22, 119.63, 70597.81, 32969.76));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 812.08, 116.77, 70714.58, 32157.68));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 814.96, 113.89, 70828.47, 31342.72));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 817.85, 111.01, 70939.47, 30524.87));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 820.74, 108.11, 71047.58, 29704.13));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 823.65, 105.20, 71152.78, 28880.48));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 826.57, 102.29, 71255.07, 28053.91));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 829.49, 99.36, 71354.43, 27224.42));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 832.43, 96.42, 71450.85, 26391.99));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 835.38, 93.47, 71544.32, 25556.61));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 838.34, 90.51, 71634.83, 24718.27));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 841.31, 87.54, 71722.37, 23876.96));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 844.29, 84.56, 71806.94, 23032.67));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 847.28, 81.57, 71888.51, 22185.40));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 850.28, 78.57, 71967.09, 21335.12));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 853.29, 75.56, 72042.65, 20481.83));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 856.31, 72.54, 72115.19, 19625.52));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 859.34, 69.51, 72184.70, 18766.17));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 862.39, 66.46, 72251.16, 17903.78));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 865.44, 63.41, 72314.57, 17038.34));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 868.51, 60.34, 72374.91, 16169.83));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 871.58, 57.27, 72432.18, 15298.25));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 874.67, 54.18, 72486.36, 14423.58));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 877.77, 51.08, 72537.44, 13545.81));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 880.88, 47.97, 72585.42, 12664.93));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 884.00, 44.85, 72630.27, 11780.94));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 887.13, 41.72, 72672.00, 10893.81));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 890.27, 38.58, 72710.58, 10003.54));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 893.42, 35.43, 72746.01, 9110.12));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 896.59, 32.27, 72778.28, 8213.53));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 899.76, 29.09, 72807.36, 7313.77));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 902.95, 25.90, 72833.27, 6410.82));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 906.15, 22.70, 72855.97, 5504.67));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 909.36, 19.50, 72875.47, 4595.32));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 912.58, 16.28, 72891.74, 3682.74));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 915.81, 13.04, 72904.79, 2766.93));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 919.05, 9.80, 72914.59, 1847.88));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 922.31, 6.54, 72921.13, 925.57));
        key = key.plus(month);
        builder.put(key, new TestingPayment(928.85, 925.57, 3.28, 72924.41, 0.00));

        return builder.build();
    }

    @Ignore
    private static SortedMap<LocalDate, Payment> buildCorrectTable_Case2() {
        // Build a correct table for a loan of 150,000 with an interest rate of 4.25, monthly payment for 20 years and
        // extra payments per month of $500. This is what testBuildTable() should build. These values were retrieved
        // from http://www.bankrate.com/calculators/mortgages/mortgage-calculator.aspx

        ImmutableSortedMap.Builder<LocalDate, Payment> builder = new ImmutableSortedMap.Builder<>(Ordering.natural());
        Period month = Period.months(1);
        // Add one month. The current date is the start date of the mortgage, not of the first payment.
        LocalDate key = LocalDate.now();
        builder.put(key, new TestingPayment(1428.85, 897.60, 500.0, 531.25, 531.25, 149102.40));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 900.78, 500.0, 528.07, 1059.32, 148201.62));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 903.97, 500.0, 524.88, 1584.20, 147297.65));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 907.17, 500.0, 521.68, 2105.88, 146390.47));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 910.39, 500.0, 518.47, 2624.35, 145480.09));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 913.61, 500.0, 515.24, 3139.59, 144566.48));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 916.85, 500.0, 512.01, 3651.60, 143649.63));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 920.09, 500.0, 508.76, 4160.35, 142729.54));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 923.35, 500.0, 505.50, 4665.85, 141806.19));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 926.62, 500.0, 502.23, 5168.09, 140879.57));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 929.90, 500.0, 498.95, 5667.03, 139949.66));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 933.20, 500.0, 495.66, 6162.69, 139016.47));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 936.50, 500.0, 492.35, 6655.04, 138079.97));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 939.82, 500.0, 489.03, 7144.07, 137140.15));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 943.15, 500.0, 485.70, 7629.78, 136197.00));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 946.49, 500.0, 482.36, 8112.14, 135250.51));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 949.84, 500.0, 479.01, 8591.15, 134300.67));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 953.20, 500.0, 475.65, 9066.80, 133347.47));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 956.58, 500.0, 472.27, 9539.07, 132390.89));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 959.97, 500.0, 468.88, 10007.96, 131430.92));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 963.37, 500.0, 465.48, 10473.44, 130467.56));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 966.78, 500.0, 462.07, 10935.52, 129500.78));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 970.20, 500.0, 458.65, 11394.16, 128530.57));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 973.64, 500.0, 455.21, 11849.38, 127556.94));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 977.09, 500.0, 451.76, 12301.14, 126579.85));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 980.55, 500.0, 448.30, 12749.44, 125599.30));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 984.02, 500.0, 444.83, 13194.27, 124615.28));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 987.51, 500.0, 441.35, 13635.62, 123627.77));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 991.00, 500.0, 437.85, 14073.47, 122636.77));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 994.51, 500.0, 434.34, 14507.81, 121642.26));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 998.04, 500.0, 430.82, 14938.62, 120644.22));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1001.57, 500.0, 427.28, 15365.91, 119642.65));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1005.12, 500.0, 423.73, 15789.64, 118637.53));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1008.68, 500.0, 420.17, 16209.81, 117628.86));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1012.25, 500.0, 416.60, 16626.42, 116616.61));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1015.83, 500.0, 413.02, 17039.43, 115600.77));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1019.43, 500.0, 409.42, 17448.85, 114581.34));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1023.04, 500.0, 405.81, 17854.66, 113558.30));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1026.67, 500.0, 402.19, 18256.85, 112531.63));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1030.30, 500.0, 398.55, 18655.40, 111501.33));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1033.95, 500.0, 394.90, 19050.30, 110467.38));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1037.61, 500.0, 391.24, 19441.54, 109429.77));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1041.29, 500.0, 387.56, 19829.10, 108388.48));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1044.98, 500.0, 383.88, 20212.98, 107343.50));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1048.68, 500.0, 380.17, 20593.15, 106294.82));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1052.39, 500.0, 376.46, 20969.61, 105242.43));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1056.12, 500.0, 372.73, 21342.35, 104186.32));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1059.86, 500.0, 368.99, 21711.34, 103126.46));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1063.61, 500.0, 365.24, 22076.58, 102062.84));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1067.38, 500.0, 361.47, 22438.05, 100995.47));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1071.16, 500.0, 357.69, 22795.74, 99924.31));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1074.95, 500.0, 353.90, 23149.64, 98849.35));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1078.76, 500.0, 350.09, 23499.73, 97770.59));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1082.58, 500.0, 346.27, 23846.00, 96688.01));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1086.41, 500.0, 342.44, 24188.44, 95601.60));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1090.26, 500.0, 338.59, 24527.03, 94511.33));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1094.12, 500.0, 334.73, 24861.76, 93417.21));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1098.00, 500.0, 330.85, 25192.61, 92319.21));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1101.89, 500.0, 326.96, 25519.57, 91217.32));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1105.79, 500.0, 323.06, 25842.64, 90111.53));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1109.71, 500.0, 319.15, 26161.78, 89001.83));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1113.64, 500.0, 315.21, 26477.00, 87888.19));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1117.58, 500.0, 311.27, 26788.27, 86770.61));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1121.54, 500.0, 307.31, 27095.58, 85649.07));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1125.51, 500.0, 303.34, 27398.92, 84523.56));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1129.50, 500.0, 299.35, 27698.27, 83394.06));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1133.50, 500.0, 295.35, 27993.63, 82260.56));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1137.51, 500.0, 291.34, 28284.97, 81123.05));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1141.54, 500.0, 287.31, 28572.28, 79981.51));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1145.58, 500.0, 283.27, 28855.55, 78835.93));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1149.64, 500.0, 279.21, 29134.76, 77686.28));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1153.71, 500.0, 275.14, 29409.89, 76532.57));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1157.80, 500.0, 271.05, 29680.95, 75374.77));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1161.90, 500.0, 266.95, 29947.90, 74212.87));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1166.01, 500.0, 262.84, 30210.74, 73046.86));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1170.14, 500.0, 258.71, 30469.44, 71876.72));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1174.29, 500.0, 254.56, 30724.01, 70702.43));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1178.45, 500.0, 250.40, 30974.41, 69523.98));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1182.62, 500.0, 246.23, 31220.64, 68341.36));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1186.81, 500.0, 242.04, 31462.69, 67154.55));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1191.01, 500.0, 237.84, 31700.52, 65963.54));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1195.23, 500.0, 233.62, 31934.15, 64768.31));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1199.46, 500.0, 229.39, 32163.53, 63568.84));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1203.71, 500.0, 225.14, 32388.67, 62365.13));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1207.98, 500.0, 220.88, 32609.55, 61157.15));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1212.25, 500.0, 216.60, 32826.15, 59944.90));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1216.55, 500.0, 212.30, 33038.45, 58728.35));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1220.86, 500.0, 208.00, 33246.45, 57507.50));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1225.18, 500.0, 203.67, 33450.12, 56282.32));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1229.52, 500.0, 199.33, 33649.45, 55052.80));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1233.87, 500.0, 194.98, 33844.43, 53818.93));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1238.24, 500.0, 190.61, 34035.04, 52580.68));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1242.63, 500.0, 186.22, 34221.26, 51338.06));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1247.03, 500.0, 181.82, 34403.09, 50091.03));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1251.45, 500.0, 177.41, 34580.49, 48839.58));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1255.88, 500.0, 172.97, 34753.47, 47583.70));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1260.33, 500.0, 168.53, 34921.99, 46323.38));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1264.79, 500.0, 164.06, 35086.05, 45058.59));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1269.27, 500.0, 159.58, 35245.64, 43789.32));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1273.76, 500.0, 155.09, 35400.72, 42515.55));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1278.28, 500.0, 150.58, 35551.30, 41237.28));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1282.80, 500.0, 146.05, 35697.35, 39954.47));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1287.35, 500.0, 141.51, 35838.85, 38667.13));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1291.91, 500.0, 136.95, 35975.80, 37375.22));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1296.48, 500.0, 132.37, 36108.17, 36078.74));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1301.07, 500.0, 127.78, 36235.95, 34777.67));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1305.68, 500.0, 123.17, 36359.12, 33471.99));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1310.31, 500.0, 118.55, 36477.67, 32161.68));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1314.95, 500.0, 113.91, 36591.57, 30846.74));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1319.60, 500.0, 109.25, 36700.82, 29527.13));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1324.28, 500.0, 104.58, 36805.40, 28202.86));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1328.97, 500.0, 99.89, 36905.28, 26873.89));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1333.67, 500.0, 95.18, 37000.46, 25540.22));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1338.40, 500.0, 90.45, 37090.92, 24201.82));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1343.14, 500.0, 85.71, 37176.63, 22858.68));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1347.89, 500.0, 80.96, 37257.59, 21510.79));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1352.67, 500.0, 76.18, 37333.77, 20158.12));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1357.46, 500.0, 71.39, 37405.17, 18800.66));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1362.27, 500.0, 66.59, 37471.75, 17438.40));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1367.09, 500.0, 61.76, 37533.51, 16071.31));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1371.93, 500.0, 56.92, 37590.43, 14699.38));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1376.79, 500.0, 52.06, 37642.49, 13322.58));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1381.67, 500.0, 47.18, 37689.68, 11940.92));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1386.56, 500.0, 42.29, 37731.97, 10554.36));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1391.47, 500.0, 37.38, 37769.35, 9162.88));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1396.40, 500.0, 32.45, 37801.80, 7766.48));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1401.35, 500.0, 27.51, 37829.30, 6365.14));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1406.31, 500.0, 22.54, 37851.85, 4958.83));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1411.29, 500.0, 17.56, 37869.41, 3547.54));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1416.29, 500.0, 12.56, 37881.97, 2131.25));
        key = key.plus(month);
        builder.put(key, new TestingPayment(1428.85, 1421.30, 500.0, 7.55, 37889.52, 709.95));
        key = key.plus(month);
        builder.put(key, new TestingPayment(712.46, 709.95, 500.0, 2.51, 37892.04, 0.00));

        return builder.build();
    }

    private static class TestingPayment implements FixedAmortizationCalculator.Payment {

        private final double total; // the total amount paid for this payment
        private final double principal; // the principal paid for this payment
        private final double extraPrincipal; // the extra principal paid for this payment, if any.
        private final double interest; // the interest paid for this payment
        private final double cumulativeInterest; // the cumulative interest paid after this payment
        private final double balance; // the balance due after this payment

        private volatile int hashCode;

        public TestingPayment(double total, double principal, double extraPrincipal, double interest,
                double cumulativeInterest, double balance) {
            this.total = total;
            this.principal = principal;
            this.extraPrincipal = extraPrincipal;
            this.interest = interest;
            this.cumulativeInterest = cumulativeInterest;
            this.balance = balance;
        }

        public TestingPayment(double total, double principal, double interest, double cumulativeInterest, double balance) {
            this(total, principal, 0.0, interest, cumulativeInterest, balance);
        }

        @Override
        public double getTotal() {
            return total;
        }

        @Override
        public double getPrincipal() {
            return principal;
        }

        @Override
        public double getExtraPrincipal() {
            return extraPrincipal;
        }

        @Override
        public double getInterest() {
            return interest;
        }

        @Override
        public double getBalance() {
            return balance;
        }

        @Override
        public double getCumulativeInterest() {
            return cumulativeInterest;
        }

        // Not concerned with verifying unrounded values...
        @Override
        public double getTotalUnrounded() {
            return total;
        }

        @Override
        public double getPrincipalUnrounded() {
            return principal;
        }

        @Override
        public double getInterestUnrounded() {
            return interest;
        }

        @Override
        public double getBalanceUnrounded() {
            return balance;
        }

        @Override
        public double getCumulativeInterestUnrounded() {
            return cumulativeInterest;
        }

        @Override
        public double[] getPmtStats() {
            return new double[] {
                    total,
                    principal,
                    extraPrincipal,
                    interest,
                    cumulativeInterest,
                    balance
            };
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("total", total)
                    .add("principal", principal)
                    .add("extraPrincipal", extraPrincipal)
                    .add("interest", interest)
                    .add("cumulativeInterest", cumulativeInterest)
                    .add("balance", balance)
                    .toString();
        }

        @Override
        public int hashCode() {
            int result = hashCode;

            if (result == 0) {
                result = Objects.hashCode(total,
                        principal,
                        extraPrincipal,
                        interest,
                        cumulativeInterest,
                        balance);
                hashCode = result;
            }

            return result;
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }

            if (!(object instanceof Payment)) {
                return false;
            }

            Payment that = (Payment) object;
            return Objects.equal(this.total, that.getTotal())
                    && Objects.equal(this.principal, that.getPrincipal())
                    && Objects.equal(this.extraPrincipal, that.getExtraPrincipal())
                    && Objects.equal(this.interest, that.getInterest())
                    && Objects.equal(this.cumulativeInterest, that.getCumulativeInterest())
                    && Objects.equal(this.balance, that.getBalance());
        }

    }

}
