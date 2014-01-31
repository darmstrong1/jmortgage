package co.da.jmtg.pmt;

public class PmtCalculators {
    // Suppresses default constructor, ensuring non-instantiability.
    private PmtCalculators() {
    }

    public static PmtCalculator getDefaultPmtCalculator(PmtPeriod pmtPeriod, double loanAmt, double interestRate,
            int years) {
        return DefaultPmtCalculator.getInstance(pmtPeriod, loanAmt, interestRate, years);
    }

    public static PmtCalculator getCanadianPmtCalculator(PmtPeriod pmtPeriod, double loanAmt, double interestRate,
            int years) {
        return CanadianPmtCalculator.getInstance(pmtPeriod, loanAmt, interestRate, years);
    }

}
