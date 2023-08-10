package com.github.maheshhegde.brokenparserpoc.javaparseradapter;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.maheshhegde.brokenparserpoc.ast.ClassDecl;
import com.github.maheshhegde.brokenparserpoc.ast.Method;
import com.github.maheshhegde.brokenparserpoc.ast.Param;
import com.github.maheshhegde.brokenparserpoc.ast.TypeUsage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassVisitor extends VoidVisitorAdapter<List<ClassDecl>> {

    private final List<Method> methods = new ArrayList<>();

    private static String getQualifiedName(ResolvedType resolvedType) {
        if (resolvedType.isReferenceType()) {
            return resolvedType.asReferenceType().getQualifiedName();
        } else {
            return resolvedType.describe();
        }
    }

    private Param getParam(Parameter parameter) {
        String typename;
        try {
            typename = getQualifiedName(parameter.getType().resolve());
        } catch (UnsolvedSymbolException e) {
            typename = null;
        }
        return Param.builder()
                .name(parameter.getNameAsString())
                .type(new TypeUsage(typename))
                .build();
    }

    @Override
    public void visit(MethodDeclaration n, List<ClassDecl> arg) {
        super.visit(n, arg);
        var resolvedMethod = n.resolve();
        var returnType = getQualifiedName(resolvedMethod.getReturnType());
        var params = n.getParameters().stream()
                .map(this::getParam).toList();
        var method = Method.builder()
                .name(n.getNameAsString())
                .returnType(new TypeUsage(returnType))
                .params(params)
                .build();
        methods.add(method);
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration n, List<ClassDecl> arg) {
        methods.clear();
        super.visit(n, arg);
        // create class
        var classDecl = ClassDecl.builder()
                .simpleName(n.getName().getIdentifier())
                .binaryName(n.getFullyQualifiedName().orElse(null))
                .methods(methods)
                .build();
        arg.add(classDecl);
    }
}
