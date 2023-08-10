package com.github.maheshhegde.brokenparserpoc;

import com.github.maheshhegde.brokenparserpoc.ast.ClassDecl;
import com.github.maheshhegde.brokenparserpoc.ast.Method;
import com.github.maheshhegde.brokenparserpoc.ecjadapter.EcjAdapter;
import com.github.maheshhegde.brokenparserpoc.qdoxadapter.QDoxAdapter;
import lombok.SneakyThrows;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Main {
    private static final List<String> classPath = List.of(".jar/pdfbox-2.0.27.jar");
    private static final List<String> sourcePath = List.of("src/test/java");

    private static final String srcTestJava = "src/test/java";
    private static final File comCompanyPkg = new File(srcTestJava, "com/company/pkg");
    private static final File orgOssLibrary = new File(srcTestJava, "org/oss/library");

    private static Method getMethodByName(ClassDecl c, String name) {
        return c.getMethods().stream()
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    @SneakyThrows
    private static ClassDecl parseSingleClass(ParserAdapter adapter, File src) {
        var parsed = adapter.parse(List.of(src.getPath()), sourcePath, classPath);
        return parsed.get(0);
    }

    private static String getBinaryNameOfParam(Method m, int pos) {
        return m.getParams().get(pos).getType().getBinaryName();
    }

    private static boolean checkImportsWellFormedForFile(ParserAdapter adapter, File file) {
        var parsed = parseSingleClass(adapter, file);
        var fn1 = getMethodByName(parsed, "fn1");

        var expectedBinaryName = "org.oss.library.good.WellFormedClass";
        return fn1.getReturnType().getBinaryName().equals(expectedBinaryName)
                && fn1.getParams().get(0).getType().getBinaryName().equals(expectedBinaryName);
    }

    private static boolean checkImportsWellFormed(ParserAdapter adapter) {
        var importsWellFormed = new File(comCompanyPkg, "ImportsWellFormedClass.java");
        return checkImportsWellFormedForFile(adapter, importsWellFormed);
    }

    private static boolean checkImportsWellFormedWildcard(ParserAdapter adapter) {
        var importsWildcard = new File(comCompanyPkg, "WildCardImportsWellFormedClass.java");
        return checkImportsWellFormedForFile(adapter, importsWildcard);
    }

    private static boolean checkImportsBadSourceFileInImpl(ParserAdapter adapter) {
        var file = new File(comCompanyPkg, "ImportsUnknownLibraryImporter.java");
        var parsed = parseSingleClass(adapter, file);
        var g = getMethodByName(parsed, "g");
        return getBinaryNameOfParam(g, 0).equals("java.lang.String");
    }

    private static boolean checkImportsBadSourceFileInSignature(ParserAdapter adapter) {
        var file = new File(comCompanyPkg, "ImportsUnknownLibraryImporter.java");
        var parsed = parseSingleClass(adapter, file);
        var g = getMethodByName(parsed, "f");
        return getBinaryNameOfParam(g, 0).equals("org.oss.library.bad.UnknownLibraryImporter");
    }

    private static boolean checkTypeFromJarLibraryImport(ParserAdapter adapter) {
        var file = new File(orgOssLibrary, "bad/UnknownLibraryImporter.java");
        var parsed = parseSingleClass(adapter, file);
        var f = getMethodByName(parsed, "getFont");
        return f.getReturnType().getBinaryName().equals("org.apache.pdfbox.pdmodel.font.PDFont")
                && getBinaryNameOfParam(f, 0).equals("org.apache.pdfbox.pdmodel.PDDocument");
    }

    private static boolean useUnknownLibraryTypeInImplementation(ParserAdapter adapter) {
        var file = new File(orgOssLibrary, "bad/UnknownLibraryImporter.java");
        var parsed = parseSingleClass(adapter, file);
        var f = getMethodByName(parsed, "getDistance");
        return getBinaryNameOfParam(f, 0).equals("java.lang.String");
    }

    private static boolean useUnknownLibraryTypeInSignatureButImportQualified(ParserAdapter adapter) {
        var file = new File(orgOssLibrary, "bad/UnknownLibraryImporter.java");
        var parsed = parseSingleClass(adapter, file);
        var f = getMethodByName(parsed, "getDistanceUsingLD");
        return getBinaryNameOfParam(f, 0)
                .equals("org.apache.commons.text.similarity.LevenshteinDistance");
    }

    // No parser can do this because its not possible.
    private static boolean useUnknownLibraryTypeImportedUsingWildcard(ParserAdapter adapter) {
        var file = new File(orgOssLibrary, "bad/UnknownLibraryImporter.java");
        var parsed = parseSingleClass(adapter, file);
        var f = getMethodByName(parsed, "sub");
        return Objects.equals(getBinaryNameOfParam(f, 0),
                "org.apache.commons.text.StringSubstitutor");
    }

    public static void main(String[] args) {
        var adapters = List.of(new QDoxAdapter(), new EcjAdapter());

        Map<String, Function<ParserAdapter, Boolean>> checks = new LinkedHashMap<>();
        checks.put("Well formed source class import", Main::checkImportsWellFormed);
        checks.put("Well formed source class import using wildcard", Main::checkImportsWellFormedWildcard);
        checks.put("Type from bad source used in impl", Main::checkImportsBadSourceFileInImpl);
        checks.put("Type from bad source used in signature", Main::checkImportsBadSourceFileInSignature);
        checks.put("Type from JAR library in classpath", Main::checkTypeFromJarLibraryImport);
        checks.put("Signature of method using unknown types in implementation", Main::useUnknownLibraryTypeInImplementation);
        checks.put("Unknown library type imported qualified", Main::useUnknownLibraryTypeInSignatureButImportQualified);
        checks.put("Unknown library type, wildcard import", Main::useUnknownLibraryTypeImportedUsingWildcard);

        System.out.printf("%-60s | ", "CHECK");
        for (var adapter: adapters) {
            System.out.printf("%-10s | ", adapter.getName());
        }
        System.out.println();
        System.out.println("-".repeat(120));

        for (var check: checks.entrySet()) {
            System.out.printf("%-60s | ", check.getKey());
            for (var adapter: adapters) {
                System.out.printf("%-10s | ", check.getValue().apply(adapter));
            }
            System.out.println();
        }
    }
}
