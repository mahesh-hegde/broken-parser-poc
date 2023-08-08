package com.github.maheshhegde.brokenparserpoc.ast;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Param {
    private String name;
    private TypeUsage type;
}
