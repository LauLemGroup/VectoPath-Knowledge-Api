package com.laulem.vectopath.business.service.impl;

import com.laulem.vectopath.business.model.Resource;
import com.laulem.vectopath.business.model.ResourceStatus;
import com.laulem.vectopath.business.repository.ResourceRepository;
import com.laulem.vectopath.business.service.ResourceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implémentation du service de gestion des ressources
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);

    private final ResourceRepository resourceRepository;
    private final VectorizedResourceService vectorizedResourceService;

    public ResourceServiceImpl(ResourceRepository resourceRepository,
                              VectorizedResourceService vectorizedResourceService) {
        this.resourceRepository = resourceRepository;
        this.vectorizedResourceService = vectorizedResourceService;
    }

    @Override
    public Resource createResource(String name, String content, String contentType, String metadata) {
        Resource resource = new Resource(name, content, contentType, metadata);
        resource = resourceRepository.save(resource);
        processResourceVectorization(resource);

        return resource;
    }

    @Override
    public Optional<Resource> getResourceById(UUID id) {
        return resourceRepository.findById(id);
    }

    @Override
    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    @Override
    public List<Resource> getResourcesByStatus(ResourceStatus status) {
        return resourceRepository.findByStatus(status);
    }

    @Override
    public List<Resource> searchResourcesByName(String name) {
        return resourceRepository.findByNameContaining(name);
    }

    @Override
    public Resource updateResourceStatus(UUID id, ResourceStatus status) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ressource non trouvée : " + id));

        resource.setStatus(status);
        return resourceRepository.save(resource);
    }

    @Override
    public void deleteResource(UUID id) {
        logger.info("Suppression de la ressource : {}", id);

        // Supprime d'abord tous les chunks vectorisés
        vectorizedResourceService.deleteResource(id);

        // Puis supprime la ressource des métadonnées
        resourceRepository.deleteById(id);
    }

    @Override
    public Resource reprocessResource(UUID id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ressource non trouvée : " + id));

        logger.info("Relance du traitement de la ressource : {}", resource.getName());

        // Supprime les anciens chunks vectorisés
        vectorizedResourceService.deleteResource(id);

        // Relance la vectorisation
        processResourceVectorization(resource);

        return resource;
    }

    /**
     * Traite la vectorisation d'une ressource avec VectorizedResourceService
     * Basé sur l'approche de RagConfig et CustomVectorStoreWithAccessControl
     */
    private void processResourceVectorization(Resource resource) {
        try {
            resource.setStatus(ResourceStatus.PROCESSING);
            resourceRepository.save(resource);

            // Vérifier si la ressource est déjà chargée (comme dans RagConfig)
            if (vectorizedResourceService.isResourceAlreadyLoaded(resource.getId())) {
                logger.info("Ressource [{}] déjà chargée dans le vector store", resource.getName());
                resource.setStatus(ResourceStatus.VECTORIZED);
                resourceRepository.save(resource);
                return;
            }

            // Utiliser VectorizedResourceService pour le découpage, la vectorisation et le stockage
            // (basé sur TokenTextSplitter de RagConfig + approche CustomVectorStoreWithAccessControl)
            vectorizedResourceService.addResource(resource);

            // Met à jour le statut de succès
            resource.setStatus(ResourceStatus.VECTORIZED);
            resourceRepository.save(resource);

            logger.info("Vectorisation terminée avec succès pour la ressource : {}", resource.getName());

        } catch (Exception e) {
            logger.error("Erreur lors de la vectorisation de la ressource : {}", resource.getName(), e);

            // Met à jour le statut d'erreur
            resource.setStatus(ResourceStatus.ERROR);
            resourceRepository.save(resource);
        }
    }


}
