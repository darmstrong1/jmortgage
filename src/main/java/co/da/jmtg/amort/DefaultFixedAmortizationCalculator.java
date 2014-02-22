package co.da.jmtg.amort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.joda.time.LocalDate;

import co.da.jmtg.pmt.PmtCalculator;
import co.da.jmtg.pmt.PmtPeriod;
import co.da.jmtg.pmt.extra.ExtraPmt;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Ordering;

/**
 * The default implementation of <tt>FixedAmortizationCalculator</tt>. It is for a US based mortgage.
 * 
 * @since 1.0
 * @author David Armstrong
 * 
 */
class DefaultFixedAmortizationCalculator implements FixedAmortizationCalculator {

    private final PmtCalculator pmtCalculator;
    private final PmtKey pmtKey;
    // Get a local copy of intervalInterestRate, mthlyPmt, and interval from the PmtCalculator object because they will
    // be used so much when building the amortization table.
    private final double periodInterestRate;
    private final double pmt;

    // Store extra payments in this Map.
    private final Map<LocalDate, Double> extraPmtMap;

    // This stores the result and is calculated at the end of the constructor.
    private final SortedMap<LocalDate, Payment> amortizationMap;

    private volatile int hashCode;

    private static final Interner<FixedAmortizationCalculator> interner = Interners.newStrongInterner();

    /*
     * 
     * @param pmtCalculator
     * 
     * @param pmtKey
     * 
     * @throws NullPointerException if pmtCalculator or pmtKey is null.
     * 
     * @throws IllegalArgumentException if PmtPeriod in PmtCalculator is not BIWEEKLY, RAPID_BIWEEKLY, MONTHLY, WEEKLY,
     * or RAPID_WEEKLY.
     */
    private DefaultFixedAmortizationCalculator(PmtCalculator pmtCalculator, PmtKey pmtKey) {
        checkPreconditions(pmtCalculator, pmtKey);

        this.pmtCalculator = pmtCalculator;
        periodInterestRate = pmtCalculator.getPeriodInterestRate();
        pmt = pmtCalculator.getPmtUnrounded(); // MUST get the unrounded payment amount for accuracy.

        this.pmtKey = pmtKey;
        // No extra payments, so set it to an empty map.
        extraPmtMap = ImmutableMap.of();

        amortizationMap = buildTable();
    }

    /*
     * Builds a FixedAmortizationCalculator with extra payments. It is the responsibility of the developer to ensure
     * that all dates in the ExtraPmt object are valid dates for this mortgage.
     * 
     * @param pmtCalculator
     * 
     * @param pmtKey
     * 
     * @param extraPmts
     * 
     * @throws NullPointerException if pmtCalculator, pmtKey, or extraPmts is null.
     * 
     * @throws IllegalArgumentException if PmtPeriod in PmtCalculator is not BIWEEKLY, RAPID_BIWEEKLY, MONTHLY, WEEKLY,
     * or RAPID_WEEKLY.
     * 
     * @throws IllegalArgumentException if extraPmts contains dates that are not valid payment dates for the mortgage
     * this object represents.
     */
    private DefaultFixedAmortizationCalculator(PmtCalculator pmtCalculator, PmtKey pmtKey, ExtraPmt extraPmts) {
        checkPreconditions(pmtCalculator, pmtKey, extraPmts);

        this.pmtCalculator = pmtCalculator;
        periodInterestRate = pmtCalculator.getPeriodInterestRate();
        pmt = pmtCalculator.getPmtUnrounded(); // MUST get the unrounded payment amount for accuracy.

        this.pmtKey = pmtKey;
        extraPmtMap = initializeExtraPmts(extraPmts);

        amortizationMap = buildTable(extraPmtMap);
    }

    /*
     * Builds a FixedAmortizationCalculator with extra payments. It is the responsibility of the developer to ensure
     * that all dates in the ExtraPmt object are valid dates for this mortgage.
     * 
     * @param pmtCalculator
     * 
     * @param pmtKey
     * 
     * @param extraPmts
     * 
     * @throws NullPointerException if pmtCalculator, pmtKey, or extraPmts is null.
     * 
     * @throws IllegalArgumentException if PmtPeriod in PmtCalculator is not BIWEEKLY, RAPID_BIWEEKLY, MONTHLY, WEEKLY,
     * or RAPID_WEEKLY.
     * 
     * @throws IllegalArgumentException if extraPmts contains dates that are not valid payment dates for the mortgage
     * this object represents.
     * 
     * @throws IllegalArgumentException if any of the ExtraPmt objects have duplicate date keys.
     */
    private DefaultFixedAmortizationCalculator(PmtCalculator pmtCalculator, PmtKey pmtKey, Iterable<ExtraPmt> extraPmts) {
        checkPreconditions(pmtCalculator, pmtKey, extraPmts);

        this.pmtCalculator = pmtCalculator;
        periodInterestRate = pmtCalculator.getPeriodInterestRate();
        pmt = pmtCalculator.getPmtUnrounded(); // MUST get the unrounded payment amount for accuracy.

        this.pmtKey = pmtKey;
        extraPmtMap = initializeExtraPmts(extraPmts);

        amortizationMap = buildTable(extraPmtMap);
    }

