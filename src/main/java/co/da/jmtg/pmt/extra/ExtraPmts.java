package co.da.jmtg.pmt.extra;

import co.da.jmtg.amort.PmtKey;

/**
 * <p>
 * Contains static classes pertaining to instances of <tt>ExtraPmt</tt>.
 * </p>
 * 
 * <p>
 * The getDefaultExtraPmt method returns an instance of DefaultExtraPmt. These objects use instance control. This means
 * there will never be two DefaultExtraPmt objects that are equal. Therefore, to compare two of these objects, it is
 * safe to always use == instead of equals().
 * </p>
 * 
 * @since 1.0
 * 
 * @author David Armstrong
 * 
 */
public class ExtraPmts {

    private ExtraPmts() {
    }

    /**
     * Creates an object with the specified {@link PmtKey} instance, count, and amount.
     * 
     * @param pmtKey
     *            The <tt>PmtKey</tt> instance
     * @param amount
     *            The amount value
     * 
     * @return a DefaultExtraPmt instance
     */
    public static ExtraPmt getDefaultExtraPmt(PmtKey pmtKey, double amount) {
        return DefaultExtraPmt.getInstance(pmtKey, amount);
    }
}
