package com.company.pkg;

import org.oss.library.good.WellFormedClass;

/** Default case, must pass with all parsers */
public class ImportsWellFormedClass {
    WellFormedClass fn1(WellFormedClass w) {
        throw new RuntimeException("Not implemented");
    }
}
