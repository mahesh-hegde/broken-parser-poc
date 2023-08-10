package com.github.maheshhegde.brokenparserpoc;

import com.github.maheshhegde.brokenparserpoc.ecjadapter.EcjAdapter;
import com.github.maheshhegde.brokenparserpoc.javaparseradapter.JavaParserAdapter;
import com.github.maheshhegde.brokenparserpoc.util.JsonPrettyPrinting;
import lombok.SneakyThrows;

import java.util.Arrays;
import java.util.List;

public class ConsoleRunner {
    private static final List<String> classPath = List.of(".jar/pdfbox-2.0.27.jar");
    private static final List<String> sourcePath = List.of("src/test/java");

    private static final List<String> defaultSrc =
            List.of("src/test/java/org/oss/library/bad/UnknownLibraryImporter.java");

    @SneakyThrows
    public static void main(String[] args) {
        var adapter = new JavaParserAdapter();
        var src = defaultSrc;
        if (args.length != 0) {
            src = Arrays.asList(args);
        }
        var classDecls = adapter.parse(src, sourcePath, classPath);
        for (var decl: classDecls) {
            JsonPrettyPrinting.prettyPrint(decl);
        }
    }
}
