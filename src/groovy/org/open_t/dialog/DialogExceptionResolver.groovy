/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.open_t.dialog

import javax.servlet.http.HttpServletRequest
import org.codehaus.groovy.grails.web.errors.GrailsExceptionResolver

/**
 * Subclass GrailsExceptionResolver to exclude DialogException from the error log
 * @author joost
 */
class DialogExceptionResolver extends GrailsExceptionResolver {

protected void logStackTrace(Exception e, HttpServletRequest request) {
    if (e.getClass()==DialogException) {
        LOG.debug(getRequestLogMessage(e, request), e);
    } else
        LOG.error(getRequestLogMessage(e, request), e);
    }
}

