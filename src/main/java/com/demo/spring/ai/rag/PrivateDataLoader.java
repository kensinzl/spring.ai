package com.demo.spring.ai.rag;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
public class PrivateDataLoader {

    private final VectorStore vectorStore;


    private final Resource policyFile;

    @Autowired
    public PrivateDataLoader(VectorStore vectorStore, @Value("classpath:/promptTemplate/HR_Policies.pdf") Resource policyFile) {
        this.vectorStore = vectorStore;
        this.policyFile = policyFile;
    }

    @PostConstruct
    public void init() {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(policyFile);
        List<Document> docs = tikaDocumentReader.get();// just one, the package code wraps into list
        TextSplitter textSplitter =
                TokenTextSplitter.builder().withChunkSize(200).withMaxNumChunks(400).build();
        vectorStore.add(textSplitter.split(docs));
    }
}
