<%@page import="org.codehaus.groovy.grails.commons.*" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title>
            <g:if test="${title}">${title}</g:if>
            <g:else><g:message code="${name?name:controllerName}.list.title" default="${name?name:controllerName}.list.title" /></g:else>
        </title>
    </head>
    <body>
        <h3>
            <g:if test="${title}">${title}</g:if>
            <g:else><g:message code="${name?name:controllerName}.list.title" default="${name?name:controllerName}.list.title" /></g:else>
        </h3>
        <div class="datatable dialog-open-events" id="tableWrapper_${dc.getName().replace('.','_').replace('class ','')}">
            <table iDisplayLength="10" datatable-type="master" bFilter="${bFilter ? true : false}"
                    jsonUrl="${createLink(base:'/',controller:controllerName,action: jsonlist ? jsonlist :'jsonlist',params:jsonlistparams?jsonlistparams:[:]).substring(1)}"
                    cellpadding="0" cellspacing="0" border="0" class="detailTable dialog-open-events datatable table table-striped table-bordered display${rowreordering?' rowreordering':''}" id="detailTable_${dc.getName().replace('.','_').replace('class ','')}"
                    positionUrl="/${controllerName}/${position ? position :'position'}" toolbar="${toolbar?:''}" >
                <thead>
                    <tr>
                        <g:each in="${dc.listProperties}" var="property">
                            <g:if test="${dc.declaredFields.findAll {it.name=='sortableProperties'}.size()>0}">
                                <th class="${dc.sortableProperties?.contains(property)?'sortable':'nonsortable'} ${controllerName}-${property}"><g:message code="${controllerName}.${property}.label" default="${controllerName}.${property}.label" /></th>
                            </g:if>
                            <g:else>
                                <th class="${(property=='id' || new DefaultGrailsDomainClass(dc).hasPersistentProperty(property))?'sortable':'nonsortable'} ${controllerName}-${property}"><g:message code="${controllerName}.${property}.label" default="${controllerName}.${property}.label" /></th>
                            </g:else>
						</g:each>
						<th width="50px"><g:message code="dialog.list.actions.label" default="Actions" /></th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td colspan="${dc.listProperties.size()}" class="dataTables_empty">Loading data from server</td>
                    </tr>
                </tbody>
                <tfoot>
                    <tr>
                        <g:each in="${dc.listProperties}" var="property">
                            <th><g:message code="${controllerName}.${property}.label" default="${controllerName}.${property}.label" /></th>
						</g:each>
                            <th><g:message code="dialog.list.actions.label" default="Actions" /></th>
                    </tr>
                </tfoot>
            </table>
        </div>
    </body>
</html>
