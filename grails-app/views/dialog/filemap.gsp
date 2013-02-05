<g:applyLayout name="plain">
<div class="filemap">
<div class="container">
   <h4><g:message code="dialog.filemap.title" default="Files"/></h4>
   
   <div class="row"><div class="span12">
		<g:if test ="${message}">
		      <div class="detail-list-item first-item last-item">
		         <div class="error">${message}</div>
		      </div>
		</g:if>
		<g:else>
			<g:if test ="${filemap.size()==0}">
				<div class="detail-list-item first-item last-item">
					<span><g:message code="dialog.filemap.nofiles" default="There are no files."/></span>
				</div>
			</g:if>
			<g:else>
			    <ul class="thumbnails">
			    <g:each in="${filemap}" var="f" >
    				<li class="span4">    					
    						<div class="thumbnail">
    							<a href="${f.url}" onclick="window.opener.CKEDITOR.tools.callFunction( ${CKEditorFuncNum}, '${f.url}');window.close();" title="${f.file.name}">
    								<img data-src="holder.js/128x128" alt="" src=${f.url}>
    							</a>
								<h5><a onclick="window.opener.CKEDITOR.tools.callFunction( ${CKEditorFuncNum}, '${f.url}');window.close();" href="#">${f.file.name}</a></h5>
								<p>${f.file.size()} bytes</p>
    						</div>
    						</li>
     					</g:each>
   				</ul>			
			</g:else>
		</g:else>
   </div>
   </div>
   </div>
</div>
</g:applyLayout>