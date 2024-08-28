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
package com.ericsson.component.aia.sdk.pba.utilities;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ericsson.component.aia.sdk.pba.model.PBAInstance;
import com.ericsson.component.aia.sdk.pba.report.ReportLevel;
import com.ericsson.component.aia.sdk.pba.report.ValidatedMessage;
import com.ericsson.component.aia.sdk.pba.report.ValidationReport;
import com.ericsson.component.aia.sdk.pba.tools.PBASchemaTool;

public class PbaSchemaToolTest {

    private static File pbaBadJsonData;

    private static File pbaGoodJsonData;

    private static PBASchemaTool schemaTool;

    @BeforeClass
    public static void loadResources() throws IOException {
        final String pbaAsJson = IOUtils.toString(PbaSchemaToolTest.class.getClassLoader().getResourceAsStream("pba-schema.json"), "UTF-8");
        pbaBadJsonData = new File("src/test/resources/pba-bad.json");
        pbaGoodJsonData = new File("src/test/resources/pba-good.json");
        schemaTool = new PBASchemaTool(pbaAsJson);
    }

    @Test
    public void testSchemaIsLoadedFromClassPath() throws IOException {
        getClass().getResource("pba-schema.json");
        final PBASchemaTool schemaTool = new PBASchemaTool();
        final ValidationReport report = schemaTool.validatePbaFileAgainstSchema(pbaBadJsonData.toPath());
        assertEquals("Expected 3 error messages", 3, report.getLogLevelMessages(ReportLevel.ERROR).size());
    }

    @Test
    public void testSchemaToolValidatesPBADataFile() throws IOException {
        final ValidationReport report = schemaTool.validatePbaFileAgainstSchema(pbaBadJsonData.toPath());
        assertEquals("Expected 3 error messages", 3, report.getLogLevelMessages(ReportLevel.ERROR).size());
    }

    @Test
    public void testSchemaToolValidatesPBAData() throws IOException {
        final Set<ValidatedMessage> report = schemaTool.validatePbaAgainstSchema(readFileAsString(pbaBadJsonData))
                .getLogLevelMessages(ReportLevel.ERROR);
        assertEquals("Expected 3 error messages", 3, report.size());
    }

    @Test
    public void testSchemaToolReportsAllMessages() throws IOException {
        final ValidationReport report = schemaTool.validatePbaAgainstSchema(readFileAsString(pbaBadJsonData));
        assertEquals("Expected 425 messages", 425, report.getMessages().size());
    }

    @Test
    public void testSchemaToolReportsOnlyWarnings() throws IOException {
        final ValidationReport report = schemaTool.validatePbaAgainstSchema(readFileAsString(pbaBadJsonData));
        assertEquals("Expected 422 warning messages", 422, report.getLogLevelMessages(ReportLevel.WARNING).size());
    }

    @Test
    public void testSchemaToolReportsNoErrorsForGoodPBA() throws IOException {
        final Set<ValidatedMessage> mesgs = schemaTool.validatePbaAgainstSchema(readFileAsString(pbaGoodJsonData))
                .getLogLevelMessages(ReportLevel.ERROR);
        assertEquals("Expected NO error messages", 0, mesgs.size());
    }

    @Test
    public void testSchemaToolGetsPBAInstance() throws IOException {
        final String expectedAppName = "Test-Application-Name";
        final PBAInstance pbaInstance = schemaTool.getPBAModelInstance(readFileAsString(pbaGoodJsonData));
        assertEquals(pbaInstance.getPba().getApplicationInfo().getName(), expectedAppName);
    }

    private static String readFileAsString(final File file) throws IOException {
        return new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);
    }

}
