package com.github.maheshhegde.brokenparserpoc.ast;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Method {
    private String name;
    private TypeUsage returnType;
    private List<Param> params;
}
