package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.service.ResourceService;
import com.laulem.vectopath.business.service.TextResourceCreationService;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service de création de ressources depuis du texte
 */
@Service
public class TextResourceCreationServiceImpl implements TextResourceCreationService {
    public static final String CONTENT_TYPE = "text/plain";
    private final ResourceService resourceService;

    public TextResourceCreationServiceImpl(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public Resource createFromText(String name, String content, String metadata) {
        validateInput(name, content);
        return resourceService.createResource(name.trim(), content.trim(), CONTENT_TYPE, metadata);
    }

    private void validateInput(String name, String content) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom de la ressource est obligatoire");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Le contenu est obligatoire pour une ressource de type TEXT");
        }
    }
}
