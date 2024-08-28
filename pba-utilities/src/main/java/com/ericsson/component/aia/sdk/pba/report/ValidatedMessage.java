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
 * ValidateMessage represents an error generated out of validating the pba schema against the pba instance.
 */
public class ValidatedMessage {

    private String message;

    private String detailedMessage;

    private ReportLevel reportLevel;

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * @return the detailedMessage
     */
    public String getDetailedMessage() {
        return detailedMessage;
    }

    /**
     * @param detailedMessage
     *            the detailedMessage to set
     */
    public void setDetailedMessage(final String detailedMessage) {
        this.detailedMessage = detailedMessage;
    }

    /**
     * @return the reportLevel
     */
    public ReportLevel getReportLevel() {
        return reportLevel;
    }

    /**
     * @param reportLevel
     *            the reportLevel to set
     */
    public void setReportLevel(final ReportLevel reportLevel) {
        this.reportLevel = reportLevel;
    }

    @Override
    public String toString() {
        return "Message: [" + message + "] \n Detailed: [" + detailedMessage + "]";
    }

}
