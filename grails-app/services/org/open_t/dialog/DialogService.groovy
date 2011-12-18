/*
* Grails Dialog plug-in
* Copyright 2011 Open-T B.V., and individual contributors as indicated
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
package org.open_t.dialog
import org.codehaus.groovy.grails.commons.* 
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

/*
 * Provide generic edit,submit,delete operations for dialog handling 
 */

class DialogService {

	def grailsApplication
	def sessionFactory
	
    boolean transactional = true
      
    def edit(domainClass,params) {
		def defaultDomainClass = new DefaultGrailsDomainClass( domainClass )		
		Map belongToMap = defaultDomainClass.getStaticPropertyValue(GrailsDomainClassProperty.BELONGS_TO, Map.class)
		
		def domainClassInstance
		if (params.id && params.id !='null') {
		    if (params.id.contains("_")){
				params.id=params.id.split("_")[1]
		    }
			domainClassInstance = domainClass.get(params.id)
		} else {
			domainClassInstance = domainClass.newInstance()
			// Some views (dialogs) shows fields that belong to the parent DomainObject
			// If there is a parentId it will load the parent DomainObject (belongsTo)
			// REMARK: Currently it will only work if belongto has only 1 relation
			if (belongToMap?.size() == 1 && params.parentId) {
				belongToMap.each { key, value -> domainClassInstance."${key}" = value.get(params.parentId) }
			} 
		}
		
		def domainPropertyName=defaultDomainClass.propertyName		
        def domainClassName=defaultDomainClass.getName()
        if (!domainClassInstance) {
            flash.message = "${domainPropertyName}.not.found"
            flash.args = [params.id]
            flash.defaultMessage = "${domainClassName} not found with id ${params.id}"
            redirect(action: "list")
        }
        else {
        	def returnMap=[:]
        	String key="${domainPropertyName}Instance"
        	returnMap.put(key,domainClassInstance)
            return returnMap
        }
    }
    	
	def submit(domainClass,params,instance=null) {
			def g=grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
			def defaultDomainClass = new DefaultGrailsDomainClass( domainClass )
			def domainPropertyName=defaultDomainClass.propertyName		
			def domainClassName=defaultDomainClass.getName()

			def action
    		def domainClassInstance
    		if (instance) {
    			domainClassInstance=instance
    		} else {
	    		if (params.id && params.id != 'null') {
	    		    if (params.id.contains("_")){
	    					params.id=params.id.split("_")[1]
	    			}
	    			domainClassInstance = domainClass.get(params.id)
					action = 'updated'
	    		} else 
	    		{
	    			domainClassInstance = domainClass.newInstance()
					action = 'created'
	    		}
	    		domainClassInstance.properties = params
    		}
			
            def successFlag=!domainClassInstance.hasErrors() && domainClassInstance.save(flush: true)
            
            def resultMessage
            def theErrorFields=[]
            if (successFlag) {
            	domainClassInstance.save(flush: true)
            
            	 def session = sessionFactory.getCurrentSession()
            	session.flush()
				
            	resultMessage="${domainClassName} #${domainClassInstance.id} : ${domainClassInstance.toString()} ${action}" 
            } else {
            	g.eachError(bean:domainClassInstance) {        		
            		theErrorFields+=it.field
            	}
            	resultMessage=g.renderErrors(bean:domainClassInstance)
            }
    		                     
    		def result = [
    		              	success:successFlag,
    		              	message:resultMessage,
							id: domainClassInstance.id,
    		              	name: domainClassInstance.toString(),	
    		              	errorFields:theErrorFields
    		              ]              
             def res=[result:result]
             return res    		
    	}
	
	def delete(domainClass,params) {
		def g=grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
		def defaultDomainClass = new DefaultGrailsDomainClass( domainClass )
		def domainPropertyName=defaultDomainClass.propertyName		
		def domainClassName=defaultDomainClass.getName()
	
		def id=params.id
		def domainClassInstance = domainClass.get(params.id )
		domainClassInstance.properties = params
        
        def theRefreshNodes=null
        def successFlag=true
        def resultMessage
        def theName = domainClassInstance.toString()
        try { 
        	domainClassInstance.delete()
        	resultMessage="${domainClassName} #${params.id} : ${theName} deleted"
        	
        } catch (Exception e ){
        	successFlag=false
        	resultMessage="${domainClassName} #${params.id} : ${theName} not deleted"
        }
		
		
        def theErrorFields=[]
                             
		def result = [
		              	success:successFlag,
		              	message:resultMessage ,
		              	id:params.id,
		              	name: theName,	
		              	refreshNodes:theRefreshNodes,
		              	errorFields:theErrorFields
		              ]              
         def res=[result:result]
         return res    		
	}

	def prettyPrint(String inputXml) {
	     Source xmlInput = new StreamSource(new StringReader(inputXml));
	     StreamResult xmlOutput = new StreamResult(new StringWriter());

	      // Configure transformer
	     Transformer transformer = TransformerFactory.newInstance().newTransformer(); // An identity transformer
	     
	     transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	     transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	     transformer.transform(xmlInput, xmlOutput);
	     return xmlOutput.getWriter().toString()
	    	     
	}

}
