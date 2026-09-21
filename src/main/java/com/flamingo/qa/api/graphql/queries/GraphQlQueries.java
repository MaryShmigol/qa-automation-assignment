package com.flamingo.qa.api.graphql.queries;

public final class GraphQlQueries {
    private GraphQlQueries() {
    }

    public static final String MOVIES_WITH_PAGINATION = """
            query Movies($first: Int!, $skip: Int!) {
              movies(first: $first, skip: $skip) {
                id
                slug
              }
            }
            """;

    public static final String MOVIE_BY_ID = """
            query MovieById($id: ID!) {
              movie(where: { id: $id }) {
                id
                slug
              }
            }
            """;

    public static final String MOVIE_WITH_FRAGMENT_AND_NESTED_FIELDS = """
            fragment MovieDetails on Movie {
              id
              slug
              federateMovie {
                data {
                  Title
                  Genre
                  Director
                }
              }
            }

            query MovieWithDetails($id: ID!) {
              movie(where: { id: $id }) {
                ...MovieDetails
              }
            }
            """;

    public static final String MALFORMED_QUERY = """
            query BrokenQuery {
              movies(first: 2) {
                id
            """;

    public static final String NON_EXISTENT_FIELD = """
            query InvalidField {
              movies(first: 1) {
                id
                definitelyNotARealField
              }
            }
            """;
}
