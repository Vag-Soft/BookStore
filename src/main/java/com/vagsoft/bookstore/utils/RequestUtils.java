package com.vagsoft.bookstore.utils;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.HandlerMapping;

public class RequestUtils {
    public static <T> T getPathVariable(final HttpServletRequest request, final String pathVariableName,
            final Class<T> type) {
        Object pathVariables = request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        if (pathVariables instanceof Map<?, ?> values) {
            Object value = values.get(pathVariableName);
            if (value == null) {
                throw new IllegalArgumentException("Path variable not found: " + pathVariableName);
            }

            // Trying to convert to Integer
            if (type == Integer.class) {
                try {
                    return type.cast(Integer.valueOf(value.toString()));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Path variable is not a valid integer: " + pathVariableName, e);
                }
            }
            throw new IllegalArgumentException("Unsupported type for path variable: " + type.getSimpleName());
        }

        throw new IllegalArgumentException("Path variables not found in request");
    }
}
