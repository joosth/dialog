includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")
includeTargets << grailsScript("_GrailsBootstrap")

target(generateDialogView: "Generates a new dialog view") {
	depends(checkVersion, parseArguments,packageApp,loadApp)

	promptForName(type: "Domain class)")

	for (name in argsMap["params"]) {

		def packageName=name.replaceAll("\\.[^.]*\$","")
		def shortName=name.replaceAll("^.*\\.","")
		def propertyName= shortName[0].toLowerCase()+shortName.substring(1)

		def artifactFile = "${basedir}/grails-app/views/${propertyName}/dialog.gsp"

		def packagePath=packageName.replaceAll("\\.","/")

		def domainClass=grailsApp.getDomainClass(name)

		if (new File(artifactFile).exists()) {
			if (!confirmInput("${artifactFile} already exists. Overwrite?")) {
				return
			}
		}
		// create path
		new File(basedir, "grails-app/views/${propertyName}").mkdirs()

		// create file
		def viewText="""<dialog:form object="\${${propertyName}Instance}" >
	<dialog:table>"""
		domainClass.persistentProperties.each { prop ->

			if ((prop.type== String) || (prop.type== int) || (prop.type== float)|| (prop.type== double)|| (prop.type== Double) ) {
				viewText+="""\n		<dialog:textField object="\${${propertyName}Instance}" propertyName="${prop.name}" mode="edit" />"""
			}
			if (prop.type== Date) {
				viewText+="""\n		<dialog:date object="\${${propertyName}Instance}" propertyName="${prop.name}" mode="edit" />"""
			}
			if (prop.type== boolean || prop.type==Boolean) {
				viewText+="""\n		<dialog:checkBox object="\${${propertyName}Instance}" propertyName="${prop.name}" mode="edit" />"""
			}
			if (prop.association) {
				def assocName=prop.type.getName()
				def targetDomainClass=grailsApp.getDomainClass(assocName)
				def sortName=targetDomainClass.persistentProperties[0].name
				viewText+="""\n		<dialog:domainObject object="\${${propertyName}Instance}" propertyName="${prop.name}" mode="edit" sort="${sortName}"/>"""
			}
		}
	 viewText+="""\n	</dialog:table>
</dialog:form>
"""
		new File(artifactFile).write(viewText)
		grailsConsole.addStatus "View ${artifactFile} generated."
	}
}

setDefaultTarget 'generateDialogView'
