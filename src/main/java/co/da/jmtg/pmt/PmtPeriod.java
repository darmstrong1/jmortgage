package co.da.jmtg.pmt;

import org.joda.time.Period;

import co.da.jmtg.amort.PmtKey;

/**
 * Enum that represents the amount of time between payments. This enum is used when building an implementation of
 * {@link PmtCalculator} to calculate the interval payment for a mortgage term. It is used when building an
 * implementation of {@link PmtKey} to determine how far to increment subsequent payment keys.
 * 
 * @since 1.0
 * @author David Armstrong
 * 
 */
public enum PmtPeriod {

    /**
     * Weekly Period
     */
    WEEKLY(Period.weeks(1), 52),

    /**
     * Rapid Weekly Period
     */
    RAPID_WEEKLY(Period.weeks(1), 52),

    /**
     * Bi-weekly Period
     */
    BIWEEKLY(Period.weeks(2), 26),

    /**
     * Rapid bi-weekly period
     */
    RAPID_BIWEEKLY(Period.weeks(2), 26),

    /**
     * Monthly Period
     */
    MONTHLY(Period.months(1), 12),

    /**
     * Yearly Period. This is a valid period for extra payments only.
     */
    YEARLY(Period.years(1), 1),

    /**
     * Yearly Period for a mortgage that is paid weekly. This is valid for extra payments only.
     */
    YEARLY_FOR_WEEKLY(Period.weeks(52), 1),

    /**
     * Yearly Period for a mortgage that is paid biweekly. This is valid for extra payments only.
     */
    YEARLY_FOR_BIWEEKLY(Period.weeks(26), 1),

    /**
     * Onetime. This is a valid period for extra payments only.
     */
    ONETIME(null, 0); // Set the period to null. It will cause localDate.plus to return itself.

    private final Period period;
    private final int pmtsPerYear;

    PmtPeriod(Period period, int pmtsPerYear) {
        this.period = period;
        this.pmtsPerYear = pmtsPerYear;
    }

    public Period period() {
        return period;
    }

    public int pmtsPerYear() {
        return pmtsPerYear;
    }

}