    /*
     * Builds a FixedAmortizationCalculator with extra payments. It is the responsibility of the developer to ensure
     * that all dates in the ExtraPmt object are valid dates for this mortgage.
     * 
     * @param pmtCalculator
     * 
     * @param pmtKey
     * 
     * @param extraPmts
     * 
     * @throws NullPointerException if pmtCalculator, pmtKey, or extraPmts is null.
     * 
     * @throws IllegalArgumentException if PmtPeriod in PmtCalculator is not BIWEEKLY, RAPID_BIWEEKLY, MONTHLY, WEEKLY,
     * or RAPID_WEEKLY.
     * 
     * @throws IllegalArgumentException if extraPmts contains dates that are not valid payment dates for the mortgage
     * this object represents.
     * 
     * @throws IllegalArgumentException if any of the ExtraPmt objects have duplicate date keys.
     */
    private DefaultFixedAmortizationCalculator(PmtCalculator pmtCalculator, PmtKey pmtKey,
            Map<LocalDate, Double> extraPmts) {
        checkPreconditions(pmtCalculator, pmtKey, extraPmts);

        this.pmtCalculator = pmtCalculator;
        periodInterestRate = pmtCalculator.getPeriodInterestRate();
        pmt = pmtCalculator.getPmtUnrounded(); // MUST get the unrounded payment amount for accuracy.

        this.pmtKey = pmtKey;
        extraPmtMap = initializeExtraPmts(extraPmts);

        amortizationMap = buildTable(extraPmtMap);
    }

