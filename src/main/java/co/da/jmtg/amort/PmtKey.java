package co.da.jmtg.amort;

import java.util.List;

import org.joda.time.LocalDate;

import co.da.jmtg.pmt.PmtPeriod;

/**
 * The payment key calculator used in an amortization map built by an implementation of
 * {@link FixedAmortizationCalculator} . The key used in an amortization map is a {@link LocalDate} value that
 * represents the date that the * payment is due. The {@link PmtPeriod} value represents the time between payments.
 * Calculation of subsequent keys is based on the period value.
 * 
 * @since 1.0
 * @author David Armstrong
 */
public interface PmtKey extends Comparable<PmtKey> {

    /**
     * Gets the {@link LocalDate} key value that this implementation of PmtKey encapsulates.
     * 
     * @return the first key
     */
    LocalDate getFirstKey();

    /**
     * Gets the list of all the keys in chronological order.
     * 
     * @return <tt>List</tt> of <tt>LocalDate</tt> objects
     */
    List<LocalDate> getKeys();

    /**
     * Gets the {@link PmtPeriod} enum used to determine the length of time between keys.
     * 
     * @return <tt>PmtPeriod</tt> enum
     */
    PmtPeriod getPmtPeriod();

    /**
     * Gets the count of payment keys this object represents.
     * 
     * @return count of payments
     */
    int getCount();

    /**
     * Sets the {@link PmtPeriod} enum to be used to determine the length of time between keys. This method encourages
     * immutability by returning an instance of PmtKey
     * 
     * @param pmtPeriod
     *            <tt>PmtPeriod</tt> enum
     * @return new <tt>PmtKey</tt> instance
     */
    PmtKey setPmtPeriod(PmtPeriod pmtPeriod);

    /**
     * Sets the first key of a new PmtKey instance.
     * 
     * @param key
     *            the new first key
     * @return new <tt>PmtKey</tt> instance
     */
    PmtKey setFirstKey(LocalDate key);

    /**
     * Sets the count of payments of a new PmtKey instance
     * 
     * @param count
     *            the new count
     * @return new <tt>PmtKey</tt> instance
     */
    PmtKey setCount(int count);

}
