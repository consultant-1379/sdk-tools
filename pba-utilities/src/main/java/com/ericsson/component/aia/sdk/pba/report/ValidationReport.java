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
package com.ericsson.component.aia.sdk.pba.report;

import java.util.HashSet;
import java.util.Set;

/**
 * The class encapsulates the report generated by schema validation.
 */
public class ValidationReport {

    final Set<ValidatedMessage> messages;

    /**
     * Constructs a Report with a set of {@code ValidatedMessage}
     *
     * @param mesgs
     *            - set of validated messages generated from schema validation.
     */
    public ValidationReport(final Set<ValidatedMessage> mesgs) {
        this.messages = mesgs;
    }

    /**
     * Method gets all {@link ValidatedMessage} generated from validating the Schema.
     *
     * @return set - of {@link ValidatedMessage}
     */
    public Set<ValidatedMessage> getMessages() {
        return this.messages;
    }

    /**
     * Method gets a set of {@link ValidatedMessage} that is of type {@code ReportLevel} supplied.
     *
     * @param reportLevel
     *            - the report level
     * @return set of {@link ValidatedMessage}
     */
    public Set<ValidatedMessage> getLogLevelMessages(final ReportLevel reportLevel) {

        final Set<ValidatedMessage> specificReportMessages = new HashSet<>();
        for (final ValidatedMessage mesg : messages) {
            if (mesg.getReportLevel() == reportLevel) {
                specificReportMessages.add(mesg);
            }
        }

        return specificReportMessages;
    }

    @Override
    public String toString() {
        return messages.toString();
    }

}