    /*
     * Checks preconditions for objects common to all the constructors. Since the constructors do not call a common
     * constructor, this is an attempt to consolidate some of the common functionality in each constructor.
     */
    private void checkPreconditions(PmtCalculator pmtCalculator, PmtKey pmtKey) {
        Preconditions.checkNotNull(pmtCalculator, "pmtCalculator must not be null.");
        Preconditions.checkNotNull(pmtKey, "pmtKey must not be null.");

        PmtPeriod pmtPeriod = pmtCalculator.getPmtPeriod();
        boolean validPeriod;
        switch (pmtPeriod) {
        case BIWEEKLY:
        case RAPID_BIWEEKLY:
        case MONTHLY:
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
    }

    /*
     * Checks preconditions in the constructor that takes an ExtraPmt object.
     */
    private void checkPreconditions(PmtCalculator pmtCalculator, PmtKey pmtKey, ExtraPmt extraPmts) {
        checkPreconditions(pmtCalculator, pmtKey);
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");
    }

    /*
     * Checks preconditions in the constructor that takes an Iterable<ExtraPmt> object.
     */
    private void checkPreconditions(PmtCalculator pmtCalculator, PmtKey pmtKey, Iterable<ExtraPmt> extraPmts) {
        checkPreconditions(pmtCalculator, pmtKey);
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");
    }

    /*
     * Checks preconditions in the constructor that takes an Map<LocalDate, Double> object.
     */
    private void checkPreconditions(PmtCalculator pmtCalculator, PmtKey pmtKey, Map<LocalDate, Double> extraPmts) {
        checkPreconditions(pmtCalculator, pmtKey);
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");
    }

    /*
     * Initialize extra map of extra payments from an Iterable<ExtraPmt> object.If an attempt is made to add an extra
     * payment with a date key that is not valid for the mortgage this object represents, an IllegalArgumentException is
     * thrown. If an attempt is made to add an extra payment for a duplicate date, an IllegalArgumentException is
     * thrown.
     */
    private Map<LocalDate, Double> initializeExtraPmts(Iterable<ExtraPmt> extraPmts) {
        ImmutableMap.Builder<LocalDate, Double> builder = ImmutableMap.builder();

        for (ExtraPmt extraPmt : extraPmts) {
            builder.putAll(initializeExtraPmts(extraPmt));
        }

        return builder.build();
    }

    /*
     * Initialize extra map of extra payments from an ExtraPmt object. If an attempt is made to add an extra payment
     * with a date key that is not valid for the mortgage this object represents, an IllegalArgumentException is thrown.
     * If an attempt is made to add an extra payment for a duplicate date, an IllegalArgumentException is thrown.
     */
    private Map<LocalDate, Double> initializeExtraPmts(ExtraPmt extraPmts) {
        ImmutableMap.Builder<LocalDate, Double> builder = ImmutableMap.builder();

        // The PmtPeriod for the ExtraPmt must be equal to the payment period for the mortgage,
        // or YEARLY if the mortage payment is monthly, YEARLY_FOR_WEEKLY if the mortgage payment is WEEKLY or
        // RAPID_WEEKLY, YEARLY_FOR_BIWEEKLY if the mortgage payment is BIWEEKLY or RAPID_BIWEEKLY, or ONETIME. Anything
        // else is invalid.
        PmtPeriod mortgagePeriod = pmtKey.getPmtPeriod();
        PmtPeriod extraPeriod = extraPmts.getPmtKey().getPmtPeriod();
        boolean validPeriod = isValidPmtPeriod(mortgagePeriod, extraPeriod);
        Preconditions.checkArgument(validPeriod, "Extra Payment Period " + extraPeriod
                + " is invalid for a mortgage payment period of " + mortgagePeriod + ".");

        for (LocalDate key : extraPmts.getPmtKey().getKeys()) {
            // If any key in extraPmts is not valid for this mortgage, throw an IllegalArgumentException. I aint playin.
            Preconditions.checkArgument(pmtKey.getKeys().contains(key),
                    "extraPmts contained the following payment date: " + key + ". It is not valid for this mortgage.");

            // We know this extra payment has a key that matches one of the payment installments.
            builder.put(key, extraPmts.getAmount());
        }

        return builder.build();
    }

    /*
     * Initialize extra map of extra payments from an Map<LocalDate, Double> object. If an attempt is made to add an
     * extra payment with a date key that is not valid for the mortgage this object represents, an
     * IllegalArgumentException is thrown. If an attempt is made to add an extra payment for a duplicate date, an
     * IllegalArgumentException is thrown.
     */
    private Map<LocalDate, Double> initializeExtraPmts(Map<LocalDate, Double> extraPmts) {
        ImmutableMap.Builder<LocalDate, Double> builder = ImmutableMap.builder();

        Set<LocalDate> keys = extraPmts.keySet();
        for (LocalDate key : keys) {
            // If any key in extraPmts is not valid for this mortgage, throw an IllegalArgumentException. I aint playin.
            Preconditions.checkArgument(pmtKey.getKeys().contains(key),
                    "extraPmts contained the following payment date: " + key + ". It is not valid for this mortgage.");

            builder.put(key, extraPmts.get(key));
        }

        return builder.build();
    }

    /**
     * Create an instance of FixedAmortizationCalculator with no extra payments.
     * 
     * @param pmtCalculator
     * @param pmtKey
     * @return FixedAmortizationCalculator
     * 
     * @throws NullPointerException
     *             if pmtCalculator or pmtKey is null.
     * 
     * @throws IllegalArgumentException
     *             if PmtPeriod in PmtCalculator is not BIWEEKLY, RAPID_BIWEEKLY, MONTHLY, WEEKLY, or RAPID_WEEKLY.
     */
    public static FixedAmortizationCalculator getInstance(PmtCalculator pmtCalculator, PmtKey pmtKey) {
        return interner.intern(new DefaultFixedAmortizationCalculator(pmtCalculator, pmtKey));
    }

    /**
     * Create an instance of FixedAmortizationCalculator with extra payments represented in the ExtraPmt object.
     * 
     * @param pmtCalculator
     * @param pmtKey
     * @param extraPmts
     * @return FixedAmortizationCalculator
     * 
     * @throws NullPointerException
     *             if pmtCalculator, pmtKey, or extraPmts is null.
     * 
     * @throws IllegalArgumentException
     *             if PmtPeriod in PmtCalculator is not BIWEEKLY, RAPID_BIWEEKLY, MONTHLY, WEEKLY, or RAPID_WEEKLY.
     * 
     * @throws IllegalArgumentException
     *             if extraPmts contains dates that are not valid payment dates for the mortgage this object represents.
     */
    public static FixedAmortizationCalculator getInstance(PmtCalculator pmtCalculator, PmtKey pmtKey, ExtraPmt extraPmts) {
        return interner.intern(new DefaultFixedAmortizationCalculator(pmtCalculator, pmtKey, extraPmts));
    }

    /**
     * Create an instance of FixedAmortizationCalculator with extra payments represented in the Iterable<ExtraPmt>
     * object.
     * 
     * @param pmtCalculator
     * @param pmtKey
     * @param extraPmts
     * @return FixedAmortizationCalculator
     * 
     * @throws NullPointerException
     *             if pmtCalculator, pmtKey, or extraPmts is null.
     * 
     * @throws IllegalArgumentException
     *             if PmtPeriod in PmtCalculator is not BIWEEKLY, RAPID_BIWEEKLY, MONTHLY, WEEKLY, or RAPID_WEEKLY.
     * 
     * @throws IllegalArgumentException
     *             if extraPmts contains dates that are not valid payment dates for the mortgage this object represents.
     */
    public static FixedAmortizationCalculator getInstance(PmtCalculator pmtCalculator, PmtKey pmtKey,
            Iterable<ExtraPmt> extraPmts) {
        return interner.intern(new DefaultFixedAmortizationCalculator(pmtCalculator, pmtKey, extraPmts));
    }

    /**
     * Create an instance of FixedAmortizationCalculator with extra payments represented in the Map<LocalDate, Double>
     * object.
     * 
     * @param pmtCalculator
     * @param pmtKey
     * @param extraPmts
     * @return FixedAmortizationCalculator
     * 
     * @throws NullPointerException
     *             if pmtCalculator, pmtKey, or extraPmts is null.
     * 
     * @throws IllegalArgumentException
     *             if PmtPeriod in PmtCalculator is not BIWEEKLY, RAPID_BIWEEKLY, MONTHLY, WEEKLY, or RAPID_WEEKLY.
     * 
     * @throws IllegalArgumentException
     *             if extraPmts contains dates that are not valid payment dates for the mortgage this object represents.
     */
    public static FixedAmortizationCalculator getInstance(PmtCalculator pmtCalculator, PmtKey pmtKey,
            Map<LocalDate, Double> extraPmts) {
        return interner.intern(new DefaultFixedAmortizationCalculator(pmtCalculator, pmtKey, extraPmts));
    }

    @Override
    public PmtCalculator getPmtCalculator() {
        return pmtCalculator;
    }

    @Override
    public PmtKey getPmtKey() {
        return pmtKey;
    }

    @Override
    public FixedAmortizationCalculator setPmtCalculator(PmtCalculator pmtCalc) {
        return getInstance(pmtCalc, pmtKey);
    }

    @Override
    public FixedAmortizationCalculator setPmtKey(PmtKey pmtKey) {
        Preconditions.checkNotNull(pmtKey, "pmtKey must not be null.");
        return getInstance(pmtCalculator, pmtKey);
    }

    /*
     * Ensures that the payment period is one of: BIWEEKLY, RAPID_BIWEEKLY, MONTHLY, WEEKLY, or RAPID_WEEKLY.
     */
    private boolean isValidPmtPeriod(PmtPeriod mortgagePeriod, PmtPeriod extraPeriod) {
        boolean validPeriod;
        switch (mortgagePeriod) {
        case MONTHLY:
            switch (extraPeriod) {
            case MONTHLY:
            case YEARLY:
            case ONETIME:
                validPeriod = true;
                break;

            default:
                validPeriod = false;
                break;
            }
            break;

        case BIWEEKLY:
        case RAPID_BIWEEKLY:
            switch (extraPeriod) {
            case BIWEEKLY:
            case RAPID_BIWEEKLY:
            case YEARLY_FOR_BIWEEKLY:
            case ONETIME:
                validPeriod = true;
                break;

            default:
                validPeriod = false;
                break;
            }
            break;

        case WEEKLY:
        case RAPID_WEEKLY:
            switch (extraPeriod) {
            case WEEKLY:
            case RAPID_WEEKLY:
            case YEARLY_FOR_WEEKLY:
            case ONETIME:
                validPeriod = true;
                break;

            default:
                validPeriod = false;
                break;
            }
            break;

        default:
            validPeriod = false;
            break;
        }

        return validPeriod;
    }

    /*
     * Takes a Map<LocalDate, Double> map with new extra payments and a Map<LocalDate, Double> map with extra payments
     * that this object has. It returns a merging of the two maps. If add is true, it adds to the value of any payment
     * that is in the existing map. If add is false, it overwrites any existing extra payment. The existing extra
     * payment map is mutable, so we can add the contents of the new map to it.
     */
    private Map<LocalDate, Double> mergeWithExistingExtraPmts(Map<LocalDate, Double> newExtraPmtMap,
            Map<LocalDate, Double> existingExtraPmtMap, boolean add) {
        if (add) {
            for (LocalDate key : newExtraPmtMap.keySet()) {
                // Get the existing value so we can add it.
                Double e = existingExtraPmtMap.get(key);
                double existing = e == null ? 0.0 : e.doubleValue();
                Double newExtra = newExtraPmtMap.get(key).doubleValue() + existing;
                existingExtraPmtMap.put(key, newExtra);
            }
        } else {
            // Add the new values from newExtraPmtMap. Any duplicate values in mutableExtraPmtMap will be overwritten,
            // which is what we want.
            existingExtraPmtMap.putAll(newExtraPmtMap);
        }

        return existingExtraPmtMap;
    }

    /*
     * Builds a Map of extra payments if there are already extra payments in extraPmtMap. This is called in the
     * setExtraPayment(ExtraPmt extraPmts) method, which returns a new FixedAmortizationCalculator object.
     */
    private Map<LocalDate, Double> buildExtraPmtFromExisting(ExtraPmt extraPmts, boolean add) {
        Map<LocalDate, Double> mutableExtraPmtMap = new HashMap<>(extraPmtMap);

        // Now, build a map with the extraPmts passed in.
        Map<LocalDate, Double> newExtraPmtMap = initializeExtraPmts(extraPmts);

        return mergeWithExistingExtraPmts(newExtraPmtMap, mutableExtraPmtMap, add);
    }

    /*
     * Builds a Map of extra payments if there are already extra payments in extraPmtMap. This is called in the
     * setExtraPayment(Iterable<ExtraPmt> extraPmts) method, which returns a new FixedAmortizationCalculator object. If
     * add is true, it will add the amount of a new extra payment to one that already exists.
     */
    private Map<LocalDate, Double> buildExtraPmtFromExisting(Iterable<ExtraPmt> extraPmts, boolean add) {
        Map<LocalDate, Double> mutableExtraPmtMap = new HashMap<>(extraPmtMap);

        // Now, build a map with the extraPmts passed in.
        Map<LocalDate, Double> newExtraPmtMap = initializeExtraPmts(extraPmts);

        return mergeWithExistingExtraPmts(newExtraPmtMap, mutableExtraPmtMap, add);
    }

    /*
     * Builds a Map of extra payments if there are already extra payments in extraPmtMap. This is called in the
     * setExtraPayment(Map<LocalDate, Double> extraPmts) method, which returns a new FixedAmortizationCalculator object.
     */
    private Map<LocalDate, Double> buildExtraPmtFromExisting(Map<LocalDate, Double> extraPmts, boolean add) {
        Map<LocalDate, Double> mutableExtraPmtMap = new HashMap<>(extraPmtMap);

        // Now, build a map with the extraPmts passed in.
        Map<LocalDate, Double> newExtraPmtMap = initializeExtraPmts(extraPmts);

        return mergeWithExistingExtraPmts(newExtraPmtMap, mutableExtraPmtMap, add);
    }

    /**
     * Returns a new FixedAmortization instance with the extra payment represented by the ExtraPmt object passed in. If
     * any payment installments of the original instance already had an extra payment, this method overwrites that
     * value.
     * 
     * @param key
     *            the key of the extra payment to set
     * 
     * @param amount
     *            the amount of the extra payment
     * 
     * @throws NullPointerException
     *             if key is null
     * 
     * @throws IllegalArgumentException
     *             if the key passed in is not a valid date for this mortgage
     * 
     * @return new FixedAmortizationCalculator instance
     */
    public FixedAmortizationCalculator setExtraPayment(LocalDate key, double amount) {
        Preconditions.checkNotNull(key, "key must not be null");

        Map<LocalDate, Double> xtra = ImmutableMap.of(key, Double.valueOf(amount));

        if (extraPmtMap.isEmpty()) {
            return getInstance(pmtCalculator, pmtKey, xtra);
        }

        return getInstance(pmtCalculator, pmtKey, buildExtraPmtFromExisting(xtra, false));
    }

    /**
     * Creates a new FixedAmortizationCalculator with the extra payments represented by the ExtraPmt object passed in.
     * If this object already had some extra payments, those that did not have the same keys as the ones passed in will
     * also be in the new object. If the extra payments in this object shared any of the keys from the extra payments
     * passed in, the new values will overwrite the old ones.
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
     * @return a new FixedAmortizationCalculator object.
     * 
     */
    @Override
    public FixedAmortizationCalculator setExtraPayment(ExtraPmt extraPmts) {
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");

        if (extraPmtMap.isEmpty()) {
            return getInstance(pmtCalculator, pmtKey, extraPmts);
        }

        return getInstance(pmtCalculator, pmtKey, buildExtraPmtFromExisting(extraPmts, false));
    }

    /**
     * Creates a new FixedAmortizationCalculator with the extra payments represented by the Iterable<ExtraPmt> object
     * passed in. If this object already had some extra payments, those that did not have the same keys as the ones
     * passed in will also be in the new object. If the extra payments in this object shared any of the keys from the
     * extra payments passed in, the new values will overwrite the old ones.
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
     * @return a new FixedAmortizationCalculator object.
     * 
     */
    @Override
    public FixedAmortizationCalculator setExtraPayments(Iterable<ExtraPmt> extraPmts) {
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");

        if (extraPmtMap.isEmpty()) {
            return getInstance(pmtCalculator, pmtKey, extraPmts);
        }

        return getInstance(pmtCalculator, pmtKey, buildExtraPmtFromExisting(extraPmts, false));
    }

    /**
     * Creates a new FixedAmortizationCalculator with the extra payments represented by the Map<LocalDate, Double>
     * object passed in. If this object already had some extra payments, those that did not have the same keys as the
     * ones passed in will also be in the new object. If the extra payments in this object shared any of the keys from
     * the extra payments passed in, the new values will overwrite the old ones.
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
     * @return a new FixedAmortizationCalculator object.
     */
    @Override
    public FixedAmortizationCalculator setExtraPayments(Map<LocalDate, Double> extraPmts) {
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");

        if (extraPmtMap.isEmpty()) {
            return getInstance(pmtCalculator, pmtKey, extraPmts);
        }

        return getInstance(pmtCalculator, pmtKey, buildExtraPmtFromExisting(extraPmts, false));
    }

    /**
     * Returns a new FixedAmortization instance with the extra payment represented by the ExtraPmt object passed in. If
     * any payment installments of the original instance already had an extra payment, this method adds to that value.
     * 
     * @param key
     *            the key of the extra payment to set
     * 
     * @param amount
     *            the amount of the extra payment
     * 
     * @throws NullPointerException
     *             if key is null
     * 
     * @throws IllegalArgumentException
     *             if the key passed in is not a valid date for this mortgage
     * 
     * @return new FixedAmortizationCalculator instance
     */
    public FixedAmortizationCalculator addExtraPayment(LocalDate key, double amount) {
        Preconditions.checkNotNull(key, "key must not be null");

        Map<LocalDate, Double> xtra = ImmutableMap.of(key, Double.valueOf(amount));

        if (extraPmtMap.isEmpty()) {
            return getInstance(pmtCalculator, pmtKey, xtra);
        }

        return getInstance(pmtCalculator, pmtKey, buildExtraPmtFromExisting(xtra, true));
    }

    /**
     * Creates a new FixedAmortizationCalculator with added extra payments. If the payment for a date in extraPmts
     * already has an extra payment, the new extra value will be added to it.
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
     * @return a new FixedAmortizationCalculator object.
     */
    @Override
    public FixedAmortizationCalculator addExtraPayment(ExtraPmt extraPmts) {
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");

        if (extraPmtMap.isEmpty()) {
            return getInstance(pmtCalculator, pmtKey, extraPmts);
        }

        return getInstance(pmtCalculator, pmtKey, buildExtraPmtFromExisting(extraPmts, true));
    }

    /**
     * Creates a new FixedAmortizationCalculator with added extra payments. If the payment for a date in extraPmts
     * already has an extra payment, the new extra value will be added to it.
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
     * @return a new FixedAmortizationCalculator object.
     */
    @Override
    public FixedAmortizationCalculator addExtraPayments(Iterable<ExtraPmt> extraPmts) {
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");

        if (extraPmtMap.isEmpty()) {
            return getInstance(pmtCalculator, pmtKey, extraPmts);
        }

        return getInstance(pmtCalculator, pmtKey, buildExtraPmtFromExisting(extraPmts, true));
    }

    /**
     * Creates a new FixedAmortizationCalculator with added extra payments. If the payment for a date in extraPmts
     * already has an extra payment, the new extra value will be added to it.
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
     * @return a new FixedAmortizationCalculator object.
     */
    @Override
    public FixedAmortizationCalculator addExtraPayments(Map<LocalDate, Double> extraPmts) {
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");

        if (extraPmtMap.isEmpty()) {
            return getInstance(pmtCalculator, pmtKey, extraPmts);
        }

        return getInstance(pmtCalculator, pmtKey, buildExtraPmtFromExisting(extraPmts, true));
    }

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
     * @return new FixedAmortizationCalculator instance
     */
    @Override
    public FixedAmortizationCalculator removeExtraPayment(LocalDate key) {
        Preconditions.checkNotNull(key, "key must not be null.");
        Preconditions.checkArgument(extraPmtMap.isEmpty() == false,
                "extraPmtMap must not be empty before calling removeExtraPayment");
        // If extraPmtMap does not contain the key we are trying to remove, throw an IllegalArgumentException. We could
        // ignore it and return the same object, but throwing an Exception sends a clear message to the caller that
        // what they were trying to do failed.
        Preconditions.checkArgument(extraPmtMap.containsKey(key), "attempt to remove extra payment with key of " + key
                + " failed because extraPmtMap does not contain an extra payment with that key.");

        // Create a mutable copy of the map and remove the extra payment from that.
        Map<LocalDate, Double> reducedMap = new HashMap<>(extraPmtMap);
        reducedMap.remove(key);

        return getInstance(pmtCalculator, pmtKey, reducedMap);
    }

    /**
     * Removes the extra payment from the payment installment for the key passed in.
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
     * @return new FixedAmortizationCalculator instance
     */
    @Override
    public FixedAmortizationCalculator removeExtraPayments(Iterable<LocalDate> keys) {
        Preconditions.checkNotNull(keys, "keys must not be null.");
        Preconditions.checkArgument(extraPmtMap.isEmpty() == false,
                "extraPmtMap must not be empty before calling removeExtraPayments");

        // Create a mutable copy of the map and remove the extra payment from that.
        Map<LocalDate, Double> reducedMap = new HashMap<>(extraPmtMap);
        for (LocalDate key : keys) {
            Preconditions.checkArgument(reducedMap.containsKey(key), "attempt to remove extra payment with key of "
                    + key
                    + " failed because extraPmtMap does not contain an extra payment with that key.");
            reducedMap.remove(key);
        }

        return getInstance(pmtCalculator, pmtKey, reducedMap);
    }

    /**
     * Clears the extra payments.
     * 
     * @throws IllegalArgumentException
     *             if there are no extra payments defined for this object
     * @return new FixedAmortizationCalculator instance
     */
    @Override
    public FixedAmortizationCalculator clearExtraPayments() {
        Preconditions.checkArgument(extraPmtMap.isEmpty() == false,
                "extraPmtMap must not be empty before calling clearExtraPayments");

        return getInstance(pmtCalculator, pmtKey);
    }

    /**
     * Returns a sorted copy of the extraPmtMap.
     */
    @Override
    public SortedMap<LocalDate, Double> getExtraPayments() {
        return ImmutableSortedMap.copyOf(extraPmtMap);
    }

    /**
     * Returns the extra payment for the key passed in or 0.0 if extraPmtMap is null or extraPmtMap does not have a
     * value for the key.
     * 
     * @param key
     *            the key of the extra payment to get
     * 
     * @throws NullPointerException
     *             if key is null
     * 
     * @return the extra payment for the key or 0.0 if there is no extra payment for it
     */
    @Override
    public double getExtraPayment(LocalDate key) {
        Preconditions.checkNotNull(key, "key must not be null.");
        Double e = extraPmtMap.get(key);
        return e == null ? 0.0 : e.doubleValue();
    }

    /**
     * Returns the amortization table as a sorted map. The keys are the date the payment is due.
     */
    @Override
    public SortedMap<LocalDate, Payment> getTable() {
        return amortizationMap;
    }

    /*
     * Builds a table with no extra payments.
     */
    private SortedMap<LocalDate, Payment> buildTable() {

        double principalOwed = pmtCalculator.getLoanAmt();
        double interestPaid = 0.0;
        ImmutableSortedMap.Builder<LocalDate, Payment> pmtMapBuilder = new ImmutableSortedMap.Builder<>(
                Ordering.natural());

        Iterator<LocalDate> keyIterator = pmtKey.getKeys().iterator();
        while ((BigDecimal.valueOf(principalOwed).setScale(2, RoundingMode.HALF_EVEN).doubleValue() > 0.0)
                && (keyIterator.hasNext())) {

            // Get the key.
            LocalDate key = keyIterator.next();

            // Create the payment object and add it with its key.
            Payment payment = new DefaultPayment(principalOwed, interestPaid);
            pmtMapBuilder.put(key, payment);

            // Update the principal owed and balance paid.
            principalOwed = payment.getBalanceUnrounded();
            interestPaid = payment.getCumulativeInterestUnrounded();
        }

        return pmtMapBuilder.build();
    }

    /*
     * Builds a table with extra payments.
     */
    private SortedMap<LocalDate, Payment> buildTable(Map<LocalDate, Double> extraPmtMap) {

        double principalOwed = pmtCalculator.getLoanAmt();
        double interestPaid = 0.0;
        ImmutableSortedMap.Builder<LocalDate, Payment> pmtMapBuilder = new ImmutableSortedMap.Builder<>(
                Ordering.natural());

        Iterator<LocalDate> keyIterator = pmtKey.getKeys().iterator();
        while ((BigDecimal.valueOf(principalOwed).setScale(2, RoundingMode.HALF_EVEN).doubleValue() > 0.0)
                && (keyIterator.hasNext())) {

            // Get the key and the extra payment. Check it for null.
            LocalDate key = keyIterator.next();
            Double extraPmt = extraPmtMap.get(key);

            // Create the payment object and add it with its key.
            Payment payment = new DefaultPayment(principalOwed, interestPaid, extraPmt == null ? 0.0
                    : extraPmt.doubleValue());
            pmtMapBuilder.put(key, payment);

            // Update the principal owed and balance paid.
            principalOwed = payment.getBalanceUnrounded();
            interestPaid = payment.getCumulativeInterestUnrounded();
        }

        return pmtMapBuilder.build();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("pmtCalculator", pmtCalculator)
                .add("pmtKey", pmtKey)
                .add("periodInterestRate", periodInterestRate)
                .add("pmt", pmt)
                .add("extraPmtMap", extraPmtMap)
                .add("amortizationMap", amortizationMap)
                .toString();
    }

    @Override
    public int hashCode() {
        int result = hashCode;

        if (result == 0) {
            result = Objects.hashCode(pmtCalculator,
                    pmtKey,
                    periodInterestRate,
                    pmt,
                    extraPmtMap,
                    amortizationMap);
            hashCode = result;
        }

        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof DefaultFixedAmortizationCalculator)) {
            return false;
        }

