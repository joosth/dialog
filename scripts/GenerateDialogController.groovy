includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")

target(generateDialogController: "Generates a new dialog controller") {
	depends(checkVersion, parseArguments)

	promptForName(type: "Controller")

	for (name in argsMap["params"]) {
		def artifactPath=name.replaceAll("\\.","/")
		def artifactFile = "${basedir}/grails-app/controllers/${artifactPath}Controller.groovy"
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
		new File(basedir, "grails-app/controllers/${packagePath}").mkdirs()

		// create file
		def dcText="""package ${packageName}
import grails.converters.JSON

class ${shortName}Controller {

	static allowedMethods = [submitdialog: "POST", delete: "POST"]

	def listService
	def dialogService

	def index() { redirect(action: "list", params: params) }

	def list() {
		render (view:'/dialog/list', model:[dc:${shortName},listConfig:${shortName}.listConfig,request:request])
	}

	def jsonlist() {
		render listService.jsonlist(${shortName},params,request) as JSON
	}

	def position() {
		render listService.position(${shortName},params) as JSON
	}

	def dialog() { return dialogService.edit(${shortName},params) }

	def submitdialog() { render dialogService.submit(${shortName},params) as JSON }

	def delete() { render dialogService.delete(${shortName},params) as JSON }
}
"""
		new File(artifactFile).write(dcText)
		grailsConsole.addStatus "Controller ${artifactFile} generated."
		createUnitTest(name: name, suffix: "Controller")
	}
}


setDefaultTarget 'generateDialogController'
