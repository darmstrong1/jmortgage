package co.da.jmtg.pmt;

/**
 * Contains static classes pertaining to instances of <tt>PmtCalculator</tt>
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
     * @return <tt>DefaultPmtCalculator</tt> instance of <tt>PmtCalculator</tt>
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
     * @return <tt>CanadianPmtCalculator</tt> instance of <tt>PmtCalculator</tt>
     */
    public static PmtCalculator getCanadianPmtCalculator(PmtPeriod pmtPeriod, double loanAmt, double interestRate,
            int years) {
        return CanadianPmtCalculator.getInstance(pmtPeriod, loanAmt, interestRate, years);
    }

}
