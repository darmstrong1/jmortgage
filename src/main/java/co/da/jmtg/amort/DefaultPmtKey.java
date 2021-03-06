package co.da.jmtg.amort;

import java.util.List;

import org.joda.time.LocalDate;

import co.da.jmtg.pmt.PmtPeriod;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

/**
 * The default implementation of PmtKey.
 * 
 * @author David Armstrong
 * 
 */
class DefaultPmtKey implements PmtKey {

    private final LocalDate firstKey;
    private final List<LocalDate> keys;
    private final PmtPeriod pmtPeriod;
    private final int count;

    private volatile int hashCode;

    // Cache all instances of DefaultPmtKey. This will guarantee that only unique DefaultPmtKey objects will exist. It
    // means clients can use == to compare for equality.
    private static final Interner<PmtKey> interner = Interners.newStrongInterner();

    /*
     * Creates an object using the specified pmtPeriod and LocalDate object. The count of payments defaults to 1.
     * 
     * @param pmtPeriod The <tt>PmtPeriod</tt> object
     * 
     * @param key The <tt>LocalDate</tt> object
     */
    private DefaultPmtKey(PmtPeriod pmtPeriod, LocalDate key) {
        this(pmtPeriod, key, 1);
    }

    /*
     * Creates an object using the specified pmtPeriod and LocalDate object.
     * 
     * @param pmtPeriod The <tt>PmtPeriod</tt> object
     * 
     * @param key The <tt>LocalDate</tt> object
     * 
     * @param count the count of payments
     * 
     * @throws NullPointerException if pmtPeriod is null or key is null.
     * 
     * @throws IllegalArgumentException if pmtPeriod is PmtPeriod.ONETIME, but count is greater than 1.
     * 
     * @return PmtKey instance
     */
    private DefaultPmtKey(PmtPeriod pmtPeriod, LocalDate key, int count) {

        Preconditions.checkNotNull(pmtPeriod, "pmtPeriod must not be null.");
        Preconditions.checkNotNull(key, "key must not be null.");
        // If pmtPeriod is ONETIME, throw an exception if count is greater than 1.
        Preconditions.checkArgument((pmtPeriod == PmtPeriod.ONETIME ? count == 1 : true),
                "For pmtPeriod of PmtPeriod.ONETIME, count must be 1.");

        this.pmtPeriod = pmtPeriod;
        this.count = count;

        firstKey = key;
        ImmutableList.Builder<LocalDate> builder = ImmutableList.builder();
        builder.add(firstKey);
        while (--count > 0) {
            key = key.plus(pmtPeriod.period());
            builder.add(key);
        }
        keys = builder.build();
    }

    /**
     * Creates an object using the specified pmtPeriod and LocalDate object. The count of payments defaults to 1.
     * 
     * @param pmtPeriod
     *            The <tt>PmtPeriod</tt> object
     * @param key
     *            The <tt>LocalDate</tt> object
     * 
     * @throws NullPointerException
     *             if pmtPeriod is null or key is null.
     * 
     * @return PmtKey instance
     */
    public static PmtKey getInstance(PmtPeriod pmtPeriod, LocalDate key) {
        return interner.intern(new DefaultPmtKey(pmtPeriod, key));
    }

    /**
     * Creates an object using the specified pmtPeriod and LocalDate object.
     * 
     * @param pmtPeriod
     *            The <tt>PmtPeriod</tt> object
     * @param key
     *            The <tt>LocalDate</tt> object
     * @param count
     *            the count of payments
     * 
     * @throws NullPointerException
     *             if pmtPeriod is null or key is null.
     * 
     * @throws IllegalArgumentException
     *             if pmtPeriod is PmtPeriod.ONETIME, but count is greater than 1.
     * 
     * @return PmtKey instance
     */
    public static PmtKey getInstance(PmtPeriod pmtPeriod, LocalDate key, int count) {
        return interner.intern(new DefaultPmtKey(pmtPeriod, key, count));
    }

    @Override
    public LocalDate getFirstKey() {
        return firstKey;
    }

    @Override
    public List<LocalDate> getKeys() {
        return keys;
    }

    @Override
    public PmtPeriod getPmtPeriod() {
        return pmtPeriod;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public PmtKey setPmtPeriod(PmtPeriod pmtPeriod) {
        return getInstance(pmtPeriod, firstKey, count);
    }

    @Override
    public PmtKey setFirstKey(LocalDate key) {
        return getInstance(pmtPeriod, key, count);
    }

    @Override
    public PmtKey setCount(int count) {
        return getInstance(pmtPeriod, firstKey, count);
    }

    // @Override
    // public Iterator<LocalDate> iterator() {
    // return keys.iterator();
    // }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("firstKey", firstKey)
                .add("keys", keys)
                .add("pmtPeriod", pmtPeriod)
                .add("count", count)
                .toString();
    }

    @Override
    public int hashCode() {
        int result = hashCode;

        if (result == 0) {
            result = Objects.hashCode(firstKey, keys, pmtPeriod, count);
            hashCode = result;
        }

        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }

        if (!(object instanceof DefaultPmtKey)) {
            return false;
        }

        DefaultPmtKey that = (DefaultPmtKey) object;
        return Objects.equal(this.firstKey, that.firstKey)
                && Objects.equal(this.pmtPeriod, that.pmtPeriod)
                && Objects.equal(this.count, that.count)
                && Objects.equal(this.keys, that.keys);
    }

    /**
     * Compare two DefaultPmtKey objects.
     * 
     * @param o
     *            the object to compare
     * 
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
     *         the specified object.
     * 
     * @throws ClassCastException
     *             if the PmtKey object passed in is not a DefaultPmtKey object.
     */
    @Override
    public int compareTo(PmtKey o) {
        if (this == o) {
            return 0;
        }

        if (!(o instanceof DefaultPmtKey)) {
            throw new ClassCastException(
                    "Object to compare must be of type DefaultPmtKey. Object is " + o == null ? "null" : o.getClass()
                            .getName());
        }
        DefaultPmtKey that = (DefaultPmtKey) o;
        ComparisonChain cmpChain = ComparisonChain.start()
                .compare(firstKey, that.firstKey)
                .compare(pmtPeriod, that.pmtPeriod)
                .compare(count, that.count);

        int result = cmpChain.result();
        if (result != 0) {
            return result;
        }

        // It should never get to this point in the code. Because we are using an Interner, two objects that are equal
        // are the same object, so the first check will return 0. If the objects are not equal, at least one of the
        // three values we check before the list will be unequal, so it should never have to compare the contents of the
        // lists (thankfully).
        for (int i = 0; i < keys.size(); i++) {
            result = cmpChain.compare(keys.get(i), that.keys.get(i)).result();
            if (result != 0) return result;
        }

        return result;
    }

}
