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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Ordering;

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

    /**
     * 
     * @param pmtCalculator
     * @param pmtKey
     * 
     * @throws NullPointerException
     *             if pmtCalculator or pmtKey is null.
     * 
     * @throws IllegalArgumentException
     *             if PmtPeriod in PmtCalculator is not BIWEEKLY, RAPID_BIWEEKLY, MONTHLY, WEEKLY, or RAPID_WEEKLY.
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

    /**
     * Builds a FixedAmortizationCalculator with extra payments. It is the responsibility of the developer to ensure
     * that all dates in the ExtraPmt object are valid dates for this mortgage.
     * 
     * @param pmtCalculator
     * @param pmtKey
     * @param extraPmts
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
    private DefaultFixedAmortizationCalculator(PmtCalculator pmtCalculator, PmtKey pmtKey, ExtraPmt extraPmts) {
        checkPreconditions(pmtCalculator, pmtKey, extraPmts);

        this.pmtCalculator = pmtCalculator;
        periodInterestRate = pmtCalculator.getPeriodInterestRate();
        pmt = pmtCalculator.getPmtUnrounded(); // MUST get the unrounded payment amount for accuracy.

        this.pmtKey = pmtKey;
        extraPmtMap = initializeExtraPmts(extraPmts);

        amortizationMap = buildTable(extraPmtMap);
    }

    /**
     * Builds a FixedAmortizationCalculator with extra payments. It is the responsibility of the developer to ensure
     * that all dates in the ExtraPmt object are valid dates for this mortgage.
     * 
     * @param pmtCalculator
     * @param pmtKey
     * @param extraPmts
     * 
     * @throws NullPointerException
     *             if pmtCalculator, pmtKey, or extraPmts is null.
     * 
     * @throws IllegalArgumentException
     *             if PmtPeriod in PmtCalculator is not BIWEEKLY, RAPID_BIWEEKLY, MONTHLY, WEEKLY, or RAPID_WEEKLY.
     * 
     * @throws IllegalArgumentException
     *             if extraPmts contains dates that are not valid payment dates for the mortgage this object represents.
     * 
     * @throws IllegalArgumentException
     *             if any of the ExtraPmt objects have duplicate date keys.
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

    /**
     * Builds a FixedAmortizationCalculator with extra payments. It is the responsibility of the developer to ensure
     * that all dates in the ExtraPmt object are valid dates for this mortgage.
     * 
     * @param pmtCalculator
     * @param pmtKey
     * @param extraPmts
     * 
     * @throws NullPointerException
     *             if pmtCalculator, pmtKey, or extraPmts is null.
     * 
     * @throws IllegalArgumentException
     *             if PmtPeriod in PmtCalculator is not BIWEEKLY, RAPID_BIWEEKLY, MONTHLY, WEEKLY, or RAPID_WEEKLY.
     * 
     * @throws IllegalArgumentException
     *             if extraPmts contains dates that are not valid payment dates for the mortgage this object represents.
     * 
     * @throws IllegalArgumentException
     *             if any of the ExtraPmt objects have duplicate date keys.
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

        for (LocalDate key : extraPmts.getPmtKey()) {
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
     * Creates a new FixedAmortizationCalculator with the extra payments represented by the ExtraPmt object passed in.
     * If this object already had some extra payments, those that did not have the same keys as the ones passed in will
     * also be in the new object. If the extra payments in this object shared any of the keys from the extra payments
     * passed in, the new values will overwrite the old ones.
     * 
     * @param ExtraPmt
     *            the extra payments that will be set in the new FixedAmortizationCalculator object.
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

    @Override
    public FixedAmortizationCalculator setExtraPayments(Iterable<ExtraPmt> extraPmts) {
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");

        if (extraPmtMap.isEmpty()) {
            return getInstance(pmtCalculator, pmtKey, extraPmts);
        }

        return getInstance(pmtCalculator, pmtKey, buildExtraPmtFromExisting(extraPmts, false));
    }

    @Override
    public FixedAmortizationCalculator setExtraPayments(Map<LocalDate, Double> extraPmts) {
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");

        if (extraPmtMap.isEmpty()) {
            return getInstance(pmtCalculator, pmtKey, extraPmts);
        }

        return getInstance(pmtCalculator, pmtKey, buildExtraPmtFromExisting(extraPmts, false));
    }

    @Override
    public FixedAmortizationCalculator addExtraPayment(ExtraPmt extraPmts) {
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");

        if (extraPmtMap.isEmpty()) {
            return getInstance(pmtCalculator, pmtKey, extraPmts);
        }

        return getInstance(pmtCalculator, pmtKey, buildExtraPmtFromExisting(extraPmts, true));
    }

    @Override
    public FixedAmortizationCalculator addExtraPayments(Iterable<ExtraPmt> extraPmts) {
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");

        if (extraPmtMap.isEmpty()) {
            return getInstance(pmtCalculator, pmtKey, extraPmts);
        }

        return getInstance(pmtCalculator, pmtKey, buildExtraPmtFromExisting(extraPmts, true));
    }

    @Override
    public FixedAmortizationCalculator addExtraPayments(Map<LocalDate, Double> extraPmts) {
        Preconditions.checkNotNull(extraPmts, "extraPmts must not be null.");

        if (extraPmtMap.isEmpty()) {
            return getInstance(pmtCalculator, pmtKey, extraPmts);
        }

        return getInstance(pmtCalculator, pmtKey, buildExtraPmtFromExisting(extraPmts, true));
    }

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
     */
    @Override
    public double getExtraPayment(LocalDate key) {
        Preconditions.checkNotNull(key, "key must not be null.");
        Double e = extraPmtMap.get(key);
        return e == null ? 0.0 : e.doubleValue();
    }

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

        Iterator<LocalDate> keyIterator = pmtKey.iterator();
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

        Iterator<LocalDate> keyIterator = pmtKey.iterator();
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
        return Objects.equal(this.pmtCalculator, that.pmtCalculator)
                && Objects.equal(this.pmtKey, that.pmtKey)
                && Objects.equal(this.periodInterestRate, that.periodInterestRate)
                && Objects.equal(this.pmt, that.pmt)
                && Objects.equal(this.extraPmtMap, that.extraPmtMap)
                && Objects.equal(this.amortizationMap, that.amortizationMap);
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

        // @Override
        // public String toString() {
        // return Objects.toStringHelper(this)
        // .add("total", total)
        // .add("principal", principal)
        // .add("extraPrincipal", extraPrincipal)
        // .add("interest", interest)
        // .add("cumulativeInterest", cumulativeInterest)
        // .add("balance", balance)
        // .toString();
        // }

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

    }

}
