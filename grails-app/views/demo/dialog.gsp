<dialog:form title="Demo" object="${demoInstance}">
	<dialog:table>		                            

		<dialog:textField object="${demoInstance}" propertyName="id" mode="show" />                            
		<dialog:textField object="${demoInstance}" propertyName="name" mode="edit" required="required" placeholder="Please enter a name"/>
				
		<dialog:date object="${demoInstance}" propertyName="dateCreated" mode="show" />
		<dialog:date object="${demoInstance}" propertyName="lastUpdated" mode="show" />
		 
		<dialog:date object="${demoInstance}" propertyName="dateOfBirth" mode="edit" />
		<dialog:textArea object="${demoInstance}" propertyName="description" mode="edit" />
		<dialog:checkBox object="${demoInstance}" propertyName="publish" mode="edit" />
		
		<dialog:domainObject object="${demoInstance}" propertyName="demo" mode="edit" />
		
		<dialog:detailTable property="demo" object="${demoInstance}" domainClass="${org.open_t.dialog.demo.DemoItem}" />
		    					
	
	 </dialog:table>
</dialog:form>
