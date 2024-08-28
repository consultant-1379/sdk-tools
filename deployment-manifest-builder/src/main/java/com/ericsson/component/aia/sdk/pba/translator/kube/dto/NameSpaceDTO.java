package com.ericsson.component.aia.sdk.pba.translator.kube.dto;

import java.util.Map;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;

/**
 * Creates a NameSpace spec in kubernetes YAML format.
 *
 * @author qchavar
 *
 */
public class NameSpaceDTO {

    final String name;

    final Namespace nameSpace;

    final Map<String, String> labels;

    /**
     * Constructs a {@link Namespace} using the name. Clients can use this to create namespaces on the kubernetes cluster.
     *
     * @param name
     *            - name of the namespace.
     * @param labls
     *            - Name/Value labels that will be supplied as Labels to the Namespace.
     */
    public NameSpaceDTO(final String name, final Map<String, String> labls) {
        this.name = name;
        labels = labls;
        nameSpace = new NamespaceBuilder().withNewMetadata().withName(name).addToLabels(labels).endMetadata().build();
    }

    /**
     * Gets the name of this namespace.
     *
     * @return - namespace name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the labels used for this NameSpace.
     *
     * @return - {@link Map<String, String>} - map of name/value pairs as labels.
     */
    public Map<String, String> getLabels() {
        return labels;
    }

    /**
     * Gets the kubernetes Namespace object.
     *
     * @return - {@link Namespace}
     */
    public Namespace getNameSpace() {
        return nameSpace;
    }
}
