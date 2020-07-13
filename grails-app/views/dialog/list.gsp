<g:if test="${listConfig==null && new org.grails.core.DefaultGrailsDomainClass(dc).hasProperty("listConfig")}">
	<g:set var="listConfig" value="${dc.listConfig}" />
</g:if>

<g:if test="${listConfig!=null}" >
	<g:render template="/dialog/clist" plugin="jquery-dialog"/>
</g:if>
<g:else>
	<g:render template="/dialog/list" plugin="jquery-dialog" />
</g:else>
