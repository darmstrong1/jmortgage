package co.da.jmtg.pmt.extra;

import co.da.jmtg.amort.PmtKey;

/**
 * Contains static classes pertaining to instances of <tt>ExtraPmt</tt>
 * 
 * @since 1.0
 * 
 * @author David Armstrong
 * 
 */
public class ExtraPmts {

    private ExtraPmts() {
    }

    public static ExtraPmt getDefaultExtraPmt(PmtKey pmtKey, double amount) {
        return DefaultExtraPmt.getInstance(pmtKey, amount);
    }
}
