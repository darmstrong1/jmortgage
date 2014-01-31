package co.da.jmtg.amort;

import org.joda.time.LocalDate;

import co.da.jmtg.pmt.PmtPeriod;

import com.google.common.base.Preconditions;

public class PmtKeys {
    // Suppresses default constructor, ensuring non-instantiability.
    private PmtKeys() {
    }

    public static PmtKey getDefaultPmtKey(PmtPeriod pmtPeriod, LocalDate mortgageStartDt, int count) {
        return DefaultPmtKey.getInstance(pmtPeriod, calcFirstPmtDueDt(pmtPeriod, mortgageStartDt), count);
    }

    public static PmtKey getDefaultPmtKey(PmtPeriod pmtPeriod, LocalDate mortgageStartDt) {
        return DefaultPmtKey.getInstance(pmtPeriod, calcFirstPmtDueDt(pmtPeriod, mortgageStartDt));
    }

    public static PmtKey getDefaultPmtKey(PmtPeriod pmtPeriod) {
        return DefaultPmtKey.getInstance(pmtPeriod, calcFirstPmtDueDt(pmtPeriod, new LocalDate()));
    }

    public static PmtKey getDefaultPmtKey(PmtPeriod pmtPeriod, int count) {
        return DefaultPmtKey.getInstance(pmtPeriod, calcFirstPmtDueDt(pmtPeriod, new LocalDate()), count);
    }

    public static PmtKey getDefaultPmtKeyForYears(PmtPeriod pmtPeriod, LocalDate mortgageStartDt, int years) {
        return DefaultPmtKey.getInstance(pmtPeriod, calcFirstPmtDueDt(pmtPeriod, mortgageStartDt),
                calcCountFromYears(pmtPeriod, years));
    }

    public static PmtKey getDefaultPmtKeyForYears(PmtPeriod pmtPeriod, int years) {
        return DefaultPmtKey.getInstance(pmtPeriod, calcFirstPmtDueDt(pmtPeriod, new LocalDate()),
                calcCountFromYears(pmtPeriod, years));
    }

    private static LocalDate calcFirstPmtDueDt(PmtPeriod pmtPeriod, LocalDate mortgageStartDt) {
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
