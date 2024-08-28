/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.pba.translator.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.ericsson.component.aia.sdk.pba.model.PBAInstance;
import com.ericsson.component.aia.sdk.pba.tools.PBASchemaTool;
import com.ericsson.component.aia.sdk.pba.translator.DeploymentBuilderFactory;
import com.ericsson.component.aia.sdk.pba.translator.KubernetesManifestBuilder;
import com.ericsson.component.aia.sdk.pba.translator.kube.dto.JobDTO;
import com.ericsson.component.aia.sdk.pba.translator.metadata.*;

public class KubernetesManifestBuilderTest extends AsbtractManifestTest {

    private final File expectedJobYAMLFile = new File("src/test/resources/job-deployment.yaml");;

    @Test
    public void testPBAConversionToKubernetesJob() {
        try {
            final PBAInstance instance = new PBASchemaTool().getPBAModelInstance(readFileAsString(new File("src/test/resources/pba-good.json")));
            final KubernetesManifestBuilder kubernetesBuidler = (KubernetesManifestBuilder) DeploymentBuilderFactory.getBuilderFor(
                    OrchestratorType.KUBERNETES, new DeploymentMetaData(JobType.BATCH, DeploymentType.SCHEDULE, JobPolicy.RESTART_ON_FAILURE));
            final JobDTO job = kubernetesBuidler.buildJob(instance, "volvo-blwarning-test");
            job.getJob().setApiVersion("batch/v1");

            assertEquals("Should have been batch/v1", "batch/v1", job.getJob().getApiVersion());
            assertEquals("Kind should have been Job", "Job", job.getJob().getKind());

            final String jobAsYaml = job.getJobSpecAsYaml();
            final String expectedYaml = readFileAsString(expectedJobYAMLFile);
            assertThat(jobAsYaml, equalToIgnoringWhiteSpace(expectedYaml));

        } catch (final IOException ioe) {
            fail("IOException unexpected: " + ioe);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testExpectedUnsupportedDeploymentTypeException() {
        try {
            final PBAInstance instance = new PBASchemaTool().getPBAModelInstance(readFileAsString(new File("src/test/resources/pba-good.json")));
            final KubernetesManifestBuilder kubernetesBuidler = (KubernetesManifestBuilder) DeploymentBuilderFactory.getBuilderFor(
                    OrchestratorType.KUBERNETES,
                    new DeploymentMetaData(JobType.BATCH, DeploymentType.STANDALONE_CLUSTER, JobPolicy.RESTART_ON_FAILURE));
            final JobDTO job = kubernetesBuidler.buildJob(instance, "volvo-blwarning-test");
            job.getJob().setApiVersion("batch/v1");
        } catch (final IOException ioe) {
            fail("IOException unexpected: " + ioe);
        }
    }

    @Test
    public void testBuilderReturnsNullForUnspportedOrchestratorTypes() {
        final KubernetesManifestBuilder kubernetesBuidler = (KubernetesManifestBuilder) DeploymentBuilderFactory.getBuilderFor(
                OrchestratorType.BAREMETAL, new DeploymentMetaData(JobType.STREAMING, DeploymentType.SCHEDULE, JobPolicy.RESTART_ON_FAILURE));
        assertNull("Builder should have been null:", kubernetesBuidler);
    }

}
