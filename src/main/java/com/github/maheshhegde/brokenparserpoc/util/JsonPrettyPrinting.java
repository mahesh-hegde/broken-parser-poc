package com.github.maheshhegde.brokenparserpoc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;

public class JsonPrettyPrinting {
    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    @SneakyThrows
    public static void prettyPrint(Object object) {
        mapper.writeValue(System.out, object);
    }
}
