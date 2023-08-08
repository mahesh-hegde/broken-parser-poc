package org.oss.library.bad;

import org.apache.commons.text.*;
import org.apache.pdfbox.text.*;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;

/** This class imports an unknown library
 * which means, in practice - we remove Apache Commons Text from classpath when parsing this one.
 * <br/>
 * <br/>
 * 1. Error in Fn2 should not prevent Fn1 from being parsed <br/>
 * 2. Any dependents must be parsed successfully. <br/>
 */
public class UnknownLibraryImporter {
    // Fn 1 - import an available library type
    public PDFont getFont(PDDocument pdDocument) {
        throw new RuntimeException("Not Implemented");
    }

    // Fn 2 - import an unavailable library type but not expose it in the interface
    // Ideally, the parser should still parse this, because the signature is fine.
    public int getDistance(String a, String b) {
        return LevenshteinDistance.getDefaultInstance().apply(a, b);
    }

    // Fn 3 - import an unavailable library type and expose it in signature.
    // I do not expect the parser to parse this.
    // But great if the parser can figure it out based on import stmt.
    public int getDistanceUsingLD(LevenshteinDistance ld, String a, String b) {
        return ld.apply(a, b);
    }

    // Fn 4 - same as Fn 3 but also the import is wildcard.
    // There is another wildcard import too.
    // So it should be impossible to decide where StringSubstituter comes from.
    public String sub(StringSubstitutor substitutor, String s, int level) {
        return substitutor.replace(s);
    }
}
