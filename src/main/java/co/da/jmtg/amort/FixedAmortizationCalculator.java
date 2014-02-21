package co.da.jmtg.amort;

import java.util.Map;
import java.util.SortedMap;

import org.joda.time.LocalDate;

import co.da.jmtg.pmt.PmtCalculator;
import co.da.jmtg.pmt.extra.ExtraPmt;

/**
 * An amortization table builder for a fixed mortgage. An implementation of this builds a {@link SortedMap} that holds
 * the contents of an amortization schedule. The map uses a Long object that represents the date of the payment as the
 * key and an object that implements the {@link Payment} interface as the value.
 * 
 * @since 1.0
 * @author David Armstrong
 * 
 */
public interface FixedAmortizationCalculator {

    /**
     * Gets the implementation of <tt>PmtCalculator</tt> used to build the amortization table.
     * 
     * @return <tt>PmtCalculator</tt> instantiation
     */
    PmtCalculator getPmtCalculator();

    /**
     * Gets the implementation of <tt>PmtKey</tt> used to build the amortization table.
     * 
     * @return <tt>PmtKey</tt> instantiation
     */
    PmtKey getPmtKey();

    /**
     * Sets the implementation of <tt>PmtCalculator</tt> to use to build the amortization table. This method encourages
     * immutability by returning an object that instantiates <tt>FixedAmortizationBuilder</tt>
     * 
     * @param pmtCalc
     *            The <tt>PmtCalculator</tt> instantiation to use to build the amortization table.
     * 
     * @return <tt>FixedAmortizationBuilder</tt> instantiation.
     */
    FixedAmortizationCalculator setPmtCalculator(PmtCalculator pmtCalc);

    /**
     * Sets the implementation of <tt>PmtKey</tt> to use to build the amortization table. This method encourages
     * immutability by returning an object that instantiates <tt>FixedAmortizationBuilder</tt>
     * 
     * @param pmtKey
     *            The <tt>PmtKey</tt> instantiation to use to build the amortization table.
     * @return <tt>FixedAmortizationBuilder</tt> instantiation.
     */
    FixedAmortizationCalculator setPmtKey(PmtKey pmtKey);

    /**
     * Returns a new FixedAmortization instance with the extra payment(s) represented by the ExtraPmt object passed in.
     * If any payment installments of the original instance already had an extra payment, this method overwrites that
     * value.
     * 
     * @param extraPmts
     *            the extra payments that will be set in the new FixedAmortizationCalculator object.
     * 
     * @throws NullPointerException
     *             if extraPmts is null.
     * 
     * @throws IllegalArgumentException
     *             if any payments in extraPmts have a date that is not a valid date for this mortgage
     * 
     * @return new FixedAmortizationCalculator instance
     */
    FixedAmortizationCalculator setExtraPayment(ExtraPmt extraPmts);

    /**
     * Returns a new FixedAmortization instance with the extra payments represented by the Iterable of ExtraPmt objects
     * passed in. If any payment installments of the original instance already had an extra payment, this method
     * overwrites that value.
     * 
     * @param extraPmts
     *            the extra payments that will be set in the new FixedAmortizationCalculator object.
     * 
     * @throws NullPointerException
     *             if extraPmts is null
     * 
     * @throws IllegalArgumentException
     *             if any payments in extraPmts have a date that is not a valid date for this mortgage
     * 
     * @return new FixedAmortizationCalculator instance
     */
    FixedAmortizationCalculator setExtraPayments(Iterable<ExtraPmt> extraPmts);

    /**
     * Returns a new FixedAmortization instance with the extra payments represented by the Map of extra payments passed
     * in. The extra payments are a map of doubles with the date of the extra payment as the key. If any payment
     * installments of the original instance already had an extra payment, this method overwrites that value.
     * 
     * @param extraPmts
     *            the extra payments that will be set in the new FixedAmortizationCalculator object.
     * 
     * @throws NullPointerException
     *             if extraPmts is null
     * 
     * @throws IllegalArgumentException
     *             if any payments in extraPmts have a date that is not a valid date for this mortgage
     * 
     * @return new FixedAmortizationCalculator instance
     */
    FixedAmortizationCalculator setExtraPayments(Map<LocalDate, Double> extraPmts);

