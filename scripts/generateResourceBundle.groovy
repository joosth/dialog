/*
 * This script generates a resource file for all missing resource items in all domain classes in the grails app
*/
import org.codehaus.groovy.grails.commons.* 

def labelExists(String key) {
    def messageSource = grailsApplication.mainContext.messageSource
    return messageSource.getMessage(key,null,"UNKNOWN",null)!="UNKNOWN"
}


println "application ${grailsApplication}"
grailsApplication.allArtefacts.each { artefact ->
    if (artefact.isAnnotationPresent(grails.persistence.Entity)) {
    //println "${artefact} ${artefact.isAnnotationPresent(grails.persistence.Entity)}"
    //println "# ${artefact}"
    
    def defaultDomainClass = new DefaultGrailsDomainClass( artefact )

    def properties=defaultDomainClass.properties
    def dcname=defaultDomainClass.propertyName
    
    println "# ${defaultDomainClass.naturalName}"
    if (!labelExists("${dcname}.list.title")) {
        println "${dcname}.list.title=${defaultDomainClass.naturalName} list"
    }
    println ""

    properties.each { property ->
        def msgkey="${dcname}.${property.name}.label"

        if (!labelExists("${dcname}.${property.name}.label")) {
            println "${dcname}.${property.name}.label=${property.naturalName}"
        }
        if (!labelExists("${dcname}.${property.name}.help")) {
            println "${dcname}.${property.name}.help =${property.naturalName}|The ${property.naturalName}"
        }
        println ""
    }    
    
    
}
}
