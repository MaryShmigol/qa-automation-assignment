package com.flamingo.qa.tests.api.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.flamingo.qa.api.common.ApiResponse;
import com.flamingo.qa.api.graphql.client.GraphQlClient;
import com.flamingo.qa.api.graphql.queries.GraphQlQueries;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@Tag("graphql")
class GraphQlPositiveTest {
    private final GraphQlClient client = new GraphQlClient();

    @Test
    void moviesQueryShouldRespectPaginationLimitAndVariables() {
        int first = 3;
        int skip = 0;

        ApiResponse<JsonNode> response = client.execute(
                GraphQlQueries.MOVIES_WITH_PAGINATION,
                Map.of("first", first, "skip", skip)
        );

        assertSuccessfulGraphQlResponse(response);

        JsonNode movies = response.body().path("data").path("movies");
        assertThat(movies.isArray()).isTrue();
        assertThat(movies.size()).isBetween(1, first);
        movies.forEach(movie -> {
            assertThat(movie.path("id").asText()).isNotBlank();
            assertThat(movie.path("slug").asText()).isNotBlank();
        });
    }

    @Test
    void movieShouldBeRetrievableById() {
        String movieId = existingMovieId();

        ApiResponse<JsonNode> response = client.execute(
                GraphQlQueries.MOVIE_BY_ID,
                Map.of("id", movieId)
        );

        assertSuccessfulGraphQlResponse(response);
        JsonNode movie = response.body().path("data").path("movie");

        assertThat(movie.isObject()).isTrue();
        assertThat(movie.path("id").asText()).isEqualTo(movieId);
        assertThat(movie.path("slug").asText()).isNotBlank();
    }

    @Test
    void fragmentQueryShouldReturnNestedMovieDetails() {
        String movieId = existingMovieId();

        ApiResponse<JsonNode> response = client.execute(
                GraphQlQueries.MOVIE_WITH_FRAGMENT_AND_NESTED_FIELDS,
                Map.of("id", movieId)
        );

        assertSuccessfulGraphQlResponse(response);

        JsonNode movie = response.body().path("data").path("movie");
        JsonNode movieData = movie.path("federateMovie").path("data");

        assertThat(movie.path("id").asText()).isEqualTo(movieId);
        assertThat(movieData.path("Title").asText()).isNotBlank();
        assertThat(movieData.path("Genre").isMissingNode()).isFalse();
        assertThat(movieData.path("Director").isMissingNode()).isFalse();
    }

    private String existingMovieId() {
        ApiResponse<JsonNode> response = client.execute(
                GraphQlQueries.MOVIES_WITH_PAGINATION,
                Map.of("first", 1, "skip", 0)
        );

        assertSuccessfulGraphQlResponse(response);

        JsonNode movies = response.body().path("data").path("movies");
        assertThat(movies.isArray()).isTrue();
        assertThat(movies.size()).isGreaterThan(0);

        return movies.get(0).path("id").asText();
    }

    private void assertSuccessfulGraphQlResponse(ApiResponse<JsonNode> response) {
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.body()).isNotNull();
        assertThat(response.body().path("errors").isMissingNode()
                || response.body().path("errors").isNull()).isTrue();
        assertThat(response.body().path("data").isObject()).isTrue();
    }
}
