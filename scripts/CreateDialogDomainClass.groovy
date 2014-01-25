/*
 * Grails Dialog plug-in
 * Copyright 2014 Open-T B.V., and individual contributors as indicated
 * by the @author tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License
 * version 3 published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses
 */

/**
 * Creates a dialog domain class that includes a listconfig skeleton + imports
 */

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
