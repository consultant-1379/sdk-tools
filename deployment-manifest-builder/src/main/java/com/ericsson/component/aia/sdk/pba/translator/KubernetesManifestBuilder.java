package com.ericsson.component.aia.sdk.pba.translator;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.ericsson.component.aia.sdk.pba.model.*;
import com.ericsson.component.aia.sdk.pba.translator.kube.dto.JobDTO;
import com.ericsson.component.aia.sdk.pba.translator.metadata.*;

import io.fabric8.kubernetes.api.model.*;

/**
 * Builder class that provides utilities to generate a Kubernetes Manifest from a pba json file.
 */
public class KubernetesManifestBuilder implements DeploymentManifestBuilder {

    final DeploymentMetaData metaData;

    Namespace namespace;

    /**
     * Constructs the builder object with the supplied {@link DeploymentMetaData}
     *
     * @param mData
     *            - meta data to be used by this builder object.
     */
    public KubernetesManifestBuilder(final DeploymentMetaData mData) {
        metaData = mData;
    }

    /**
     * Builds a {@code JobDTO} that encapsulates a Kubernetes {@code Job}
     *
     * @param pba
     *            - pba instance from which the kubernetes job has to be built.
     * @param jobName
     *            - the name of the job that should be used in the kubernetes cluster.
     * @return - {@code JobDTO} - encompassing a Kubernetes Job.
     */
    public JobDTO buildJob(final PBAInstance pba, final String jobName) {
        if (metaData.getDeployType() == DeploymentType.SCHEDULE) {
            return createJobSpecForSchedule(pba, jobName);
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Generates a kubernetes Job Spec.
     *
     * @param pba
     *            - pba instance from which the kubernetes job has to be built.
     * @return - {@link PodTemplateSpec} - the spec.
     */
    PodTemplateSpec getJobTemplateSpec(final PBAInstance pba) {

        final io.fabric8.kubernetes.api.model.Container container = new io.fabric8.kubernetes.api.model.Container();
        final Docker docker = pba.getPba().getBuildInfo().getContainer().getDocker();
        container.setImage(docker.getRepoBaseUrl() + "/" + docker.getRepoPath() + "/" + docker.getImagePath());
        if (docker.getForcePullImage()) {
            container.setImagePullPolicy("Always");
        }

        container.setName(docker.getName());

        final List<io.fabric8.kubernetes.api.model.Container> containers = new ArrayList<>();
        containers.add(container);

        //get env args
        final List<EnvVar> listEnvVars = getContainerEnvironmentArgs(pba.getPba().getDeploymentInfo().getEnvArgs());

        // volumes to be mounted from hosts
        final List<Volume> volumes = new ArrayList<>();
        //set container mount paths
        container.setVolumeMounts(getContainerMountPaths(docker.getMountPaths(), volumes));

        container.setEnv(listEnvVars);
        final PodSpec podSpec = new PodSpec();
        podSpec.setContainers(containers);
        //set volumes for the pod that will be shared by the container
        /**
         * @TODO - Should refactor to get this volume information through pba inline with kubernetes schema for volume.
         */

        podSpec.setVolumes(volumes);

        if (metaData.getJobPolicy() == JobPolicy.RESTART_ON_FAILURE) {
            podSpec.setRestartPolicy("OnFailure");
        } else if (metaData.getJobPolicy() == JobPolicy.NEVER_RESTART) {
            podSpec.setRestartPolicy("Never");
        }
        final PodTemplateSpec temple = new PodTemplateSpec();
        temple.setSpec(podSpec);
        return temple;
    }

    private List<EnvVar> getContainerEnvironmentArgs(final List<Arg> envArgs) {
        final List<EnvVar> listEnvVars = new ArrayList<>();
        for (final Arg arg : envArgs) {
            final EnvVarBuilder envBuilder = new EnvVarBuilder();
            envBuilder.withName(arg.getKey());
            envBuilder.withValue(arg.getValue().toString());
            listEnvVars.add(envBuilder.build());
        }
        return listEnvVars;
    }

    private List<VolumeMount> getContainerMountPaths(final Set<String> mountPathSet, final List<Volume> volumes) {
        final List<VolumeMount> mountPathList = new ArrayList<>();
        //set mount volumes
        for (final String mountPath : mountPathSet) {
            final VolumeMount mountP = new VolumeMountBuilder().withMountPath(mountPath).withReadOnly(true).build();
            /**
             * @TODO - Should refactor to get this volume information through pba inline with kubernetes schema for volume.
             */
            mountP.setName("test-vols");
            mountPathList.add(mountP);
            /**
             * @TODO - Should refactor the below to ensure that the volumes are build separate outside the mount paths.
             */
            volumes.add(new VolumeBuilder().withName("test-vols").withNewHostPath(mountPath).build());
        }
        return mountPathList;
    }

    private JobDTO createJobSpecForSchedule(final PBAInstance pba, final String jobName) {
        final JobBuilder jobBuilder = new JobBuilder();
        jobBuilder.withNewMetadata().withName(StringUtils.lowerCase(jobName)).endMetadata().withNewSpec().withTemplate(getJobTemplateSpec(pba))
                .endSpec();
        return new JobDTO(jobBuilder.build());
    }

}
