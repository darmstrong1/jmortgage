package co.da.jmtg.pmt;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

class CanadianPmtCalculator implements PmtCalculator {

    private final double loanAmt;
    private final double interestRate;
    private final double intervalInterestRate;
    private final int years;
    private final int pmtCt;
    private final PmtPeriod pmtPeriod;
    private final double pmt;
    private final double pmtUnrounded;

    private volatile int hashCode;

    // Cache all instances of CanadianPmtCalculator. This will guarantee that only unique CanadianPmtCalculator objects
    // will exist. It means clients can use == to compare for equality.
    private static final Interner<PmtCalculator> interner = Interners.newStrongInterner();

    private CanadianPmtCalculator(PmtPeriod pmtPeriod, double loanAmt, double interestRate, int years) {

        Preconditions.checkNotNull(pmtPeriod, "pmtPeriod must not be null");
        Preconditions.checkArgument(loanAmt > 0.0, "Loan Amount must be greater than 0");
        Preconditions.checkArgument(interestRate >= 0.0 && interestRate <= 100,
                "Interest Rate must be between 0 and 100.");
        Preconditions.checkArgument(years > 0, "Mortgage term in years must be greater than 0.");

        boolean validPeriod;
        switch (pmtPeriod) {
        case BIWEEKLY:
        case MONTHLY:
        case RAPID_BIWEEKLY:
        case RAPID_WEEKLY:
        case WEEKLY:
            validPeriod = true;
            break;

        default:
            validPeriod = false;
            break;
        }
        Preconditions.checkArgument(validPeriod, "Valid PmtPeriod values are BIWEEKLY, MONTHLY, RAPID_BIWEEKLY, "
                + "RAPID_WEEKLY, or WEEKLY");

        this.loanAmt = loanAmt;
        this.interestRate = interestRate;
        this.intervalInterestRate = Math.pow(1 + (this.interestRate / 100) / 2,
                (double) 2 / (double) pmtPeriod.pmtsPerYear()) - 1;
        this.pmtPeriod = pmtPeriod;
        this.years = years;
        pmtCt = years * pmtPeriod.pmtsPerYear();

        pmtUnrounded = calcPmtUnrounded();
        pmt = calcPmt(pmtUnrounded);
    }

    public static PmtCalculator getInstance(PmtPeriod pmtPeriod, double loanAmt, double interestRate, int years) {
        return interner.intern(new CanadianPmtCalculator(pmtPeriod, loanAmt, interestRate, years));
    }

    @Override
    public double getLoanAmt() {
        return loanAmt;
    }

    @Override
    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public double getPeriodInterestRate() {
        return intervalInterestRate;
    }

    @Override
    public int getPmtCt() {
        return pmtCt;
    }

    @Override
    public int getYears() {
        return years;
    }

    @Override
    public PmtPeriod getPmtPeriod() {
        return pmtPeriod;
    }

    @Override
    public PmtCalculator setLoanAmt(double loanAmt) {
        return getInstance(pmtPeriod, loanAmt, interestRate, years);
    }

    @Override
    public PmtCalculator setInterestRate(double interestRate) {
        return getInstance(pmtPeriod, loanAmt, interestRate, years);
    }

    @Override
    public PmtCalculator setYears(int years) {
        return getInstance(pmtPeriod, loanAmt, interestRate, years);
    }

    @Override
    public PmtCalculator setPmtPeriod(PmtPeriod pmtPeriod) {
        return getInstance(pmtPeriod, loanAmt, interestRate, years);
    }

    @Override
    public double getPmt() {
        return pmt;
    }

    @Override
    public double getPmtUnrounded() {
        return pmtUnrounded;
    }

    private double calcPmt(double pmtUnrounded) {
        return new BigDecimal(pmtUnrounded).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    private double calcPmtUnrounded() {
        double semiAnnual = (double) 1 / (double) 6;
        double divInterest = interestRate / (double) 200;
        double mthlyInterestRt = Math.pow(1 + (this.interestRate / 100) / 2, (double) 2 / (double) 12) - 1;

        double pmt = loanAmt
                * (mthlyInterestRt / (1 - (Math.pow(Math.pow(1 + divInterest, semiAnnual), -(years * 12)))));

        // Now, see if the PmtPeriod is weekly, rapid weekly, biweekly, or rapid biweekly. If weekly divide payment by
        // 4. If biweekly, divide payment by 2.
        switch (pmtPeriod) {
        case BIWEEKLY:
        case WEEKLY:
            // First, round off the payment.
            // pmt = BigDecimal.valueOf(pmt).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            // Get the annual payment by multiplying by 12, then divide by the number of payments in the year.
            pmt = (pmt * 12) / pmtPeriod.pmtsPerYear();
            break;

        case RAPID_BIWEEKLY:
            pmt /= 2;
            break;

        case RAPID_WEEKLY: // RAPID_WEEKLY
            pmt /= 4;
            break;

        default: // must be MONTHLY. Do nothing.
            break;
        }

        return pmt;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("loanAmt", loanAmt)
                .add("interestRate", interestRate)
                .add("intervalInterestRate", intervalInterestRate)
                .add("years", years)
                .add("pmtCt", pmtCt)
                .add("pmtPeriod", pmtPeriod)
                .toString();
    }

    @Override
    public int hashCode() {
        int result = hashCode;

        if (result == 0) {
            result = Objects.hashCode(loanAmt,
                    interestRate,
                    intervalInterestRate,
                    years,
                    pmtCt,
                    pmtPeriod);
            hashCode = result;
        }

        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof CanadianPmtCalculator)) {
            return false;
        }

        CanadianPmtCalculator that = (CanadianPmtCalculator) object;
        return Objects.equal(this.loanAmt, that.loanAmt)
                && Objects.equal(this.interestRate, that.interestRate)
                && Objects.equal(this.intervalInterestRate, that.intervalInterestRate)
                && Objects.equal(this.years, that.years)
                && Objects.equal(this.pmtCt, that.pmtCt)
                && Objects.equal(this.pmtPeriod, that.pmtPeriod);
    }

}
