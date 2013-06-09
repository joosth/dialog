includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target(createDialogDomainClass: "Creates a new dialog domain class") {
	depends(checkVersion, parseArguments)

	promptForName(type: "Domain class")

	for (name in argsMap["params"]) {
		def artifactPath=name.replaceAll("\\.","/")
		def artifactFile = "${basedir}/grails-app/domain/${artifactPath}.groovy"
		def packageName=name.replaceAll("\\.[^.]*\$","")
		def shortName=name.replaceAll("^.*\\.","")

		def packagePath=packageName.replaceAll("\\.","/")
		def propertyName= shortName[0].toLowerCase()+shortName.substring(1)


		if (new File(artifactFile).exists()) {
			if (!confirmInput("${artifactFile} already exists. Overwrite?")) {
				return
			}
		}
		// create path
		new File(basedir, "grails-app/domain/${packagePath}").mkdirs()

		// create file
		def dcText="""package ${packageName}
import org.open_t.dialog.*
class ${shortName} {

	// Configuration for list
	static listConfig=new ListConfig(name:'${propertyName}',controller: '${propertyName}',newButton:true).configure {
		// Add columns like this:
		//column name:'name',sortable:true
	}
}
"""
		new File(artifactFile).write(dcText)
		grailsConsole.addStatus "Domain class ${artifactFile} generated."

		createUnitTest(name: name, suffix: "")
	}
}

setDefaultTarget 'createDialogDomainClass'