    /**
     * Returns a new FixedAmortization instance with the extra payment(s) represented by the ExtraPmt object passed in.
     * If any payment installments of the original instance already had an extra payment, the new instance will have the
     * sum of the two values as the extra payment amount.
     * 
     * @param extraPmts
     *            the extra payments to be added.
     * 
     * @throws NullPointerException
     *             if extraPmts is null
     * 
     * @throws IllegalArgumentException
     *             if any payments in extraPmts have a date that is not a valid date for this mortgage
     * 
     * @return new FixedAmortizationCalculator instance
     */
    FixedAmortizationCalculator addExtraPayment(ExtraPmt extraPmts);

    /**
     * Returns a new FixedAmortization instance with the extra payments represented by the Iterable of ExtraPmt objects
     * passed in. If any payment installments of the original instance already had an extra payment, the new instance
     * will have the sum of the two values as the extra payment amount.
     * 
     * @param extraPmts
     *            the extra payments to be added.
     * 
     * @throws NullPointerException
     *             if extraPmts is null
     * 
     * @throws IllegalArgumentException
     *             if any payments in extraPmts have a date that is not a valid date for this mortgage
     * 
     * @return new FixedAmortizationCalculator instance
     */
    FixedAmortizationCalculator addExtraPayments(Iterable<ExtraPmt> extraPmts);

    /**
     * Returns a new FixedAmortization instance with the extra payments represented by the Map of extra payments passed
     * in. The extra payments are a map of doubles with the date of the extra payment as the key. If any payment
     * installments of the original instance already had an extra payment, the new instance will have the sum of the two
     * values as the extra payment amount.
     * 
     * @param extraPmts
     *            the extra payments to be added.
     * 
     * @throws NullPointerException
     *             if extraPmts is null
     * 
     * @throws IllegalArgumentException
     *             if any payments in extraPmts have a date that is not a valid date for this mortgage
     * 
     * @return new FixedAmortizationCalculator instance
     */
    FixedAmortizationCalculator addExtraPayments(Map<LocalDate, Double> extraPmts);

    /**
     * Removes the extra payment from the payment installment for the key passed in.
     * 
     * @param key
     *            the key of the extra payment to remove
     * 
     * @throws NullPointerException
     *             if key is null
     * 
     * @throws IllegalArgumentException
     *             if there are no extra payments defined for this object or if there is no extra payment for the
     *             payment represented by key
     * 
     * @return new FixedAmortizationCalculator instance
     */
    FixedAmortizationCalculator removeExtraPayment(LocalDate key);

    /**
     * Returns a new FixedAmortization instance with the extra payments represented by the Iterable of PmtKey objects
     * passed in removed.
     * 
     * @param keys
     *            the keys of the extra payments to remove
     * 
     * @throws NullPointerException
     *             if keys is null
     * 
     * @throws IllegalArgumentException
     *             if there are no extra payments defined for this object or if there is no extra payment for the
     *             payments represented by the keys
     * 
     * @return new FixedAmortizationCalculator instance
     */
    FixedAmortizationCalculator removeExtraPayments(Iterable<LocalDate> keys);

    /**
     * Returns a copy of the map of extra payments, sorted by chronological order.
     * 
     * @return sorted map of extra payments, sorted by date
     */
    SortedMap<LocalDate, Double> getExtraPayments();

    /**
     * Returns the extra payment at the payment with the key passed in.
     * 
     * @param key
     *            the key of the extra payment to get
     * 
     * @throws NullPointerException
     *             if key is null
     * 
     * @return the extra payment for the key or 0.0 if there is no extra payment for it
     */
    double getExtraPayment(LocalDate key);

    /**
     * Returns a new FixedAmortizationCalculator instance with no extra payments.
     * 
     * @throws IllegalArgumentException
     *             if there are no extra payments defined for this object
     * 
     * @return new FixedAmortizationCalculator instance
     */
    FixedAmortizationCalculator clearExtraPayments();

