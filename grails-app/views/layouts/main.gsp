<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">--%>
<html>
    <head>
    	<!-- This is main.gsp in the dialog plugin demo -->
        <title><g:layoutTitle default="Dialog plugin demo" /></title>
                
        <%-- css from used modules --%>
        <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'jquery.cluetip.css',contextPath:'',plugin:'dialog')}" />
    	<link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'datatables.css',plugin:'dialog',contextPath:'')}" />
	    
		<%-- Theme style --%>
		<link rel="stylesheet" type="text/css" href="${resource(dir:'css/theme',file:'roller-theme.css',contextPath:'',plugin:'dialog')}" />
        <link rel="stylesheet" type="text/css" href="${resource(dir:'css/theme',file:'theme.css',contextPath:'',plugin:'dialog')}" />
        
        <link rel="stylesheet" type="text/css" href="${resource(dir:'css',file:'dialog.css',plugin:'dialog',contextPath:'')}" />              
        <link rel="shortcut icon" href="${createLinkTo(dir:'images',file:'favicon.ico',contextPath:'',plugin:'dialog')}" type="image/x-icon" />
        
        <g:javascript src="jquery/jquery-1.4.2.js" plugin="dialog" />
        <g:javascript>
        	var wfp={};
        	var dataTableHashList = {};
        	wfp.baseUrl="${request.contextPath}";
        </g:javascript>
                 
        <g:javascript src="jquery/jquery-ui-1.8.custom.min.js" contextPath="" plugin="dialog" />
        <g:javascript src="jquery/jquery.cluetip-patched.js"  contextPath="" plugin="dialog" />
        <g:javascript src="jquery/jquery.dataTables.js"  contextPath="" plugin='dialog'/>
                        
        <g:javascript src="dialog.js"  contextPath="" plugin="dialog" />
        
		<g:layoutHead />
	</head>
    <body>
    
     <div id="main-toolbar" >
    	<span id="menu-toolbar">
    		<a href="/dialog/demo" title="Home|Go to the home screen" class="action home help" >&nbsp;</a>
    	</span>
    	
    	<span id="user-toolbar" style="float:right;">    		    		
    		<sec:ifLoggedIn><sec:loggedInUserInfo field="username"/><g:link title="logout" class="logout action" controller="logout">&nbsp;</g:link></sec:ifLoggedIn>
    	</span>
    
    	</div>
		
		<div class="page-body" >
     	          
	    <div class="dialog-menu">
	    <div id="topleft-logo" class="fg-toolbar ui-toolbar ui-widget-header ui-corner-tl ui-corner-tr table-title">
	    <h1>Dialog demo</h1></div>
	      <ul>		            
	        <li>Demo</li>	        	        
	        <ul>	        
	          <li class="menu-icon menu-demo"><g:link controller="demo" action="list">Demo</g:link></li>
	          <li class="menu-icon menu-demo-items"><g:link controller="demoItem" action="list">Demo items</g:link></li>
          	</ul>
            
	      </ul>	      
	    </div>

    	 <g:layoutBody />
    	 	    </div>
    </body>	
</html>
