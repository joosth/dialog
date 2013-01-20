<div class="filemap">
   <div class="title"><g:message code="dialog.filemap.title" default="Files"/></div>
   <div class="body">
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
				<g:each in="${filemap}" var="f" >
		      		<div class="images">
						<div class="item" style="wwidth:128px;border:1px solid #CCC;margin:5px;padding:5px;float:left;">
		            		<div class="thumbnail">
								<a href="${f.url}" onclick="window.opener.CKEDITOR.tools.callFunction( ${CKEditorFuncNum}, '${f.url}');window.close();" title="${f.file.name}"><img width="128" height="128" src="${f.url}"/></a>              
		            		</div>
		            		<div class="details">
			            		<a onclick="window.opener.CKEDITOR.tools.callFunction( ${CKEditorFuncNum}, '${f.url}');window.close();" href="#">${f.file.name}</a>
		            		</div>
		       			</div>
		      		</div>
				</g:each>   
			</g:else>
		</g:else>
   </div>
</div>
