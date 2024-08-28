package com.ericsson.component.aia.sdk.pba.translator.kube.dto;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.fabric8.kubernetes.api.model.Job;
import io.fabric8.kubernetes.client.internal.SerializationUtils;

/**
 * Class encompasses a {@link Job}
 */
public class JobDTO {

    final Job job;

    /**
     * Constructs a Job that encompasses a {@link Job}.
     *
     * @param job
     *            - the job
     */
    public JobDTO(final io.fabric8.kubernetes.api.model.Job job) {
        this.job = job;
    }

    /**
     * Gets the Job that was generated from the pba.
     *
     * @return - {@link Job}
     */
    public Job getJob() {
        return job;
    }

    /**
     * Gets this Job as kubernetes Yaml file.
     *
     * @return - kubernetes yaml manifest as String.
     * @throws JsonProcessingException
     *             - if the processing of the pba instance fails.
     */
    public String getJobSpecAsYaml() throws JsonProcessingException {
        return SerializationUtils.dumpAsYaml(job);
    }

}
