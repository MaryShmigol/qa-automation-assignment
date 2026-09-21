package com.flamingo.qa.api.graphql.model;

import java.util.Map;

public record GraphQlRequest(String query, Map<String, Object> variables) {
}
