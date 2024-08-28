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
package com.ericsson.component.aia.sdk.pba.tools;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.component.aia.sdk.pba.exception.InvalidPbaException;
import com.ericsson.component.aia.sdk.pba.model.PBAInstance;
import com.ericsson.component.aia.sdk.pba.report.ReportLevel;
import com.ericsson.component.aia.sdk.pba.report.ValidatedMessage;
import com.ericsson.component.aia.sdk.pba.report.ValidationReport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.Dereferencing;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * PBA utility that validates the pba schema against the pba instance.
 */
public class PBASchemaTool {

    private static final String EXCEPTION_MESSAGE = "Exception occurred while creating PBAInstance";

    /**
     * Logger for schema tool.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PBASchemaTool.class);

    private static final String PBA_SCHEMA_IN_CLASSPATH = "pba-schema.json";

    /**
     * Used to denote if the schema has to be loaded from the classpath.
     */
    private final boolean loadSchemaFromClasspath;

    /**
     * The json pba schema as {@code String} that this tool works on.
     */
    private String pbaSchema;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * The default constructor to use if the schema has to be loaded from the classpath. The file "pba-schema.json" is the file resource expected in
     * the classpath.
     */
    public PBASchemaTool() {
        loadSchemaFromClasspath = true;
    }

    /**
     * Constructor that instantiates the schema tool with jsonSchema supplied as String.
     *
     * @param pbaSchema
     *            - the pba schema as {@code String}
     */
    public PBASchemaTool(final String pbaSchema) {
        loadSchemaFromClasspath = false;
        this.pbaSchema = pbaSchema;
    }

    /**
     * Method returns instance of the model from the pba json data supplied.
     *
     * @param pbaData
     *            - the pba data as json.
     * @return {@code PBAInstance} if data is valid against the schema, null - if the data contains errors.
     *
     * @throws InvalidPbaException
     *             - if the PBA could not be processed.
     */
    public PBAInstance getPBAModelInstance(final String pbaData) {
        try {
            final ValidationReport report = validatePbaAgainstSchema(pbaData);
            final Set<ValidatedMessage> errorMessages = report.getLogLevelMessages(ReportLevel.ERROR);
            if (!errorMessages.isEmpty()) {
                throw new InvalidPbaException(errorMessages.toString());
            }
            return objectMapper.readValue(pbaData, PBAInstance.class);
        } catch (final IOException exp) {
            throw new InvalidPbaException("could not parse pbaData", exp);
        }
    }

    /**
     * This method will convert PBAInstance to JSON string.
     *
     * @param pbaInstance
     *            to convert to JSON string
     * @return as JSON string.
     */
    public String convertToJsonString(final PBAInstance pbaInstance) {
        try {
            return objectMapper.writeValueAsString(pbaInstance);
        } catch (final JsonProcessingException exp) {
            throw new InvalidPbaException("could not convert to json string", exp);
        }
    }

    /**
     * Method validates the json pba data against the pba schema.
     *
     * @param pbaData
     *            - the pba data as json.
     * @return - A Set of {@code ValidatedMessage} if this pba data contains invalid attributes, otherwise an empty set.
     * @throws InvalidPbaException
     *             - if the PBA could not be processed.
     */
    public ValidationReport validatePbaAgainstSchema(final String pbaData) {
        try {
            final String pbaSchema = getSchema();
            final JsonNode schemaNode = objectMapper.readTree(pbaSchema);
            final LoadingConfigurationBuilder builder = LoadingConfiguration.newBuilder();
            builder.dereferencing(Dereferencing.INLINE).freeze();
            final JsonSchemaFactory schemaFact = JsonSchemaFactory.newBuilder().setLoadingConfiguration(builder.freeze()).freeze();
            final JsonNode jsonNode = objectMapper.readTree(pbaData);
            final ProcessingReport processingReport = schemaFact.getJsonSchema(schemaNode).validate(jsonNode, true);
            LOGGER.debug(processingReport.isSuccess() + "" + processingReport);
            return getValidationReport(processingReport);
        } catch (final IOException | ProcessingException exp) {
            throw new InvalidPbaException(EXCEPTION_MESSAGE, exp);
        }
    }

    /**
     * This method is used to get a String as a JSON Schema.
     *
     * @return The JSON Scheme as a String.
     * @throws IOException
     *             Exception thrown if there is a problem read the schema file.
     */
    public String getSchema() throws IOException {
        if (loadSchemaFromClasspath) {
            this.pbaSchema = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(PBA_SCHEMA_IN_CLASSPATH), "UTF-8");
        }
        return this.pbaSchema;
    }

    /**
     * Method validates the json pba file against the pba schema.
     *
     * @param jsonDataFilePath
     *            - the absolute path of the pba file to check.
     * @return - A Set of {@code ValidatedMessage} if this pba data contains invalid attributes, otherwise an empty set.
     * @throws InvalidPbaException
     *             - if the file could not be processed.
     */
    public ValidationReport validatePbaFileAgainstSchema(final Path jsonDataFilePath) {
        try {
            final String jsonData = readStreamAsStringFronFile(jsonDataFilePath);
            return validatePbaAgainstSchema(jsonData);
        } catch (final IOException exp) {
            throw new InvalidPbaException(EXCEPTION_MESSAGE, exp);
        }
    }

    private ValidationReport getValidationReport(final ProcessingReport processingReport) {
        final Iterator<ProcessingMessage> messagesIter = processingReport.iterator();

        final Set<ValidatedMessage> validationMessages = new HashSet<>();
        while (messagesIter.hasNext()) {
            final ProcessingMessage message = messagesIter.next();
            final ValidatedMessage vMessage = new ValidatedMessage();
            vMessage.setMessage(message.getMessage());
            vMessage.setDetailedMessage(message.toString());
            vMessage.setReportLevel(Utils.getReportLevel(message.getLogLevel()));
            validationMessages.add(vMessage);
        }

        return new ValidationReport(validationMessages);
    }

    /**
     * Utility main to run as standalone.
     *
     * @param args
     *            - pba json schema file, and pba.json file are expcted.
     * @throws IOException
     *             - if the file could not be read.
     * @throws ProcessingException
     *             - if the file could not be parsed/loaded.
     * @throws URISyntaxException
     *             - if the resource could not be loaded from the URI.
     */
    public static void main(final String... args) throws IOException, URISyntaxException {

        if ((args != null && args.length != 2) || args == null) {
            LOGGER.info("Ensure to supply absolute path to pba json schema file, and pba.json file");
            System.exit(0);
        }

        final Path jsonSchemaPath = Paths.get(ClassLoader.getSystemClassLoader().getResource("pba-schema.json").toURI());
        final String jsonDataPath = args[1];

        final PBASchemaTool pbaTool = new PBASchemaTool(readStreamAsStringFronFile(jsonSchemaPath));
        pbaTool.getPBAModelInstance(readStreamAsStringFronFile(new File(jsonDataPath).toPath()));

    }

    /**
     * File Conversion utility. Reads a file and converts it into {@code String}
     *
     * @param fileName
     *            - absolute path to the file.
     * @return - String representation of the contents of the file.
     * @throws IOException
     *             - If the file could not be read.
     */
    public static String readStreamAsStringFronFile(final Path fileName) throws IOException {
        return new String(Files.readAllBytes(fileName), StandardCharsets.UTF_8);
    }

}
