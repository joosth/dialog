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

import java.text.SimpleDateFormat

import org.apache.commons.lang.WordUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.grails.commons.GrailsDomainClassProperty
import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.web.servlet.support.RequestContextUtils as RCU
import org.springframework.transaction.annotation.Transactional


/*
 * Provide list handling service
 */
class ListService {

	static transactional = false

	def grailsApplication
	def messageSource

	/**
	* Converts value to a display string
	* @param value The value to be shown
	* @return String a string that represents the value in human-readable form
	*/

	def getDisplayString(value) {
		if (value==null) {
			return ""
		}
		try {
			def type=value.getClass().getName()

			if (type== "java.lang.String") {
				return value
			}

			def webUtils = WebUtils.retrieveGrailsWebRequest()
			def request=webUtils.getCurrentRequest()
			def locale = RCU.getLocale(request)

			if (type== "boolean" || type=="java.lang.Boolean") {
				return messageSource.getMessage("dialog.checkBox.${value}.label".toString(),null, value.toString(),locale)
			}

			if (type== "java.util.Date" || type=="java.sql.Timestamp") {
				def dateFormat
				if (value.format ("HH:mm:ss")=="00:00:00") {
					dateFormat= messageSource.getMessage("dialog.date.format".toString(),null, "yyyy-MM-dd",locale)
				} else {
					dateFormat= messageSource.getMessage("dialog.datetime.format".toString(),null, "yyyy-MM-dd hh:mm:ss",locale)
				}
				def format=new SimpleDateFormat(dateFormat,locale)
				return format.format(value)
			}

			return value.toString()
		} catch (Exception e) {
			println e.message
			return "?"
		}
	}

    /**
	* Generates a JSON response to feed the datalist
	* @param dc The domain class to be used
	* @param params The parameters from the http request
	* @param request the HTTPServletRequest
	* @param filterColumnNames The name of the column to be used for filtering (can be null to disable)
	* @param actions A closure that provides customized actions in the actions column of the table
	* @return a map that is ready to be rendered as a JSON message
	*/
	@Transactional(readOnly=true)
	def jsonlist(dc,params,request,filterColumnNames=null,actions=null) {
        	def title=dc.getName();
        	title=title.replaceAll (".*\\.", "")
        	def propName=title[0].toLowerCase()+title.substring(1)

			def listConfig=null
			def columns

			if (new DefaultGrailsDomainClass(dc).hasProperty("listConfig")) {
				listConfig=dc.listConfig
				columns=dc.listConfig.columns.collect { it.name }
				if (!filterColumnNames) {
					filterColumnNames=dc.listConfig.filterColumns
				}
			} else if  (new DefaultGrailsDomainClass(dc).hasProperty("listProperties")) {
				columns=dc.listProperties
			}

			def sortName=columns[new Integer(params."order[0][column]")]
     		sortName=sortName? sortName:columns[0]

			def documentList
			def recordsTotal=dc.count()
			def recordsFiltered

			//Create Id for the table
			def detailTableId="detailTable_"+dc
			detailTableId=detailTableId.replace(".","_")
			detailTableId=detailTableId.replace("class ","")

			if (params['objectId'] != null) {
				if (params.objectId !='null') {
					def filterMethod = "findAllBy"+WordUtils.capitalize(params.property)
					def masterDomainObj = grailsApplication.getClassForName(params.objectClass).get(params.objectId)
					documentList=dc."$filterMethod"(masterDomainObj, [max:params.length,offset:params.start,order:params."order[0][dir]",sort:sortName])
					recordsTotal =dc.executeQuery("select count(*) as cnt from ${dc.getName()} as dc where ${params.property}=:object",[object:masterDomainObj])
					recordsFiltered=recordsTotal

				} else {
					recordsTotal=0
					recordsFiltered=0
				}
			} else {
				if (filterColumnNames && params."search[value]") {
					def fields
					if (String.isInstance(filterColumnNames)) {
						fields=[filterColumnNames]
					} else {
						fields=filterColumnNames
					}
					def where=fields.collect {"str(dc.${it}) like :term"}.join(" or ")
					def order=fields.collect {"dc.${it}"}.join(",")
					documentList=dc.findAll("from ${dc.getName()} as dc where ${where} order by ${order}",[term:'%'+params."search[value]"+'%'],[max:params.length,offset:params.start,order:params."order[0][dir]",sort:sortName])
					recordsFiltered =dc.executeQuery("select count(*) as cnt from ${dc.getName()} as dc where ${where}",[term:'%'+params."search[value]"+'%'])
				} else {
					documentList=dc.list([max:params.length,offset:params.start,order:params."order[0][dir]",sort:sortName])
					recordsFiltered = recordsTotal
				}
			}

    		def data=[]
            documentList.each { doc ->
        		def inLine=[DT_RowId:doc.id]
				def i=0
        		columns.each {
					// If the prop name contains a '.' it needs to be evaluated through a groovy shell
					// Doing so is considerably slower than the construct in the else
					if (it.contains(".")) {
						def val=Eval.me("doc",doc,"doc.${it}")
						inLine +=["${i}": getDisplayString(val)]
					} else {
						def val=""
						try {
							val=doc."${it}"
						} catch (Exception e) {
							val="!!!"
						}
						inLine +=["${i}": getDisplayString(val)]
					}
					i++
        		}
        		def baseUrl=request.contextPath

				def actionsString=null

				if (actions) {
					actionsString=actions(doc,['detailTableId':detailTableId])
				}

				if (actions==null && listConfig){
					actionsString=listConfig.renderActions(itemId:doc.id,propName:propName)
    			}
        		if(!actions && !listConfig) {
        			actions= { dok, env ->
                        """<div class="btn-group">""" +
                            """<a class="btn btn-default btn-sm" href="#" onclick="dialog.formDialog(${dok.id},'${propName}',{ refresh : '${detailTableId}'}, null)" title="edit"><i class="fa fa-pencil"></a>""" +
                            """<a class="btn btn-default btn-sm" href="#" onclick="dialog.deleteDialog('${dok.id}','${propName}',{ refresh : '${detailTableId}'}, null)" title="delete"><i class="fa fa-trash"></a>""" +
                        """</div>"""
                    }
					actionsString=actions(doc,['detailTableId':detailTableId])
        		}
        		inLine+=["${i}":actionsString]
				data+=inLine
    		}

    		def json = [draw:params.draw,recordsTotal:recordsTotal,recordsFiltered:recordsFiltered,data:data]
        	return json
        }

