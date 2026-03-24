package com.demo.spring.ai.rag;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.extern.log4j.Log4j2;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Uses Tavily AI Search to fetch the latest info than LLM built.
 *
 */
@Log4j2
public class TavilyDocumentRetriever implements DocumentRetriever {

    private final Integer maxResults;
    private final RestClient restClient;

    // https://docs.tavily.com/documentation/api-reference/endpoint/search
    // so accord to tavily layout to establish the record
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record TavilyRequestPayload(String query, String searchDepth, int maxResults) {}

    record TavilyResponse(List<Hit> results) {
        record Hit(String title, String url, String content, Double score) {}
    }

    private TavilyDocumentRetriever (Integer maxResults) {
        this.maxResults = maxResults;
        this.restClient = RestClient.builder()
                .baseUrl("https://api.tavily.com/search")
                .defaultHeader("Authorization", "Bearer " + System.getenv("TAVILY_API_KEY"))
                .build();
    }



    @Override
    public List<Document> retrieve(Query query) {

        // 1. fetch the info from Tavily via the query
        TavilyResponse tavilyResponse =
                                        restClient.post().
                                        body(new TavilyRequestPayload(query.text(), "advanced", maxResults)).
                                        retrieve().
                                        onStatus(HttpStatusCode::is5xxServerError,
                                                (request, response) -> { throw new RuntimeException(response.toString());}).
                                        body(TavilyResponse.class);

        // 2. Build AI Document List
        if(tavilyResponse == null || CollectionUtils.isEmpty(tavilyResponse.results)) {
            return Collections.EMPTY_LIST;
        } else {
            return tavilyResponse.results.stream().map(hit ->
                    Document.builder().
                    text(hit.content).
                    score(hit.score).
                    metadata("title", hit.title()).
                    metadata("url", hit.url()).
                    build()
            ).collect(Collectors.toList());
        }
    }



    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Integer maxResults;

        public Builder maxResults(Integer maxResults) {
            this.maxResults = maxResults;
            return this;
        }

        public TavilyDocumentRetriever build() {
            return new TavilyDocumentRetriever(this.maxResults);
        }
    }
}
