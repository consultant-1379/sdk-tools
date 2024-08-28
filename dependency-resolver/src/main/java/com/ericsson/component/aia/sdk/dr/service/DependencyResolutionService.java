package com.ericsson.component.aia.sdk.dr.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ericsson.aia.metadata.api.MetaDataServiceIfc;
import com.ericsson.aia.metadata.exception.MetaDataServiceException;
import com.ericsson.aia.metadata.model.MetaData;
import com.ericsson.component.aia.sdk.dr.exception.DependencyResolutionException;

/**
 * Service Providing logical dependencies resolution for applications and services.
 * @author EMOIBMO
 *
 */
public class DependencyResolutionService {
    private final static String DEPENDENCIES_SCHEMA_NAME = "dependencies-graph";
    private final MetaDataServiceIfc metadataServiceRef;
    /**
     * Service Constructor setting the reference for the Metadata Service.
     * @param metadataServiceRef
     *            Metadata Service Reference.
     */
    public DependencyResolutionService(final MetaDataServiceIfc metadataServiceRef) {
        this.metadataServiceRef = metadataServiceRef;
    }

    /**
     * Retrieves a complete dependency graph for an Application/Service PBA by Id.
     * @param pbaId
     *            Application or Service Id
     * @return dependency graph
     * @throws DependencyResolutionException
     *             DependencyResolutionServiceException
     */
    public List<String> getDependencies(final String pbaId) throws DependencyResolutionException {

        try {

            if (pbaId == null || pbaId.trim().equalsIgnoreCase("")) {
                throw new DependencyResolutionException("A valid pba id must be specified. The id cannot be be null or empty");
            }

            //-- Verify that the 'dependencies-graph' collection exists in the database.
            if (!metadataServiceRef.schemaExists(DEPENDENCIES_SCHEMA_NAME)) {
                throw new DependencyResolutionException("There is no schema defined in the Metadata store for '" + DEPENDENCIES_SCHEMA_NAME + "'");
            }

            final ArrayList<String> filteredDependencies = (ArrayList<String>) getDependenciesTree(pbaId, new ArrayList<String>());

            final Set<String> hashSet = new HashSet<>();
            hashSet.addAll(filteredDependencies);
            filteredDependencies.clear();
            filteredDependencies.addAll(hashSet);

            return filteredDependencies;

        } catch (MetaDataServiceException e) {
            e.printStackTrace();
            throw new DependencyResolutionException(e.getMessage());
        }

    }

    private List<String> getDependenciesTree(final String application, final List<String> alreadyVisited) throws MetaDataServiceException {

        final ArrayList<String> applications = new ArrayList<String>();

        alreadyVisited.add(application);

        final List<String> tempDependencies = new ArrayList<String>();

        final ArrayList<MetaData> dependenciesTree = metadataServiceRef.findAll("dependencies-graph");
        for (final Iterator iterator = dependenciesTree.iterator(); iterator.hasNext();) {
            final MetaData metaData = (MetaData) iterator.next();
            if (!metaData.getKey().trim().equalsIgnoreCase(application)) {
                iterator.remove();
            }
        }

        for (final MetaData metaData : dependenciesTree) {
            tempDependencies.add(metaData.getValue());
        }

        if (!tempDependencies.isEmpty()) {
            applications.addAll(tempDependencies);
            for (final String dependency : tempDependencies) {
                if (!alreadyVisited.contains(dependency)) {
                    applications.addAll(getDependenciesTree(dependency, alreadyVisited));
                }
            }
        }

        return applications;
    }
}
