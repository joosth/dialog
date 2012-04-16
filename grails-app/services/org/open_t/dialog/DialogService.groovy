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
import org.apache.commons.lang.WordUtils

/*
 * Provide generic edit,submit,delete operations for dialog handling 
 */

class DialogService {

	def grailsApplication
	def sessionFactory
	
    boolean transactional = true
    
	/**
	* Prepares data for an edit action. The data consists of a Domain object instance with default values that is wrapped in a map
	* Based on the id in params the instance is retrieved or a fresh instance is created 
	* @param domainClass The domain class to be used
	* @param params The parameters from the http request 
	* @return a map that is ready to be rendered as a JSON message
	*/

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
	
	/**
	* Processes a form submission.
	* The instance to be submitted is constructed based on params or the provided instance is used. This last option enables the controller to pre-prep the instance.
	* 
	* @param domainClass The domain class to be used
	* @param params The parameters from the http request
	* @param instance The domain object to be used (in lieu of creating one from params)
	* @return a map that is ready to be rendered as a JSON message
	*/
    	
	def submit(domainClass,params,instance=null) {
			def g=grailsApplication.mainContext.getBean('org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib')
			def defaultDomainClass = new DefaultGrailsDomainClass( domainClass )
			def domainPropertyName=defaultDomainClass.propertyName		
			def domainClassName=defaultDomainClass.getName()

			def action
    		def domainClassInstance
    		if (instance) {
    			domainClassInstance=instance
				action='updated'
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
			
            def resultMessage=""
            def theErrorFields=[]
            if (successFlag) {
            	domainClassInstance.save(flush: true)            
            	 def session = sessionFactory.getCurrentSession()
            	session.flush()
				
            	resultMessage="${domainClassName} #${domainClassInstance.id} : ${domainClassInstance.toString()} ${action}" 
            } else {
				if (domainClassInstance) {
					domainClassInstance.errors.allErrors.each {						
						theErrorFields+=it.field
					}
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
	
	/**
	* Delete a domain object
	* The instance to be submitted is constructed based on params or the provided instance is used. This last option enables the controller to pre-prep the instance.
	*
	* @param domainClass The domain class to be used
	* @param params The parameters from the http request
	* @return a map that is ready to be rendered as a JSON message
	*/
	
	
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
	
	/**
	* Pretty print a XML document	
	* @param inputXml The text to be processed
	* @return Pretty XML
	*/
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
	
	/**
	* Manage a join relationship. This is to accomodate the type of n:m relationship that the security plugin uses.
	* This is ment to be invoked directly after the normal form submission
	* @param params The request parameters
	* @param instance The current domain instance to be used
	* @param propertyName The 'property' that provides the related items from the target class
	* @param joinClass The class that is used to maintain the join relationship. Needs to provide a create and a remove method
	* @param targetClass The class that is on the other side of the n:m relationship
	* @return Pretty XML
	*/
	
	def manageJoin(params,instance,propertyName,joinClass,targetClass) {
	
		def newAuthorities=[]
		if (params."${propertyName}") {
			if (params."${propertyName}".class==java.lang.String) {
				newAuthorities=[new java.lang.Long(params."${propertyName}")]
			} else {
				newAuthorities=params."${propertyName}".collect { return new java.lang.Long(it) }
			}
		}
	
		def currentAuthorities=[]
		instance."${propertyName}".each { currentAuthority ->
			currentAuthorities+=currentAuthority.id
		}
	   
		// Add UserRole entries that are missing
		
		newAuthorities.each { newAuthority ->
			if (!currentAuthorities.contains(newAuthority)) {
				joinClass.create(instance, targetClass.get(newAuthority))
			}
		}
	
	
		// Remove UserRole entries that are superfluous
		
		currentAuthorities.each { currentAuthority ->
			if (!newAuthorities.contains(currentAuthority)) {
				joinClass.remove(instance,targetClass.get(currentAuthority))
			}
		}
		
	}
	
	
	/**
	* Generates a JSON response to feed the datalist
	* @param dc The domain class to be used
	* @param params The parameters from the http request
	* @param request the HTTPServletRequest
	* @param filterColumnName The name of the column to be used for filtering (can be null to disable)
	* @param actions A closure that provides customized actions in the actions column of the table
	* @return a map that is ready to be rendered as a JSON message
	*/

	def autocomplete(dc,params,request,searchColumnName="name",labelColumnName="acLabel",descriptionColumnName="acDescription") {
			def title=dc.getName();
			title=title.replaceAll (".*\\.", "")
			def propName=title[0].toLowerCase()+title.substring(1)
			
			def columns=dc.listProperties
			def sortName=searchColumnName
			 
			def documentList
			def iTotalRecords=dc.count()
			def iTotalDisplayRecords
			def maxResults=10
						
			if (searchColumnName) {
				def filterMethod = "findAllBy"+WordUtils.capitalize(searchColumnName)+"Like"
				documentList=dc."$filterMethod"(params.term+"%", [max:maxResults,sort:sortName])
			} else {
				documentList=dc.list([max:maxResults,sort:sortName])
			}
			
			
			def json=[]
			documentList.each { doc ->
				if (labelColumnName) {
					if (descriptionColumnName) {
						json+=[value:doc.id,label:doc."${labelColumnName}", description:doc."${descriptionColumnName}"]
					} else {
						json+=[value:doc.id,label:doc."${labelColumnName}"]
					}
				} else {
					json+=[value:doc.id,label:doc.toString()]				
				}
			} 
			return json
		}
}
