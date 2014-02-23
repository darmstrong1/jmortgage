package co.da.jmtg.pmt.extra;

import co.da.jmtg.amort.PmtKey;

/**
 * Implementations of this interface encapsulate extra payments. Extra payment objects contain the amount of the extra
 * payment, the number of payments to which the extra payment will be applied, and a {@link PmtKey} object. The PmtKey
 * object indicates the start date of the extra payment. This start date must match exactly with the date of one of the
 * regular mortgage payments. The PmtKey also indicates the interval of the extra payment. Valid pmtPeriod values are
 * ONETIME, YEARLY, MONTHLY, BIWEEKLY, RAPID_BIWEEKLY, WEEKLY, and RAPID_WEEKLY.
 * 
 * @since 1.0
 * @author David Armstrong
 */

public interface ExtraPmt extends Comparable<ExtraPmt> {

    /**
     * Gets the <tt>PmtKey</tt> object for this extra payment. The <tt>PmtKey</tt> object indicates the start date of
     * the extra payment, the interval between extra payments and the number of consecutive payments to which to apply
     * the extra payment.
     */
    PmtKey getPmtKey();

    /**
     * Gets the amount of the extra payment.
     * 
     * @return <tt>double</tt>
     */
    double getAmount();

    /**
     * Sets the <tt>PmtKey</tt> instance. This method encourages immutability by returning an instance of
     * <tt>ExtraPmt</tt>. The object returned will use the specified <tt>ConsecutivePmtKey</tt> instance.
     * 
     * @param pmtKey
     *            <tt>PmtKey</tt> instance
     * @return <tt>ExtraPmt</tt> instantiation
     */
    ExtraPmt setPmtKey(PmtKey pmtKey);

    /**
     * Sets the amount. This method encourages immutability by returning an instance of <tt>ExtraPmt</tt>. The object
     * returned will use the specified amount and will have the same <tt>ExtraPmt</tt> instance and count values as the
     * object on which <tt>setAmount</tt> was called.
     * 
     * @param amount
     *            The amount value
     * @return <tt>ExtraPmt</tt> instantiation
     */
    public ExtraPmt setAmount(double amount);

}
