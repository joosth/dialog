import org.codehaus.groovy.grails.scaffolding.*
import grails.util.GrailsNameUtils
import org.codehaus.groovy.grails.commons.*
import org.open_t.dialog.*

includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsCreateArtifacts")
includeTargets << grailsScript("_GrailsBootstrap")


target ('default': "Generates dialog messages") {
	depends(checkVersion, parseArguments,loadApp)	

	// get the current file, read it into messsages Map
	def artifactFile = "${basedir}/grails-app/i18n/messages.properties"
	def messagesText=new File(artifactFile).text
	def messages=[:]
	messagesText.eachLine { line ->	
		if (!line.startsWith("#")) {
			def split=line.split('=')
			
			if (split.size()==2) {
				def (key,value)=split
				// trim spaces
				key=key.replaceAll("^ *","").replaceAll(" *\$","")
				value=value.replaceAll("^ *","").replaceAll(" *\$","")

				messages[key]=value
			}
		}
	}
	
	// Returns true if label exists
	def labelExists= { key ->
		messages[key]!=null
	}
	
	def newMessageText=""
	
	// Go through domain classes to get properties and generate label and help messages 
	grailsApp.domainClasses.each { dc ->
		def msgText=""
		def defaultDomainClass = dc //new DefaultGrailsDomainClass( artefact )
		
		def properties=defaultDomainClass.properties
		def dcname=defaultDomainClass.propertyName
				
		if (!labelExists("list.${dcname}.title")) {
			msgText+="\nlist.${dcname}.title=${defaultDomainClass.naturalName} List"
		}
	
		if (!labelExists("form.${dcname}.title")) {
			msgText+="\nform.${dcname}.title=${defaultDomainClass.naturalName}"
		}
		
		if (dc.hasProperty("listConfig")) {		
			dc.getStaticPropertyValue("listConfig",Object).columns.each { column ->
				if (!labelExists("list.${dcname}.${column.name}.label")) {
					def columnTitle= column.name[0].toUpperCase()+column.name.substring(1)					
					msgText+= "\nlist.${dcname}.${column.name}.label=${columnTitle}"
				}
			}
		}
		
		properties.each { property ->
			
			if (!labelExists("${dcname}.${property.name}.label")) {
				msgText+= "\n${dcname}.${property.name}.label=${property.naturalName}"
			}
			if(argsMap["with-help"]) {
				if (!labelExists("${dcname}.${property.name}.help")) {
					msgText+= "\n${dcname}.${property.name}.help=${property.naturalName}"
				}
			}
		}
		
		if (msgText) {
			msgText="\n\n# ${defaultDomainClass.naturalName}\n${msgText}"
			newMessageText+=msgText
		}		
	}
	
	// Go through controllers to generate messages for menu
	def menuText=""

	grailsApp.domainClasses.each { dc ->
		
		def defaultDomainClass = dc //new DefaultGrailsDomainClass( artefact )
		
		def properties=defaultDomainClass.properties
		def dcname=defaultDomainClass.propertyName
		
		if (dc.hasProperty("listConfig")) {
			def listConfig=dc.getStaticPropertyValue("listConfig",Object)
			def propertyControllerName=listConfig.controller
			
			if (!labelExists("menu.${propertyControllerName}.list.label")) {
				def controllerTitle=propertyControllerName[0].toUpperCase()+propertyControllerName.substring(1).replaceAll("(\\w)([A-Z])",'$1 $2')
				menuText+="\nmenu.${propertyControllerName}.list.label=${controllerTitle}"
			}
		}
	}	
	
	if (!labelExists("dropdown.${grailsAppName}.label")) {
		def appplicationTitle=grailsAppName[0].toUpperCase()+grailsAppName.substring(1).replaceAll("(\\w)([A-Z])",'$1 $2')
		menuText+="\ndropdown.${grailsAppName}.label=${appplicationTitle}"
	}
	
	if (menuText) {
		newMessageText="${newMessageText}\n\n# Menu messages\n${menuText}"
	}

	if (newMessageText) {
		def n=newMessageText.count("\n")
		new File(artifactFile).write(messagesText+'\n'+newMessageText)
		grailsConsole.addStatus "${n} lines added to ${artifactFile}."
	} else {
		grailsConsole.addStatus "No new messages added."
	}
}


