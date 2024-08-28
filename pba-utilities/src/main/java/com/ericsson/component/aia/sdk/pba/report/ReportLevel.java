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

/**
 * Signifies the level of validation messages.
 */
public enum ReportLevel {

    DEBUG("debug"), INFO("info"), WARNING("warning"), ERROR("error"), FATAL("fatal"), NONE("none");

    final String reportLevel;

    /**
     * Constructs signifying the level of reporting.
     * @param rLevel
     *            - reporting level
     */
    ReportLevel(final String rLevel) {
        reportLevel = rLevel;
    }

    /**
     * Gets the reprt level of this enum.
     *
     * @return the reportLevel
     */
    public String getReportLevel() {
        return reportLevel;
    }

    /**
     * Retruns the report level.
     */
    @Override
    public String toString() {
        return reportLevel;
    }

}
