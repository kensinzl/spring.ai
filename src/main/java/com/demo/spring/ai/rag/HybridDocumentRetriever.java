package com.demo.spring.ai.rag;


import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This supports 1) local Qdrant and 2)Tavily fetch
 *
 */
@Component
public class HybridDocumentRetriever implements DocumentRetriever {

    private final TavilyDocumentRetriever tavilyDocumentRetriever;
    private final VectorStoreDocumentRetriever vectorStoreDocumentRetriever;

    @Autowired
    public HybridDocumentRetriever(VectorStore vectorStore) {
        this.tavilyDocumentRetriever =
                TavilyDocumentRetriever.
                builder().
                maxResults(3).
                build();

        this.vectorStoreDocumentRetriever =
                VectorStoreDocumentRetriever.
                        builder().
                        vectorStore(vectorStore).
                        topK(3).
                        similarityThreshold(0.5).
                        build();
    }



    @Override
    public List<Document> retrieve(Query query) {
        List<Document> finalResult = new ArrayList<>();

        List<Document> localDocuments = vectorStoreDocumentRetriever.retrieve(query);
        List<Document> webLatestDocuments = tavilyDocumentRetriever.retrieve(query);

        finalResult.addAll(localDocuments);
        finalResult.addAll(webLatestDocuments);

        return finalResult;
    }
}
