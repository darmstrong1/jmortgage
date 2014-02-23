package co.da.jmtg.pmt;

import co.da.jmtg.amort.FixedAmortizationCalculator;

/**
 * A payment calculator for a fixed mortgage. An implementation of this interface calculates the interval payment of a
 * fixed mortgage given the interest rate, loan amount, and length of mortgage term in years. The interval between each
 * payment is defined by {@link PmtPeriod}.
 * 
 * @since 1.0
 * @author david
 * 
 */
public interface PmtCalculator extends Comparable<PmtCalculator> {

    /**
     * Gets the loan amount used to determine the payment.
     * 
     * @return loan amount as <tt>double</tt>.
     */
    double getLoanAmt();

    /**
     * Gets the interest rate used to determine the payment.
     * 
     * @return interest rate as <tt>double</tt>.
     */
    double getInterestRate();

    /**
     * Gets the interval interest rate used to determine the payment. This value is used by implementations of
     * {@link FixedAmortizationCalculator} when building an amortization table.
     * 
     * @return interval interest rate as <tt>double</tt>.
     */
    double getPeriodInterestRate();

    /**
     * Gets the payment count used to determine the payment. For a 30 year loan with a monthly payment interval, this
     * value would be 360.
     * 
     * @return payment count as <tt>int</tt>.
     */
    int getPmtCt();

    /**
     * Gets the duration of the mortgage term in years.
     * 
     * @return years as <tt>int</tt>.
     */
    int getYears();

    /**
     * Gets the interval used to determine the payment. The interval can be weekly, biweekly, or monthly.
     * 
     * @return {@link PmtPeriod}
     */
    PmtPeriod getPmtPeriod();

    /**
     * Sets the loan amount used to determine the payment. This method encourages implementations to be immutable by
     * specifying that it returns an object that implements <tt>PmtCalculator</tt>.
     * 
     * @param loanAmt
     *            the loan amount as <tt>double</tt> to be used to calculate the payment.
     * @return new <tt>PmtCalculator</tt> instance
     */
    PmtCalculator setLoanAmt(double loanAmt);

    /**
     * Sets the interest rate used to determine the payment. This method encourages implementations to be immutable by
     * specifying that it returns an object that implements <tt>PmtCalculator</tt>.
     * 
     * @param interestRate
     *            the interest rate as <tt>double</tt> to be used to calculate the payment.
     * @return new <tt>PmtCalculator</tt> instance
     */
    PmtCalculator setInterestRate(double interestRate);

    /**
     * Sets the years used to determine the payment. This method encourages implementations to be immutable by
     * specifying that it returns an object that implements <tt>PmtCalculator</tt>.
     * 
     * @param years
     *            as <tt>int</tt> to be used to calculate the payment.
     * @return new <tt>PmtCalculator</tt> instance
     */
    PmtCalculator setYears(int years);

    /**
     * Sets the PmtPeriod used to determine the payment. This method encourages implementations to be immutable by
     * specifying that it returns an object that implements <tt>PmtCalculator</tt>.
     * 
     * @param pmtPeriod
     *            {@link PmtPeriod} object to be used to calculate the payment.
     * @return new <tt>PmtCalculator</tt> instance
     */
    PmtCalculator setPmtPeriod(PmtPeriod pmtPeriod);

    /**
     * Gets the payment. Implementations of this method should return the payment amount rounded.
     * 
     * @return payment rounded as <tt>double</tt>.
     */
    double getPmt();

    /**
     * Gets the unrounded payment. This method is used by implementations of <tt>FixedAmortizationBuilder</tt> when
     * building an amortization schedule.
     * 
     * @return payment as <tt>double</tt>.
     */
    double getPmtUnrounded();

}
