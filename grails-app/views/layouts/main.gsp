<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%--<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">--%>
<html>
    <head>
    	<!-- This is main.gsp in the dialog plugin demo -->
        <title><g:layoutTitle default="Dialog plugin demo" /></title>
        
        
        <g:layoutHead />
		<r:layoutResources/>		
		<dialog:head />   
		
	</head>
    <body>
    <div class="navbar navbar-inverse navbar-fixed-top">
			<div class="navbar-inner">
        		<div class="container">          
        		<a href="/demo" title="${message(code:'brand')}|${message(code:'brand')}" class="brand" >&nbsp;</a>
          			<div class="nav-collapse collapse">
	            		<ul class="nav">            
		              		<li class="">                
		                		<a href="/dialog/demo" class="brand" >Dialog demo</a>
		              		</li>
		              
		             		 <li class="dropdown">
		              			<a class="dropdown-toggle" data-toggle="dropdown" href="#">
					 			<g:message code="demo" />
								<b class="caret"></b>
								</a>
								<ul class="dropdown-menu">
									<li class="menu-icon formicon" ><g:link controller="demo" action="list">Demo</g:link></li>
								    <li class="menu-icon mytasks" ><g:link controller="demoItem" action="list" >Demo items</g:link></li>
								</ul>                    
              		 		</li>
             		 	</ul>
            		 </div>
           		 </div>
         	</div>
    
    	 	<g:layoutBody />
	    			<r:layoutResources />
    	 	    </div>
    </body>	
</html>
