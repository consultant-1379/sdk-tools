package com.ericsson.component.aia.sdk.pba.translator.test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import com.ericsson.component.aia.sdk.pba.model.PBAInstance;
import com.ericsson.component.aia.sdk.pba.tools.PBASchemaTool;
import com.ericsson.component.aia.sdk.pba.translator.DeploymentBuilderFactory;
import com.ericsson.component.aia.sdk.pba.translator.KubernetesManifestBuilder;
import com.ericsson.component.aia.sdk.pba.translator.kube.dto.JobDTO;
import com.ericsson.component.aia.sdk.pba.translator.kube.dto.NameSpaceDTO;
import com.ericsson.component.aia.sdk.pba.translator.metadata.*;

import io.fabric8.kubernetes.client.*;

public class DeploymentSDKTestClient {

    /**
     * Test kubernetes-client. Example of usage.
     *
     * @param args
     *            - args supplied through command line
     * @throws IOException
     */
    public static void main(final String... args) throws IOException {

        final PBAInstance instance = new PBASchemaTool().getPBAModelInstance(
                readFileAsString(new File("/home/qchavar/git-repo/AIA/sdk-tools/deployment-manifest-builder/src/test/resources/pba-good.json")));

        final KubernetesManifestBuilder kubernetesBuidler = (KubernetesManifestBuilder) DeploymentBuilderFactory.getBuilderFor(
                OrchestratorType.KUBERNETES, new DeploymentMetaData(JobType.BATCH, DeploymentType.SCHEDULE, JobPolicy.RESTART_ON_FAILURE));

        final JobDTO job = kubernetesBuidler.buildJob(instance, "volvo-blwarning-test");
        job.getJob().setApiVersion("batch/v1");
        System.out.println(job.getJobSpecAsYaml());

        final String master = "https://192.168.99.100:8443/";
        final Config config = new ConfigBuilder().withMasterUrl(master).withApiVersion("extensions/v1beta1").build();
        final KubernetesClient client = new DefaultKubernetesClient(config);

        //create or replace namespace in cluster
        final NameSpaceDTO nameSpaceDto = new NameSpaceDTO("test-varaga", new HashMap<>());

    }

    private static String readFileAsString(final File file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
    }

}
