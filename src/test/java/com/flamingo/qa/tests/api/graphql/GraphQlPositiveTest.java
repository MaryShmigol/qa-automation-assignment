package com.flamingo.qa.tests.api.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.flamingo.qa.api.common.ApiResponse;
import com.flamingo.qa.api.graphql.client.GraphQlClient;
import com.flamingo.qa.api.graphql.queries.GraphQlQueries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@Tag("api")
@Tag("graphql")
@Tag("positive")
@TestInstance(PER_CLASS)
class GraphQlPositiveTest {

    private final GraphQlClient client = new GraphQlClient();

    private String existingMovieId;

    @BeforeAll
    void loadExistingMovieId() {
        ApiResponse<JsonNode> response = client.execute(
                GraphQlQueries.MOVIES_WITH_PAGINATION,
                Map.of("first", 1, "skip", 0)
        );

        JsonNode responseBody = response.body();
        JsonNode errors = responseBody.path("errors");
        JsonNode movies = responseBody.path("data").path("movies");

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode())
                    .as("HTTP status code")
                    .isEqualTo(200);

            softly.assertThat(
                            errors.isMissingNode() || errors.isNull()
                    )
                    .as("GraphQL errors should be absent or null")
                    .isTrue();

            softly.assertThat(movies.isArray())
                    .as("Movies should be returned as an array")
                    .isTrue();

            softly.assertThat(movies.size())
                    .as("Movies array should not be empty")
                    .isPositive();

            softly.assertThat(
                            movies.path(0).path("id").asText()
                    )
                    .as("Existing movie ID")
                    .isNotBlank();
        });

        existingMovieId = movies.path(0)
                .path("id")
                .asText();
    }

    @Test
    void moviesQueryShouldRespectPaginationLimitAndVariables() {
        int first = 3;
        int skip = 0;

        ApiResponse<JsonNode> response = client.execute(
                GraphQlQueries.MOVIES_WITH_PAGINATION,
                Map.of(
                        "first", first,
                        "skip", skip
                )
        );

        JsonNode responseBody = response.body();
        JsonNode errors = responseBody.path("errors");
        JsonNode data = responseBody.path("data");
        JsonNode movies = data.path("movies");

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode())
                    .as("HTTP status code")
                    .isEqualTo(200);

            softly.assertThat(
                            errors.isMissingNode() || errors.isNull()
                    )
                    .as("GraphQL errors should be absent or null")
                    .isTrue();

            softly.assertThat(data.isObject())
                    .as("GraphQL data should be an object")
                    .isTrue();

            softly.assertThat(movies.isArray())
                    .as("Movies should be returned as an array")
                    .isTrue();

            softly.assertThat(movies.size())
                    .as("Movies count should respect the requested limit")
                    .isBetween(1, first);

            movies.forEach(movie -> {
                softly.assertThat(movie.path("id").asText())
                        .as("Movie ID")
                        .isNotBlank();

                softly.assertThat(movie.path("slug").asText())
                        .as("Movie slug")
                        .isNotBlank();
            });
        });
    }

    @Test
    void movieShouldBeRetrievableById() {
        ApiResponse<JsonNode> response = client.execute(
                GraphQlQueries.MOVIE_BY_ID,
                Map.of("id", existingMovieId)
        );

        JsonNode responseBody = response.body();
        JsonNode errors = responseBody.path("errors");
        JsonNode data = responseBody.path("data");
        JsonNode movie = data.path("movie");

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode())
                    .as("HTTP status code")
                    .isEqualTo(200);

            softly.assertThat(
                            errors.isMissingNode() || errors.isNull()
                    )
                    .as("GraphQL errors should be absent or null")
                    .isTrue();

            softly.assertThat(data.isObject())
                    .as("GraphQL data should be an object")
                    .isTrue();

            softly.assertThat(movie.isObject())
                    .as("Movie should be returned as an object")
                    .isTrue();

            softly.assertThat(movie.path("id").asText())
                    .as("Movie ID")
                    .isEqualTo(existingMovieId);

            softly.assertThat(movie.path("slug").asText())
                    .as("Movie slug")
                    .isNotBlank();
        });
    }

    @Test
    void fragmentQueryShouldReturnMovieFields() {
        ApiResponse<JsonNode> response = client.execute(
                GraphQlQueries.MOVIE_WITH_FRAGMENT,
                Map.of("id", existingMovieId)
        );

        JsonNode responseBody = response.body();
        JsonNode errors = responseBody.path("errors");
        JsonNode data = responseBody.path("data");
        JsonNode movie = data.path("movie");

        assertSoftly(softly -> {
            softly.assertThat(response.statusCode())
                    .as("HTTP status code")
                    .isEqualTo(200);

            softly.assertThat(
                            errors.isMissingNode() || errors.isNull()
                    )
                    .as(
                            "GraphQL errors should be absent or null. Actual errors: %s",
                            errors.toPrettyString()
                    )
                    .isTrue();

            softly.assertThat(data.isObject())
                    .as("GraphQL data should be an object")
                    .isTrue();

            softly.assertThat(movie.isObject())
                    .as("Movie should be returned as an object")
                    .isTrue();

            softly.assertThat(movie.path("id").asText())
                    .as("Movie ID returned through fragment")
                    .isEqualTo(existingMovieId);

            softly.assertThat(movie.path("slug").asText())
                    .as("Movie slug returned through fragment")
                    .isNotBlank();
        });
    }
}