<dialog:form title="Demo" object="${demoItemInstance}">
	<dialog:table>		                            

		<dialog:textField object="${demoItemInstance}" propertyName="id" mode="show" />                            
		<dialog:textField object="${demoItemInstance}" propertyName="qty" mode="edit" />
		<dialog:textField object="${demoItemInstance}" propertyName="name" mode="edit" />
		<dialog:textField object="${demoItemInstance}" propertyName="price" mode="edit" />
		
	 </dialog:table>
</dialog:form>
