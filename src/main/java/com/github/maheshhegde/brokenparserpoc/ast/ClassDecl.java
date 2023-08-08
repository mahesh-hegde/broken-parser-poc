package com.github.maheshhegde.brokenparserpoc.ast;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClassDecl {
    private String binaryName;
    private String simpleName;
    private TypeUsage parent;
    private List<Method> methods;
}
