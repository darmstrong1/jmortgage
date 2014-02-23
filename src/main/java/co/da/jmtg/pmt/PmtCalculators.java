package co.da.jmtg.pmt;

/**
 * <p>
 * Contains static classes pertaining to instances of <tt>PmtCalculator</tt>.
 * </p>
 * 
 * <p>
 * The getDefaultPmtCalculator method returns an instance of DefaultPmtCalculator. These objects use instance control.
 * This means there will never be two DefaultPmtCalculator objects that are equal. Therefore, to compare two of these
 * objects, it is safe to always use == instead of equals().
 * </p>
 * 
 * <p>
 * The getCanadianPmtCalculator method returns an instance of CanadianPmtCalculator. These objects use instance control.
 * This means there will never be two CanadianPmtCalculator objects that are equal. Therefore, to compare two of these
 * objects, it is safe to always use == instead of equals().
 * </p>
 * 
 * @since 1.0
 * @author David Armstrong
 * 
 */
public class PmtCalculators {
    // Suppresses default constructor, ensuring non-instantiability.
    private PmtCalculators() {
    }

    /**
     * Get an instance of <tt>DefaultPmtCalculator</tt>
     * 
     * @param pmtPeriod
     *            Payment period of the mortgage
     * @param loanAmt
     *            Amount of the loan
     * @param interestRate
     *            interest rate
     * @param years
     *            number of years to pay mortgage
     * 
     * @return <tt>DefaultPmtCalculator</tt> instance of <tt>PmtCalculator</tt>
     * 
     * @throws NullPointerException
     *             if pmtPeriod is null
     * 
     * @throws IllegalArgumentException
     *             if loanAmt is not greater than 0, interest rate is not between 0 and 100, or years is not greater
     *             than 0.
     */
    public static PmtCalculator getDefaultPmtCalculator(PmtPeriod pmtPeriod, double loanAmt, double interestRate,
            int years) {
        return DefaultPmtCalculator.getInstance(pmtPeriod, loanAmt, interestRate, years);
    }

    /**
     * Get an instance of <tt>CanadianPmtCalculator</tt>
     * 
     * @param pmtPeriod
     *            Payment period of the mortgage
     * @param loanAmt
     *            Amount of the loan
     * @param interestRate
     *            interest rate
     * @param years
     *            number of years to pay mortgage
     * 
     * @return <tt>CanadianPmtCalculator</tt> instance of <tt>PmtCalculator</tt>
     * 
     * @throws NullPointerException
     *             if pmtPeriod is null
     * 
     * @throws IllegalArgumentException
     *             if loanAmt is not greater than 0, interest rate is not between 0 and 100, or years is not greater
     *             than 0.
     */
    public static PmtCalculator getCanadianPmtCalculator(PmtPeriod pmtPeriod, double loanAmt, double interestRate,
            int years) {
        return CanadianPmtCalculator.getInstance(pmtPeriod, loanAmt, interestRate, years);
    }

}