	/**
	* Generates a JSON response to feed the datalist from an arbitrary HQL query
	* @param dc The domain class to be used
	* @param params The parameters from the http request
	* @param request the HTTPServletRequest
	* @param query the HQL query
	* @param the HQL query that counts the number of items in above query, if null, the query is created by prepending 'select count(*) ' to the query above
	* @param filterColumnNames The name of the column to be used for filtering (can be null to disable)
	* @param actions A closure that provides customized actions in the actions column of the table
	* @param queryParams a parameter map for the query
	* @return a map that is ready to be rendered as a JSON message
	*/
	@Transactional(readOnly=true)

	def jsonquery(dc,params,request,query,countQuery=null,listProperties=null,filterColumnNames=null,actions=null,queryParams=[:]) {
		def title=dc.getName();
		title=title.replaceAll (".*\\.", "")
		def propName=title[0].toLowerCase()+title.substring(1)

		if(!countQuery) {
			countQuery="select count(*) ${query}"
		}

		def columns
		if (listProperties) {
			columns=listProperties
		} else if (new DefaultGrailsDomainClass(dc).hasProperty("listConfig")) {
			columns=dc.listConfig.columns.collect { it.name }
			if (!filterColumnNames) {
				filterColumnNames=dc.listConfig.filterColumns
			}
		} else if  (new DefaultGrailsDomainClass(dc).hasProperty("listProperties")) {
			columns=dc.listProperties
		}

		def sortName=columns[new Integer(params."order[0][column]")]
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
		def recordsTotal=dc.executeQuery(countQuery,queryParams)[0]
		def recordsFiltered=recordsTotal
		if (filterColumnNames && params."search[value]") {
			def fields
			if (String.isInstance(filterColumnNames)) {
				fields=[filterColumnNames]
			} else {
				fields=filterColumnNames
			}
			def where=fields.collect {"str(dc.${it}) like :term"}.join(" or ")
			def order=fields.collect {"dc.${it}"}.join(",")

			query = "${query} and (${where})"
			countQuery= "${countQuery} and (${where})"
			queryParams.put('term','%'+params."search[value]"+'%')


			recordsFiltered=dc.executeQuery(countQuery,queryParams)[0]
		}

		query="${query} order by ${sortName} ${params."order[0][dir]"}"

		documentList=dc.executeQuery(query,queryParams,[max:params.length,offset:params.start,order:params."order[0][dir]",sort:sortName])

		def data=[]
            documentList.each { doc ->
        		def inLine=[DT_RowId:doc.id]
				def i=0

				columns.each {
					// If the prop name contains a '.' it needs to be evaluated through a groovy shell
					// Doing so is considerably slower than the construct in the else
					if (it.contains(".")) {
						def val=Eval.me("doc",doc,"doc.${it}")
						inLine +=["${i}": getDisplayString(val)]
					} else {
						def val=""
						try {
							val=doc."${it}"
						} catch (Exception e) {
							val="!!!"
						}
						inLine +=["${i}": getDisplayString(val)]
					}
					i++
				}

        		def baseUrl=request.contextPath
        		if(!actions) {
        			actions = { dok, env ->
        			    """<div class="btn-group">""" +
        			        """<a class="btn btn-default btn-sm" href="#" onclick="dialog.formDialog(${dok.id},'${propName}',{ refresh : '${detailTableId}'}, null)" title="edit"><i class="fa fa-pencil"></i></a>""" +
        			        """<a class="btn btn-default btn-sm" href="#" onclick="dialog.deleteDialog('${dok.id}','${propName}',{ refresh : '${detailTableId}'}, null)" title="delete"><i class="fa fa-trash"></i></a>""" +
                        """</div>"""
                    }
        		}
        		inLine+=["${i}":actions(doc,['detailTableId':detailTableId])]
				data+=inLine
    		}

    		def json = [draw:params.draw,recordsTotal:recordsTotal,recordsFiltered:recordsFiltered,data:data]
    	return json
	}

