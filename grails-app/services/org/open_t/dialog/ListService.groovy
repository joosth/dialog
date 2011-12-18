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
	
	def wf4pConfigService
	def grailsApplication
    boolean transactional = true
	
	// Data provider for data list

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
        			actions= { dok, env -> """<span class="list-action-button ui-state-default" onclick="formDialog(${dok.id},'${propName}',{ refresh : '${detailTableId}'}, null)">edit</span>&nbsp;<span class="list-action-button ui-state-default" onclick="deleteDialog(${dok.id},'${propName}',{ refresh : '${detailTableId}'}, null)">&times;</span>""" }
        		}
        		inLine+=actions(doc,['detailTableId':detailTableId])
        		def aaLine=[inLine]
        		aaData+=(aaLine)
    		}

    		def json = [sEcho:params.sEcho,iTotalRecords:iTotalRecords,iTotalDisplayRecords:iTotalDisplayRecords,aaData:aaData]
        	return json
        }
    
    
}