    /**
     * Gets the amortization table. The amortization table is represented as a <tt>SortedMap</tt> with a
     * <tt>LocalDate</tt> that represents the date the payment is due as the key and an implementation of
     * <tt>FixedAmortizationBuilder.Payment</tt> as the value.
     * 
     * @return <tt>SortedMap</tt>
     */
    SortedMap<LocalDate, Payment> getTable();

    /**
     * Interface that represents a payment for a period.
     * 
     * @author David Armstrong
     * 
     */
    public interface Payment {
        /**
         * Constant value for the position of the total amount for a payment when calling
         * DefaultFixedAmortizationBuilder.DefaultPayment's getPmtStats method.
         * 
         * @since 1.0
         */
        public static final int TOTAL = 0;

        /**
         * Constant value for the position of the principal amount for a payment when calling
         * DefaultFixedAmortizationBuilder.DefaultPayment's getPmtStats method.
         * 
         * @since 1.0
         */
        public static final int PRINCIPAL = 1;

        /**
         * Constant value for the position of the extra principal amount for a payment when calling
         * DefaultFixedAmortizationBuilder.DefaultPayment's getPmtStats method.
         * 
         * @since 1.0
         */
        public static final int EXTRA_PRINCIPAL = 2;

        /**
         * Constant value for the position of the interest amount for a payment when calling
         * DefaultFixedAmortizationBuilder.DefaultPayment's getPmtStats method.
         * 
         * @since 1.0
         */
        public static final int INTEREST = 3;

        /**
         * Constant value for the position of the cumulative interest amount for a payment when calling
         * DefaultFixedAmortizationBuilder.DefaultPayment's getPmtStats method.
         * 
         * @since 1.0
         */
        public static final int CUMULATIVE_INTEREST = 4;

        /**
         * Constant value for the position of the balance amount for a payment when calling
         * DefaultFixedAmortizationBuilder.DefaultPayment's getPmtStats method.
         * 
         * @since 1.0
         */
        public static final int BALANCE = 5;

        /**
         * Gets the total amount paid for this payment
         * 
         * @return <tt>double</tt> The total amount of payment
         */
        public double getTotal();

        /**
         * Gets the principal amount paid for this payment
         * 
         * @return <tt>double</tt> The principal paid.
         */
        public double getPrincipal();

        /**
         * Gets the additional principal amount paid for this payment, if any
         * 
         * @return <tt>double</tt> The principal paid.
         */
        public double getExtraPrincipal();

        /**
         * Gets the interest amount paid for this payment.
         * 
         * @return <tt>double</tt> The interest paid.
         */
        public double getInterest();

        /**
         * Gets the balance due after this payment.
         * 
         * @return <tt>double</tt> The balance due.
         */
        public double getBalance();

        /**
         * Gets the cumulative interest paid after this payment
         * 
         * @return <tt>double</tt> The cumulative interest paid.
         */
        public double getCumulativeInterest();

        /**
         * Gets the total amount paid for the payment unrounded.
         * 
         * @return <tt>double</tt> the unrounded total amount paid.
         */
        public double getTotalUnrounded();

        /**
         * Gets the principal amount paid for this payment unrounded.
         * 
         * @return <tt>double</tt> The principal paid.
         */
        public double getPrincipalUnrounded();

        /**
         * Gets the interest amount paid for this payment unrounded.
         * 
         * @return <tt>double</tt> The interest paid.
         */
        public double getInterestUnrounded();

        /**
         * Gets the balance due after this payment unrounded.
         * 
         * @return <tt>double</tt> The balance due.
         */
        public double getBalanceUnrounded();

        /**
         * Gets the cumulative interest paid after this payment unrounded.
         * 
         * @return <tt>double</tt> The cumulative interest paid.
         */
        public double getCumulativeInterestUnrounded();

        /**
         * Gets all the stats for the payment in an array of doubles
         * 
         * @return <tt>double</tt> Array of payment stats
         * @since 1.0
         */
        public double[] getPmtStats();

    }

}
