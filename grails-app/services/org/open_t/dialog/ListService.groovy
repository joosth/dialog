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

import org.apache.commons.lang.WordUtils

/*
 * Provide list handling service
 */

class ListService {
	
	def grailsApplication
    boolean transactional = true
	

	/**
	* Generates a JSON response to feed the datalist
	* @param dc The domain class to be used
	* @param params The parameters from the http request
	* @param request the HTTPServletRequest
	* @param filterColumnName The name of the column to be used for filtering (can be null to disable)
	* @param actions A closure that provides customized actions in the actions column of the table
	* @return a map that is ready to be rendered as a JSON message
	*/

    def jsonlist(dc,params,request,filterColumnName=null,actions=null) {
        	def title=dc.getName();
        	title=title.replaceAll (".*\\.", "")
        	def propName=title[0].toLowerCase()+title.substring(1)
        	
            def columns=dc.listProperties
            def sortName=columns[new Integer(params.iSortCol_0)]
     		sortName=sortName? sortName:columns[0]
			 
			def documentList
			def iTotalRecords=dc.count()
			def iTotalDisplayRecords
			
			//Create Id for the table
			def detailTableId="detailTable_"+dc
			detailTableId=detailTableId.replace(".","_")
			detailTableId=detailTableId.replace("class ","")
						
			if (params['objectId'] != null) {
				if (params.objectId !='null') {
					def filterMethod = "findAllBy"+WordUtils.capitalize(params.property)
					def masterDomainObj = grailsApplication.getClassForName(params.objectClass).get(params.objectId)
					documentList=dc."$filterMethod"(masterDomainObj, [max:params.iDisplayLength,offset:params.iDisplayStart,order:params.sSortDir_0,sort:sortName])
					iTotalRecords=documentList.size()
					iTotalDisplayRecords = iTotalRecords
				} else {
					iTotalRecords=0
					iTotalDisplayRecords=0
				}				
			} else {
				if (filterColumnName && params.sSearch) {
					def filterMethod = "findAllBy"+WordUtils.capitalize(filterColumnName)+"Like"
					iTotalDisplayRecords=dc."$filterMethod"(params.sSearch+"%").size()
					documentList=dc."$filterMethod"(params.sSearch+"%", [max:params.iDisplayLength,offset:params.iDisplayStart,order:params.sSortDir_0,sort:sortName])
				} else {
					documentList=dc.list([max:params.iDisplayLength,offset:params.iDisplayStart,order:params.sSortDir_0,sort:sortName])
					iTotalDisplayRecords = iTotalRecords
				}
			}
			
    		def aaData=[]
            documentList.each { doc ->
        		def inLine=[]
        		columns.each { 	            			   
        			inLine +=doc."${it}".toString()
        		}	            		
        		def baseUrl=request.contextPath
        		if(!actions) {
        			actions= { dok, env -> """<span class="list-action-button ui-state-default" onclick="dialog.formDialog(${dok.id},'${propName}',{ refresh : '${detailTableId}'}, null)">edit</span>&nbsp;<span class="list-action-button ui-state-default" onclick="dialog.deleteDialog(${dok.id},'${propName}',{ refresh : '${detailTableId}'}, null)">&times;</span>""" }
        		}
        		inLine+=actions(doc,['detailTableId':detailTableId])
        		def aaLine=[inLine]
        		aaData+=(aaLine)
    		}

    		def json = [sEcho:params.sEcho,iTotalRecords:iTotalRecords,iTotalDisplayRecords:iTotalDisplayRecords,aaData:aaData]
        	return json
        }
	
	/*
	 * JSON service that allows for an arbitrary HQL query to be the source of the list 
	 */
	
	/**
	* Generates a JSON response to feed the datalist from an arbitrary HQL query
	* @param dc The domain class to be used
	* @param params The parameters from the http request
	* @param request the HTTPServletRequest
	* @param query the HQL query
	* @param the HQL query that counts the number of items in above query, if null, the query is created by prepending 'select count(*) ' to the query above
	* @param filterColumnName The name of the column to be used for filtering (can be null to disable)
	* @param actions A closure that provides customized actions in the actions column of the table
	* @return a map that is ready to be rendered as a JSON message
	*/
	def jsonquery(dc,params,request,query,countQuery=null,listProperties=null,filterColumnName=null,actions=null) {
		def title=dc.getName();
		title=title.replaceAll (".*\\.", "")
		def propName=title[0].toLowerCase()+title.substring(1)
		
		if(!countQuery) {
			countQuery="select count(*) ${query}"
		}
		
		
		//def columns=listProperties ? listProperties : dc.listProperties
		def columns= dc.listProperties
		def sortName=columns[new Integer(params.iSortCol_0)]
		sortName=sortName? sortName:columns[0]
		
		def documentList
		
		//Create Id for the table
		def detailTableId="detailTable_"+dc
		detailTableId=detailTableId.replace(".","_")
		detailTableId=detailTableId.replace("class ","")
		
		
					
		if (params['objectId'] != null) {
			query = "${query} and (${params.property}.id=${params.objectId})"
			countQuery= "${countQuery} and (${params.property}.id=${params.objectId})"
		}
		def iTotalRecords=dc.executeQuery(countQuery)[0]
		def iTotalDisplayRecords=iTotalRecords
		if (filterColumnName && params.sSearch) {
			query = "${query} and (${filterColumnName} like '${params.sSearch}%')"
			countQuery= "${countQuery} and (${filterColumnName} like '${params.sSearch}%')"
			iTotalDisplayRecords=dc.executeQuery(countQuery)[0]
		}
		
		query="${query} order by ${sortName} ${params.sSortDir_0}" 

		documentList=dc.executeQuery(query,[],[max:params.iDisplayLength,offset:params.iDisplayStart,order:params.sSortDir_0,sort:sortName])
		
		def aaData=[]
		documentList.each { doc ->
			def inLine=[]
			columns.each {
				inLine +=doc."${it}".toString()
			}
			def baseUrl=request.contextPath
			if(!actions) {
				actions= { dok, env -> """<span class="list-action-button ui-state-default" onclick="dialog.formDialog(${dok.id},'${propName}',{ refresh : '${detailTableId}'}, null)">edit</span>&nbsp;<span class="list-action-button ui-state-default" onclick="dialog.deleteDialog(${dok.id},'${propName}',{ refresh : '${detailTableId}'}, null)">&times;</span>""" }
			}
			inLine+=actions(doc,['detailTableId':detailTableId])
			def aaLine=[inLine]
			aaData+=(aaLine)
		}

		def json = [sEcho:params.sEcho,iTotalRecords:iTotalRecords,iTotalDisplayRecords:iTotalDisplayRecords,aaData:aaData]
		return json
	}
	
	
    
    
}
