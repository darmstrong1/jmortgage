package co.da.jmtg.amort;

import java.util.Iterator;
import java.util.List;

import org.joda.time.LocalDate;

import co.da.jmtg.pmt.PmtPeriod;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;

class DefaultPmtKey implements PmtKey {

    private final LocalDate firstKey;
    private final List<LocalDate> keys;
    private final PmtPeriod pmtPeriod;
    private final int count;

    private volatile int hashCode;

    // Cache all instances of DefaultPmtKey. This will guarantee that only unique DefaultPmtKey objects will exist. It
    // means clients can use == to compare for equality.
    private static final Interner<PmtKey> interner = Interners.newStrongInterner();

    /**
     * Creates an object using the specified pmtPeriod and LocalDate object.
     * 
     * @param pmtPeriod
     *            The <tt>PmtPeriod</tt> object
     * @param key
     *            The <tt>LocalDate</tt> object
     */
    private DefaultPmtKey(PmtPeriod pmtPeriod, LocalDate key) {
        this(pmtPeriod, key, 1);
    }

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

    public static PmtKey getInstance(PmtPeriod pmtPeriod, LocalDate key) {
        return interner.intern(new DefaultPmtKey(pmtPeriod, key));
    }

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

    @Override
    public Iterator<LocalDate> iterator() {
        return keys.iterator();
    }

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
                && Objects.equal(this.keys, that.keys)
                && Objects.equal(this.pmtPeriod, that.pmtPeriod)
                && Objects.equal(this.count, that.count);
    }

}
