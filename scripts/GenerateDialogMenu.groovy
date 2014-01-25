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
 * Generates a dialog menu for all domain classes that have a listconfig
 */


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