	/**
	* DEPRECATED
	* Generates a JSON response to feed the datalist from a searchable query
	* @param dc The domain class to be used
	* @param params The parameters from the http request
	* @param request the HTTPServletRequest
	* @param query the HQL query
	* @param the HQL query that counts the number of items in above query, if null, the query is created by prepending 'select count(*) ' to the query above
	* @param filterColumnNames The name(s) of the column to be used for filtering (can be null to disable)
	* @param actions A closure that provides customized actions in the actions column of the table
	* @param queryParams a parameter map for the query
	* @return a map that is ready to be rendered as a JSON message
	*/
	@Transactional(readOnly=true)
	def jsonsearch(dc,params,request,query,listProperties=null,actions=null,queryParams=[:]) {
		def title=dc.getName();
		title=title.replaceAll (".*\\.", "")
		def propName=title[0].toLowerCase()+title.substring(1)


		def columns=listProperties ? listProperties : dc.listProperties
		//def columns= dc.listProperties
		def sortName=columns[new Integer(params."order[0][column]")]
		sortName=sortName? sortName:columns[0]

		def documentList

		//Create Id for the table
		def detailTableId="detailTable_"+dc
		detailTableId=detailTableId.replace(".","_")
		detailTableId=detailTableId.replace("class ","")


		if (params."search[value]") {
			query = params."search[value]"
		}
		def res=dc.search(query,[max:params.length,offset:params.start,order:params."order[0][dir]",sort:sortName])
		documentList=res.results

		def recordsTotal=res.total
		def recordsFiltered=recordsTotal



		def data=[]
			documentList.each { doc ->
				def inLine=[DT_RowId:doc.id]
				def i=0
				columns.each {
					inLine +=["${i}":doc."${it}".toString()]
					i++
				}
				def baseUrl=request.contextPath
				if(!actions) {
					actions = { dok, env ->
                        """<div class="btn-group">""" +
                            """<a class="btn btn-default btn-sm" href="#" onclick="dialog.formDialog(${dok.id},'${propName}',{ refresh : '${detailTableId}'}, null)" title="edit"><i class="fa fa-pencil"></a>""" +
                            """<a class="btn btn-default btn-sm" href="#" onclick="dialog.deleteDialog('${dok.id}','${propName}',{ refresh : '${detailTableId}'}, null)" title="delete"><i class="fa fa-trash"></a>""" +
                        """</div>"""
                    }
				}
				inLine+=["${i}":actions(doc,['detailTableId':detailTableId])]
				data+=inLine
			}

			def json = [draw:params.draw,recordsTotal:recordsTotal,recordsFiltered:recordsFiltered,data:data]
		return json
	}

	/**
	 * Move position of an item in a sortable list
     *
	 * @param dc The domain class
	 * @param params The parameters of the HTTP request. Should contain id and toPosition
	 * @return
	 */
	@Transactional
	def position(dc,params) {
		def defaultDomainClass = new DefaultGrailsDomainClass( dc )
		Map belongsToMap = defaultDomainClass.getStaticPropertyValue(GrailsDomainClassProperty.BELONGS_TO, Map.class)

		def movedItem=dc.get(params.id)
		Integer toPosition=new Integer(params.toPosition)
		movedItem.position=toPosition
		movedItem.save(flush:true)
		def items
		if (belongsToMap?.size() == 1 ) {
			String parentname=""
			belongsToMap.each { key, value ->
				parentname=key
			}
			def parent=movedItem."${parentname}"
			// Hibernate doesn't like =null, so we need to make an exception for that case.
			if (parent) {
				items=dc.findAll("from ${dc.getName()} where ${parentname}=:parent order by position asc",[parent:parent])
			} else {
				items=dc.findAll("from ${dc.getName()} where ${parentname} is null order by position asc")
			}
		} else {
			items=dc.findAll([sort:'position',order:'asc']) {}
		}
		int idx=1
		items.each { item ->
			if (item.id!=movedItem.id){
				if (idx==toPosition) {
					idx++
				}
				item.position=idx
				item.save()
				idx++
			}
		}
		return ['succes':true]
	}
}