        DefaultFixedAmortizationCalculator that = (DefaultFixedAmortizationCalculator) object;
        return Objects.equal(this.periodInterestRate, that.periodInterestRate)
                && Objects.equal(this.pmt, that.pmt)
                && Objects.equal(this.pmtCalculator, that.pmtCalculator)
                && Objects.equal(this.pmtKey, that.pmtKey)
                && Objects.equal(this.extraPmtMap, that.extraPmtMap)
                && Objects.equal(this.amortizationMap, that.amortizationMap);
    }

    /**
     * Compare two DefaultFixedAmortizationCalculator objects.
     * 
     * @param o
     *            the object to compare
     * 
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object.
     * 
     * @throws ClassCastException
     *             if the PmtKey object passed in is not a DefaultFixedAmortizationCalculator object.
     */
    @Override
    public int compareTo(FixedAmortizationCalculator o) {
        // Since we use an Interner for this object, two objects that are equal should always be the same object, so
        // this first check should always be true if the "objects" are the same.
        if (this == o) {
            return 0;
        }

        if (!(o instanceof DefaultFixedAmortizationCalculator)) {
            throw new ClassCastException(
                    "Object to compare must be of type DefaultFixedAmortizationCalculator. Object is " + o == null ? "null"
                            : o.getClass().getName());
        }

        DefaultFixedAmortizationCalculator that = (DefaultFixedAmortizationCalculator) o;

        // The most important comparison to make between two FixedAmortizationCalculators is the total cost. This cost
        // is the total principal owed plus the total interest. Compare this value first.
        Payment thisLastPmt = amortizationMap.get(amortizationMap.lastKey());
        Payment thatLastPmt = that.amortizationMap.get(that.amortizationMap.lastKey());
        double thisTotalCost = BigDecimal.valueOf(pmtCalculator.getLoanAmt())
                .add(BigDecimal.valueOf(thisLastPmt.getCumulativeInterest())).doubleValue();
        double thatTotalCost = BigDecimal.valueOf(that.pmtCalculator.getLoanAmt())
                .add(BigDecimal.valueOf(thatLastPmt.getCumulativeInterest())).doubleValue();

        int result = Double.compare(thisTotalCost, thatTotalCost);
        if (result != 0) {
            return result;
        }

        // The total costs are the same, so compare starting with the pmtCalculator.
        result = ComparisonChain.start()
                .compare(pmtCalculator, that.pmtCalculator)
                .compare(periodInterestRate, that.periodInterestRate)
                .compare(pmt, that.pmt)
                .compare(pmtKey, that.pmtKey)
                .result();

        if (result != 0) {
            return result;
        }

        // If two DefaultFixedAmortizationCalculators are not the same, it is highly unlikely we will get to this point.
        // See if the extraPmtMaps are the same size.
        int extraSz = extraPmtMap.size();
        int thatXtraSz = that.extraPmtMap.size();
        if (extraSz > thatXtraSz) return 1;
        if (extraSz < thatXtraSz) return -1;

        // If we get here, we know the keys for both objects should be the same. If they were not the same, the PmtKey
        // objects would not be the same and the PmtKey objects have already been compared. If they were not the same,
        // we would not be at this point. So, at this point, compare the extra payment values.

        Set<LocalDate> keys = extraPmtMap.keySet();

        for (LocalDate key : keys) {
            Double extraPmt = extraPmtMap.get(key);
            Double thatXtraPmt = that.extraPmtMap.get(key);
            // A null value is considered less than anything else.
            if (thatXtraPmt == null) return 1;
            result = extraPmt.compareTo(thatXtraPmt);
            if (result != 0) return result;
        }

        // Compare the two amortization maps. We should never get here because if we did, the objects would be the same.
        // We use an Interner, so objects that are equal will always be the same object.

        // See if the amortization maps are the same size.
        int amortSz = amortizationMap.size();
        int thatAmortSz = that.amortizationMap.size();
        if (amortSz > thatAmortSz) return 1;
        if (amortSz < thatAmortSz) return -1;

        keys = amortizationMap.keySet();
        for (LocalDate key : keys) {
            Payment thisPmt = amortizationMap.get(key);
            Payment thatPmt = that.amortizationMap.get(key);
            // A null value is considered less than anything else.
            if (thatPmt == null) return 1;
            result = thisPmt.compareTo(thatPmt);
            if (result != 0) return result;
        }

        return 0;
    }

    class DefaultPayment implements FixedAmortizationCalculator.Payment {

        private final double total; // the total amount paid for this payment
        private final double principal; // the principal paid for this payment
        private final double extraPrincipal; // the extra principal paid for this payment, if any
        private final double interest; // the interest paid for this payment
        private final double cumulativeInterest; // the cumulative interest paid after this payment
        private final double balance; // the balance due after this payment

        // Values rounded off
        private final double totalRounded;
        private final double principalRounded;
        private final double extraPrincipalRounded;
        private final double interestRounded;
        private final double balanceRounded;
        private final double cumulativeInterestRounded;

        private volatile int hashCode;

        private DefaultPayment(double principalOwed, double interestPaid, double extraPrincipal) {
            // The extraPmt is the extra amount being paid for this payment. The total is the monthly payment plus the
            // extra payment.
            interest = principalOwed * periodInterestRate;
            this.extraPrincipal = extraPrincipal;
            total = Math.min(pmt + extraPrincipal, principalOwed + interest);
            principal = total - interest;
            balance = principalOwed - principal;
            cumulativeInterest = interestPaid + interest;

            totalRounded = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            principalRounded = BigDecimal.valueOf(principal).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            extraPrincipalRounded = BigDecimal.valueOf(extraPrincipal).setScale(2, RoundingMode.HALF_EVEN)
                    .doubleValue();
            interestRounded = BigDecimal.valueOf(interest).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            balanceRounded = BigDecimal.valueOf(balance).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
            cumulativeInterestRounded = BigDecimal.valueOf(cumulativeInterest).setScale(2, RoundingMode.HALF_EVEN)
                    .doubleValue();
        }

        private DefaultPayment(double principalOwed, double interestPaid) {
            this(principalOwed, interestPaid, 0);
        }

        @Override
        public double getTotal() {
            return totalRounded;
        }

        @Override
        public double getPrincipal() {
            return principalRounded;
        }

        @Override
        public double getExtraPrincipal() {
            return extraPrincipalRounded;
        }

        @Override
        public double getInterest() {
            return interestRounded;
        }

        @Override
        public double getBalance() {
            return balanceRounded;
        }

        @Override
        public double getCumulativeInterest() {
            return cumulativeInterestRounded;
        }

        @Override
        public double getTotalUnrounded() {
            return total;
        }

        @Override
        public double getPrincipalUnrounded() {
            return principal;
        }

        @Override
        public double getInterestUnrounded() {
            return interest;
        }

        @Override
        public double getBalanceUnrounded() {
            return balance;
        }

        @Override
        public double getCumulativeInterestUnrounded() {
            return cumulativeInterest;
        }

        @Override
        public double[] getPmtStats() {

            return new double[] {
                    totalRounded,
                    principalRounded,
                    extraPrincipalRounded,
                    interestRounded,
                    cumulativeInterestRounded,
                    balanceRounded
            };
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("total", totalRounded)
                    .add("principal", principalRounded)
                    .add("extraPrincipal", extraPrincipalRounded)
                    .add("interest", interestRounded)
                    .add("cumulativeInterest", cumulativeInterestRounded)
                    .add("balance", balanceRounded)
                    .toString();
        }

        @Override
        public int hashCode() {
            int result = hashCode;

            if (result == 0) {
                result = Objects.hashCode(total,
                        principal,
                        extraPrincipal,
                        interest,
                        cumulativeInterest,
                        balance);
                hashCode = result;
            }

            return result;
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }

            if (!(object instanceof DefaultPayment)) {
                return false;
            }

            DefaultPayment that = (DefaultPayment) object;
            return Objects.equal(this.total, that.total)
                    && Objects.equal(this.principal, that.principal)
                    && Objects.equal(this.extraPrincipal, that.extraPrincipal)
                    && Objects.equal(this.interest, that.interest)
                    && Objects.equal(this.cumulativeInterest, that.cumulativeInterest)
                    && Objects.equal(this.balance, that.balance);
        }

        @Override
        public int compareTo(Payment o) {
            if (this == o) {
                return 0;
            }

            if (!(o instanceof DefaultPayment)) {
                throw new ClassCastException(
                        "Object to compare must be of type DefaultPayment. Object is " + o == null ? "null"
                                : o.getClass().getName());
            }

            DefaultPayment that = (DefaultPayment) o;
            return ComparisonChain.start()
                    .compare(total, that.total)
                    .compare(principal, that.principal)
                    .compare(extraPrincipal, that.extraPrincipal)
                    .compare(interest, that.interest)
                    .compare(cumulativeInterest, that.cumulativeInterest)
                    .compare(balance, that.balance)
                    .result();
        }

    }

}
