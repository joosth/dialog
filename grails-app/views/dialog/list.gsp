<g:if test="${listConfig}" >
	<g:render template="/dialog/clist" />
</g:if>
<g:else>
	<g:render template="/dialog/list" />
</g:else>