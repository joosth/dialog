<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title>${result.title}</title>
         <r:script>
        // Dialog plugin -- list
        $(document).ready(function() {
            $("#exceptionDialog").modal();
        });
       </r:script>
    </head>
    <body>
		<div class="body">
			<div class="row-fluid">
				<div class="span12">
                  <div title="${result.title}" id="exceptionDialog" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
                      <div class="modal-dialog">
                          <div class="modal-content">
                              <div class="modal-header">
                                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                                    <h3>${result.title}</h3>
                              </div>
                              <div class="modal-body">
                              <form>
                                  <div>${result.message}</div>
                              </form>
                            </div>
                            <div class="modal-footer">
                                <a href="#" class="btn" data-dismiss="modal"><g:message code="dialog.messages.ok" /></a>
                            </div>
                        </div>
                    </div>
				</div>
			</div>
		</div>
        </div>
	</body>
</html>
