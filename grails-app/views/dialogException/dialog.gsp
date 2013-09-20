<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />		
        <title>${title}</title>        
    </head>    
    <body>
		<div class="body">			
			<div class="row-fluid">
				<div class="span12">
                  <div title="${title}" id="error" class="xml-form modal hide xfade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						<div id="myModalLabel">
                            <span class="modal-header">${title}</span>
                        </div>
                    </div>
                    <div class="modal-body">
                  <form class="xml-form">
					<div>
						${msg}						
		  			</div>		
                  </form>
                    </div>
                    <div class="modal-footer">
                      <a href="#" class="btn" data-dismiss="modal"><g:message code="dialog.messages.ok" /></a>
                  </div>
				</div>
			</div>
		</div>
	</body>
</html>

