package com.laulem.vectopath.client.config;

import com.laulem.vectopath.business.service.ResourceService;
import com.laulem.vectopath.client.service.resource.FileResourceGeneration;
import com.laulem.vectopath.client.service.resource.TxtFileResourceGeneration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class FileResourceGenerationConfig {
    @Bean("defaultFileResourceGeneration")
    @ConditionalOnMissingBean(name = "defaultFileResourceGeneration")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public FileResourceGeneration defaultFileResourceGeneration(ResourceService resourceService) {
        return new TxtFileResourceGeneration(resourceService);
    }
}
