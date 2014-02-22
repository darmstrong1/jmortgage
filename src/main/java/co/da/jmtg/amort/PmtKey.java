package co.da.jmtg.amort;

import java.util.List;

import org.joda.time.LocalDate;

import co.da.jmtg.pmt.PmtPeriod;

/**
 * The payment key calculator used in an amortization map built by an implementation of
 * {@link FixedAmortizationCalculator} . The key used in an amortization map is a Long value that represents the date
 * that the * payment is due. The {@link PmtPeriod} value represents the time between payments. Implementations of this
 * interface provide methods to calculate the first key and to calculate subsequent keys. Calculation of subsequent keys
 * is based on the interval value.
 * 
 * @since 1.0
 * @author David Armstrong
 */
public interface PmtKey extends Comparable<PmtKey> {

    /**
     * Gets the <tt>LocalDate</tt> key value that this implementation of PmtKey encapsulates.
     * 
     * @return <tt>LocalDate</tt>
     */
    LocalDate getFirstKey();

    /**
     * Gets the list of all the keys in chronological order.
     * 
     * @return <tt>List</tt> of <tt>LocalDate</tt> objects
     */
    List<LocalDate> getKeys();

    /**
     * Gets the <tt>PmtPeriod</tt> enum used to determine the length of time between keys.
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
     * Sets the <tt>PmtPeriod</tt> enum to be used to determine the length of time between keys. This method encourages
     * immutability by returning an instance of PmtKey
     * 
     * @param pmtPeriod
     *            <tt>PmtPeriod</tt> enum
     * @return <tt>PmtKey</tt> instantiation
     */
    PmtKey setPmtPeriod(PmtPeriod pmtPeriod);

    PmtKey setFirstKey(LocalDate key);

    PmtKey setCount(int count);

}
