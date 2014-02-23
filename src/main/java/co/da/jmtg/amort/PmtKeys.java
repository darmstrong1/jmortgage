package co.da.jmtg.amort;

import org.joda.time.LocalDate;

import co.da.jmtg.pmt.PmtPeriod;

import com.google.common.base.Preconditions;

/**
 * <p>
 * Contains static classes pertaining to instances of <tt>PmtKey</tt>.
 * </p>
 * 
 * <p>
 * The getDefaultPmtKey methods return an instance of DefaultPmtKey. These objects use instance control. This means
 * there will never be two DefaultPmtKey objects that are equal. Therefore, to compare two of these objects, it is safe
 * to always use == instead of equals().
 * </p>
 * 
 * @since 1.0
 * 
 * @author David Armstrong
 * 
 */
public class PmtKeys {
    // Suppresses default constructor, ensuring non-instantiability.
    private PmtKeys() {
    }

    /**
     * Create a DefaultPmtKey object.
     * 
     * @param pmtPeriod
     *            The payment period for the PmtKey
     * @param firstPmtDt
     *            The first payment date
     * @param count
     *            The count of payments
     * @return DefaultPmtKey
     */
    public static PmtKey getDefaultPmtKey(PmtPeriod pmtPeriod, LocalDate firstPmtDt, int count) {
        return DefaultPmtKey.getInstance(pmtPeriod, firstPmtDt, count);
    }

    /**
     * Create a DefaultPmtKey object. Since no count is passed in, it defaults to one. This method is best used for a
     * PmtKey for extra payments.
     * 
     * @param pmtPeriod
     *            The payment period for the PmtKey
     * @param firstPmtDt
     *            The first payment date
     * @return DefaultPmtKey
     */
    public static PmtKey getDefaultPmtKey(PmtPeriod pmtPeriod, LocalDate firstPmtDt) {
        return DefaultPmtKey.getInstance(pmtPeriod, firstPmtDt);
    }

    /**
     * Create a DefaultPmtKey object. Since no date is passed in, the payment date defaults to the current date. Since
     * no count is passed in, it defaults to one. This method is best used for a PmtKey for extra payments.
     * 
     * @param pmtPeriod
     *            The payment period for the PmtKey
     * @return DefaultPmtKey
     */
    public static PmtKey getDefaultPmtKey(PmtPeriod pmtPeriod) {
        return DefaultPmtKey.getInstance(pmtPeriod, new LocalDate());
    }

    /**
     * Create a DefaultPmtKey object. Since no date is passed in, the payment date defaults to the current date.
     * 
     * @param pmtPeriod
     *            The payment period for the PmtKey
     * @param count
     *            The count of payments
     * @return DefaultPmtKey
     */
    public static PmtKey getDefaultPmtKey(PmtPeriod pmtPeriod, int count) {
        return DefaultPmtKey.getInstance(pmtPeriod, new LocalDate(), count);
    }

    /**
     * Create a DefaultPmtKey object. It calculates the count of payments on the years, which are passed in, and the
     * value of PmtPeriod.
     * 
     * @param pmtPeriod
     *            The payment period for the PmtKey
     * @param firstPmtDt
     *            The first payment date
     * @param years
     *            The number of years for the mortgage
     * @return DefaultPmtKey
     */
    public static PmtKey getDefaultPmtKeyForYears(PmtPeriod pmtPeriod, LocalDate firstPmtDt, int years) {
        return DefaultPmtKey.getInstance(pmtPeriod, firstPmtDt, calcCountFromYears(pmtPeriod, years));
    }

    /**
     * Create a DefaultPmtKey object. It calculates the count of payments on the years, which are passed in, and the
     * value of PmtPeriod. Since no date is passed in, the payment date defaults to the current date.
     * 
     * @param pmtPeriod
     *            The payment period for the PmtKey
     * @param years
     *            The number of years for the mortgage
     * @return DefaultPmtKey
     */
    public static PmtKey getDefaultPmtKeyForYears(PmtPeriod pmtPeriod, int years) {
        return DefaultPmtKey.getInstance(pmtPeriod, new LocalDate(), calcCountFromYears(pmtPeriod, years));
    }

    /**
     * Calculate the first payment date from the mortgage start date.The first payment date is determined based on the
     * value of the PmtPeriod object.
     * 
     * @param pmtPeriod
     *            The payment period for the PmtKey
     * @param mortgageStartDt
     *            The number of years for the mortgage
     * @return DefaultPmtKey
     */
    public static LocalDate calcFirstPmtDueDt(PmtPeriod pmtPeriod, LocalDate mortgageStartDt) {
        Preconditions.checkNotNull(pmtPeriod, "pmtPeriod must not be null.");
        Preconditions.checkNotNull(mortgageStartDt, "mortgageStartDt must not be null.");
        // The first payment is due after the start date. Get the period from pmtPeriod to determine when the first
        // payment is due.
        return mortgageStartDt.plus(pmtPeriod.period());
    }

    private static int calcCountFromYears(PmtPeriod pmtPeriod, int years) {
        Preconditions.checkNotNull(pmtPeriod, "pmtPeriod must not be null.");
        int count;
        switch (pmtPeriod) {
        case YEARLY:
            count = years;
            break;
        case MONTHLY:
            count = years * 12;
            break;

        case WEEKLY:
        case RAPID_WEEKLY:
            count = years * 52;
            break;

        case BIWEEKLY:
        case RAPID_BIWEEKLY:
            count = years * 26;
            break;

        default: // Must be ONETIME
            count = 1;
            break;
        }
        return count;
    }
}
