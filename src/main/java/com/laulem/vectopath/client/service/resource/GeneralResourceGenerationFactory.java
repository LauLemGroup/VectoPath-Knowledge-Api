package com.laulem.vectopath.client.service.resource;

import com.laulem.vectopath.client.exception.UnsupportedSourceTypeException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GeneralResourceGenerationFactory {

    private final Map<String, GeneralResourceGeneration> generationMap;

    public GeneralResourceGenerationFactory(List<GeneralResourceGeneration> generations) {
        this.generationMap = generations.stream().collect(Collectors.toMap(GeneralResourceGeneration::getSourceType, Function.identity()));
    }

    public GeneralResourceGeneration getResourceGeneration(String sourceType) {
        GeneralResourceGeneration generation = generationMap.get(sourceType.toUpperCase());
        if (generation == null) {
            throw new UnsupportedSourceTypeException(sourceType);
        }
        return generation;
    }
}
