package com.laulem.vectopath.infra.conf;

import com.laulem.vectopath.business.service.splitter.DocumentSplitter;
import com.laulem.vectopath.business.service.splitter.DocumentSplitterFactory;
import com.laulem.vectopath.infra.service.DefaultTokenTextSplitter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DocumentSplitterConfiguration {

    @Bean("defaultDocumentSplitter")
    @ConditionalOnMissingBean(name = "defaultDocumentSplitter")
    public DocumentSplitter defaultDocumentSplitter() {
        return new DefaultTokenTextSplitter();
    }

    @Bean
    public DocumentSplitterFactory documentSplitterFactory(List<DocumentSplitter> splitters) {
        return new DocumentSplitterFactory(splitters);
    }
}
