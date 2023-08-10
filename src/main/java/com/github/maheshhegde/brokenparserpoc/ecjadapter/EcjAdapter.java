package com.github.maheshhegde.brokenparserpoc.ecjadapter;

import com.github.maheshhegde.brokenparserpoc.ParserAdapter;
import com.github.maheshhegde.brokenparserpoc.ast.ClassDecl;
import com.github.maheshhegde.brokenparserpoc.ast.Method;
import com.github.maheshhegde.brokenparserpoc.ast.Param;
import com.github.maheshhegde.brokenparserpoc.ast.TypeUsage;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import javax.annotation.Nullable;
import java.io.CharArrayWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EcjAdapter implements ParserAdapter {
    private final ASTParser parser = ASTParser.newParser(AST.JLS20);

    private CompilationUnit getCompilationUnit(String unitName, char[] source,
           List<String> classPath, List<String> sourcePath) {
        parser.setResolveBindings(true);
        // TODO: WHAT IS THIS
        // parser.setBindingsRecovery(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setIgnoreMethodBodies(true);
        Map<String, String> options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_5, options);
        parser.setCompilerOptions(options);
        parser.setUnitName(unitName);

        var classPathArray = classPath.toArray(String[]::new);
        var sourcePathArray = sourcePath.toArray(String[]::new);
        parser.setEnvironment(classPathArray, sourcePathArray, null, true);
        parser.setSource(source);
        return (CompilationUnit) parser.createAST(null);
    }

    @Override
    public List<ClassDecl> parse(List<String> sourceFiles, List<String> sourcePath, List<String> classPath) throws IOException {
        List<ClassDecl> classes = new ArrayList<>();
        for (var s: sourceFiles) {
            var charArrayWriter = new CharArrayWriter();
            try (var input = new FileReader(s)) {
                input.transferTo(charArrayWriter);
            }
            var cu = getCompilationUnit(s, charArrayWriter.toCharArray(), classPath, sourcePath);
            for (var type: cu.types()) {
                if (type instanceof TypeDeclaration typeDeclaration) {
                    classes.add(getClassDecl(typeDeclaration));
                }
            }
        }
        return classes;
    }

    private ClassDecl getClassDecl(TypeDeclaration typeDeclaration) {
        var methods = Arrays.stream(typeDeclaration.getMethods())
                .map(this::getMethod)
                .collect(Collectors.toList());
        return ClassDecl.builder()
                .simpleName(typeDeclaration.getName().getIdentifier())
                .binaryName(typeDeclaration.resolveBinding().getBinaryName())
                .methods(methods)
                .build();
    }

    private Method getMethod(MethodDeclaration methodDeclaration) {
        var rawReturnType = methodDeclaration.getReturnType2();
        var returnType = rawReturnType == null ? null :
                getResolvedBinaryName(rawReturnType);
        var params = new ArrayList<Param>();
        for (var p: methodDeclaration.parameters()) {
            if (p instanceof SingleVariableDeclaration paramDecl) {
                params.add(getParameter(paramDecl));
            }
        }
        return Method.builder()
                .name(methodDeclaration.getName().getIdentifier())
                .returnType(new TypeUsage(returnType))
                .params(params)
                .build();
    }

    private Param getParameter(SingleVariableDeclaration p) {
        var type = getResolvedBinaryName(p.getType());
        return Param.builder()
                .name(p.getName().getIdentifier())
                .type(new TypeUsage(type))
                .build();
    }

    private String getResolvedBinaryName(Type type) {
        var res = type.resolveBinding();
        if (res != null) {
            return res.getBinaryName();
        }
        return null;
    }
}
