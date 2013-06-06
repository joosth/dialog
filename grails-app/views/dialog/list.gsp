<g:if test="${listConfig==null && new org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass(dc).hasProperty("listConfig")}">
	<g:set var="listConfig" value="${dc.listConfig}" />
</g:if>

<g:if test="${listConfig!=null}" >
	<g:render template="/dialog/clist" />
</g:if>
<g:else>
	<g:render template="/dialog/list" />
</g:else>