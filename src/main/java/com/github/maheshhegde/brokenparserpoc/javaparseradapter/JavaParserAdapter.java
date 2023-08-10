package com.github.maheshhegde.brokenparserpoc.javaparseradapter;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.maheshhegde.brokenparserpoc.ParserAdapter;
import com.github.maheshhegde.brokenparserpoc.ast.ClassDecl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JavaParserAdapter implements ParserAdapter {

    @Override
    public List<ClassDecl> parse(List<String> sourceFiles, List<String> sourcePath, List<String> classPath) throws IOException {
        List<TypeSolver> typeSolvers = new ArrayList<>();
        typeSolvers.add(new ReflectionTypeSolver());
        for (var s: sourcePath) {
            typeSolvers.add(new JavaParserTypeSolver(s));
        }
        for (var c: classPath) {
            typeSolvers.add(new JarTypeSolver(c));
        }
        var combinedTypeSolver = new CombinedTypeSolver(typeSolvers);
        StaticJavaParser.getParserConfiguration()
                .setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));
        List<ClassDecl> classes = new ArrayList<>();
        for (var input: sourceFiles) {
            CompilationUnit cu = StaticJavaParser.parse(new File(input));
            cu.accept(new ClassVisitor(), classes);
        }
        return classes;
    }
}
