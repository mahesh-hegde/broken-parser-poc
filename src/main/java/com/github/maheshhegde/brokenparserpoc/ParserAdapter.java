package com.github.maheshhegde.brokenparserpoc;

import com.github.maheshhegde.brokenparserpoc.ast.ClassDecl;

import java.io.IOException;
import java.util.List;

public interface ParserAdapter {
    List<ClassDecl> parse(List<String> sourceFiles, List<String> sourcePath, List<String> classPath) throws IOException;

    default String getName() {
        return getClass().getSimpleName().replace("Adapter", "");
    }
}
