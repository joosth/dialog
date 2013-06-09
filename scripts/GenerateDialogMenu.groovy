includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")
includeTargets << grailsScript("_GrailsBootstrap")

target(generateDialogMenu: "Set up a dialog-based grails application") {
	depends(checkVersion, parseArguments,loadApp)

	// AppNameResources.groovy
	def grailsAppTitle=grailsAppName[0].toUpperCase()+grailsAppName.substring(1)

	def menuText="""<div class="nav-collapse collapse">
	<ul class="nav">
		<li class="">
			<a href="/${grailsAppName}" class="brand" >ToDo</a>
		</li>
        <dialog:dropdown code="${grailsAppName}">
	"""
	/*
	grailsApp.allArtefacts.each {
		def name=it.name
		if (name.endsWith("Controller")) {
			def shortControllerName=name.replaceAll("^.*\\.","")
			def propertyControllerName= shortControllerName[0].toLowerCase()+shortControllerName.substring(1).replaceAll("Controller","")

			//def controllerTitle=shortControllerName.replaceAll("Controller","").replaceAll("(\\w)([A-Z])",'$1 $2')
			//menuText+="\nmenu.${propertyControllerName}.list.label=${controllerTitle}"
			menuText+="""\n<dialog:menuitem controller="${propertyControllerName}" action="list" icon="icon-star"/>"""
		}
	}
	*/
	grailsApp.domainClasses.each { dc ->

		def defaultDomainClass = dc //new DefaultGrailsDomainClass( artefact )

		def properties=defaultDomainClass.properties
		def dcname=defaultDomainClass.propertyName

		if (dc.hasProperty("listConfig")) {
			def listConfig=dc.getStaticPropertyValue("listConfig",Object)
			menuText+="""\n<dialog:menuitem controller="${listConfig.controller}" action="list" icon="icon-star"/>"""
		}
	}

	menuText+="""		</dialog:dropdown>
	</ul>
</div>
"""

	generateFile "${basedir}/grails-app/views/layouts/_menu.gsp" , menuText
}

def generateFile (path,text) {
	if (new File(path).exists()) {
		if (!confirmInput("${path} already exists. Overwrite?")) {
			return
		}
	}
	new File(path).write(text)
	grailsConsole.addStatus "File ${path} generated."
}

setDefaultTarget 'generateDialogMenu'
