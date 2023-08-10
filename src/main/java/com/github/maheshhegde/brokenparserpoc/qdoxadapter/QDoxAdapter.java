package com.github.maheshhegde.brokenparserpoc.qdoxadapter;

import com.github.maheshhegde.brokenparserpoc.ParserAdapter;
import com.github.maheshhegde.brokenparserpoc.ast.ClassDecl;
import com.github.maheshhegde.brokenparserpoc.ast.Method;
import com.github.maheshhegde.brokenparserpoc.ast.Param;
import com.github.maheshhegde.brokenparserpoc.ast.TypeUsage;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.library.*;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaType;
import com.thoughtworks.qdox.type.TypeResolver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.stream.Collectors;

public class QDoxAdapter implements ParserAdapter {

    @Override
    public List<ClassDecl> parse(List<String> sourceFiles, List<String> sourcePath, List<String> classPath) throws IOException {
        var projectBuilder = new JavaProjectBuilder();
        sourcePath.forEach(p -> projectBuilder.addSourceFolder(new File(p)));
        ClassLibraryBuilder libraryBuilder = new SortedClassLibraryBuilder();
        libraryBuilder.appendClassLoader(ClassLoader.getSystemClassLoader());
        var urls = new URL[classPath.size()];
        for (int i = 0; i < classPath.size(); i++) {
            File classPathFile = new File(classPath.get(i));
            urls[i] = classPathFile.toURI().toURL();
        }

        var urlClassLoader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        // TODO: TF is the behavior here for system classes?
        projectBuilder.addClassLoader(urlClassLoader);

        // I assume addSource actually means parseSource?
        for (String sourceFile : sourceFiles) {
            File src = new File(sourceFile);
            if (src.isDirectory()) {
                projectBuilder.addSourceTree(src);
            } else {
                projectBuilder.addSource(src);
            }
        }

        return projectBuilder.getClasses().stream()
                .map(this::getClassDecl)
                .collect(Collectors.toList());
    }

    private ClassDecl getClassDecl(JavaClass c) {
        var methods = c.getMethods().stream()
                .map(this::getMethod)
                .collect(Collectors.toList());
        return ClassDecl.builder()
                .simpleName(c.getName())
                .binaryName(c.getBinaryName())
                .parent(new TypeUsage(c.getSuperClass().getBinaryName()))
                .methods(methods)
                .build();
    }

    private Method getMethod(JavaMethod m) {
        var params = m.getParameters().stream()
                .map(this::getParameter)
                .collect(Collectors.toList());
        return Method.builder()
                .name(m.getName())
                .returnType(new TypeUsage(m.getReturnType(true).getBinaryName()))
                .params(params)
                .build();
    }

    private Param getParameter(JavaParameter p) {
        var type = new TypeUsage(p.getType().getBinaryName());
        return Param.builder()
                .type(type)
                .name(p.getName())
                .build();
    }
}
