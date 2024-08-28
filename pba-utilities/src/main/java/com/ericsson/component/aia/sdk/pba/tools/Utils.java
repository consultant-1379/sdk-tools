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

import com.ericsson.component.aia.sdk.pba.report.ReportLevel;
import com.github.fge.jsonschema.core.report.LogLevel;

/**
 * Utilities class.
 */
public final class Utils {

    private Utils() {
    }

    /**
     * Maps and gets the ReportLevel from the LogLevel.
     *
     * @param level
     *            - {@link LogLevel}
     * @return - {@link ReportLevel}
     */
    static ReportLevel getReportLevel(final LogLevel level) {
        switch (level.toString()) {
            case "debug":
                return ReportLevel.DEBUG;
            case "info":
                return ReportLevel.INFO;
            case "warning":
                return ReportLevel.WARNING;
            case "error":
                return ReportLevel.ERROR;
            case "fatal":
                return ReportLevel.FATAL;
            case "none":
                return ReportLevel.NONE;
            default:
                return ReportLevel.NONE;
        }
    }

}
