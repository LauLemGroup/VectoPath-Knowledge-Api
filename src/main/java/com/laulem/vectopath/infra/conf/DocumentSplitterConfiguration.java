package com.laulem.vectopath.infra.conf;

import com.laulem.vectopath.business.service.splitter.DocumentSplitter;
import com.laulem.vectopath.business.service.splitter.DocumentSplitterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DocumentSplitterConfiguration {
    @Bean
    public DocumentSplitterFactory documentSplitterFactory(List<DocumentSplitter> splitters) {
        return new DocumentSplitterFactory(splitters);
    }
}
