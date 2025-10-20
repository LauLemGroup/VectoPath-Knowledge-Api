package com.laulem.vectopath.client.service;

import com.laulem.vectopath.client.dto.CreateResourceRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service léger de la couche client pour parser les requêtes HTTP
 * et les transformer en appels vers la couche business
 */
@Service
public class ResourceRequestAdapter {

    /**
     * Adapte une requête multipart en paramètres pour la couche business
     */
    public CreateResourceRequest adaptMultipartRequest(MultipartFile file, CreateResourceRequest existingRequest) {
        CreateResourceRequest request = new CreateResourceRequest();
        request.setSourceType(CreateResourceRequest.SourceType.FILE);

        // Utilise les données de la requête existante si elles sont fournies
        if (existingRequest != null) {
            request.setName(existingRequest.getName());
            request.setMetadata(existingRequest.getMetadata());
            request.setContentType(existingRequest.getContentType());
        }

        // Si pas de nom spécifié, utiliser le nom du fichier
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            request.setName(file.getOriginalFilename());
        }

        return request;
    }
}
