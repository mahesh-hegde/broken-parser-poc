package com.company.pkg;

import org.oss.library.bad.UnknownLibraryImporter;

// Imports class `unknownLibraryImporter` which has unresolved dependencies.
// All members must be parsed correctly.
// Because all we need to know for binding this class is UnknownLibraryImporter's FQN.
public class ImportsUnknownLibraryImporter {
    // Implementation uses UnknownLibraryImporter
    int g() {
        return new UnknownLibraryImporter().getDistance("igeub", "hgi");
    }

    // Signature uses UnknownLibraryImporter
    // This should pass - that's the standard.
    UnknownLibraryImporter f(UnknownLibraryImporter u) {
        throw new RuntimeException("Not implemented");
    }
}
