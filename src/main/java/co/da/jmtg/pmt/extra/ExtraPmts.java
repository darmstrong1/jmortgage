package co.da.jmtg.pmt.extra;

import co.da.jmtg.amort.PmtKey;

public class ExtraPmts {

    private ExtraPmts() {
    }

    public static ExtraPmt getDefaultExtraPmt(PmtKey pmtKey, double amount) {
        return DefaultExtraPmt.getInstance(pmtKey, amount);
    }
}
