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
package org.open_t.dialog

import grails.util.Holders

import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.web.servlet.support.RequestContextUtils as RCU

/**
 * List configuration delegate and helpers
 */
class ListConfig {
	String name
	String controller
	String action='jsonlist'
	Boolean filter=false
	def toolbar=false
	def newButton=true
	def rowreordering=false
	def idName="id"
	List<ListConfigColumn> columns = []

	def configure (Closure closure) {
		closure.setDelegate(this)
		closure.setResolveStrategy(Closure.DELEGATE_FIRST)
		closure()
		return this
	}

    /**
     * Class to hold column information
     */
	class ListConfigColumn {
		String name
		Boolean sortable=false
		Boolean filter=false
	}

    /**
     * Adds a column
     * @param The params to populate the column with (name,sortable,filter)
     */
	def column (params){
		columns.add (new ListConfigColumn(params))
	}

	def actions=['dialog','delete']

    /**
     * Renders out the actions column based on the actions list property
     */
	def renderActions = { props ->
		def s="""<div class="btn-group">"""

		def applicationContext=Holders.getApplicationContext()
		def messageSource = applicationContext.getBean("messageSource")

		def webUtils = WebUtils.retrieveGrailsWebRequest()
		def request=webUtils.getCurrentRequest()
		def locale = RCU.getLocale(request)
		actions.each { action ->
			String defaultLabel = messageSource.getMessage("list.action.${action}.label".toString(),null, "${action}",locale)
			String label = messageSource.getMessage("list.${name}.action.${action}.label".toString(),null, defaultLabel,locale)
			switch(action) {
				case "delete":
					s+="""<a class="btn btn-default btn-sm" href="#" onclick="dialog.deleteDialog('${props.itemId}','${props.propName}',{ refresh : '${props.detailTableId}'}, null)" title="${label}"><i class="fa fa-trash"></a>"""
				break
                case "show":
                    s+="""<a class="btn btn-default btn-sm" href="#" onclick="dialog.formDialog('${props.itemId}','${this.controller}',{ dialogname:'${action}',nosubmit:true,refresh : '${props.detailTableId}'}, null)" title="${label}"><i class="fa fa-search"></i></a>"""
                break
				default:
					s+="""<a class="btn btn-default btn-sm" href="#" onclick="dialog.formDialog('${props.itemId}','${this.controller}',{ dialogname:'${action}',refresh : '${props.detailTableId}'}, null)" title="${label}"><i class="fa fa-pencil"></i></a>"""
			}
		}
		s+="""</div>"""
		return s
	}

    /**
     * Renders a list with pagination of the entire datalist
     * This requires the entire datalist to be present which is inefficient
     * User renderList if you can fetch the appropriate list section and total in more efficient manner
     *
     * @param datalist The list of items
     * @param params The params as provided to the controller
     * @return ready-to-be-rendered-as-JSON data
     */

	def paginateList(datalist,params) {
        if (filter) {
            if (params["search[value]"]) {
                String searchString=params["search[value]"].toLowerCase()
                datalist=datalist.findAll { record ->
                    getFilterColumns().find { filterColumn ->
                        record[filterColumn].toString().toLowerCase().contains(searchString)
                    }
                }
            }
        }

        def totalRecords=datalist.size()
		if (totalRecords>0) {
			if (params."order[0][column]" && params."order[0][column]".length()>0) {
				def columnName=params."order[0][column]"
				def name=this.columns[new Integer(columnName)].name
				datalist=datalist.sort { it."${name}" }
			}
			if (params."order[0][dir]"=='desc') {
				datalist=datalist.reverse()
			}

			Integer firstResult=params.start?new Integer(params.start):0
			Integer maxResults=params.length?new Integer(params.length):10

			// pagination
			if (firstResult>totalRecords) { firstResult=totalRecords }
			if ((firstResult+maxResults)>totalRecords) {maxResults=totalRecords-firstResult}
			datalist=datalist[firstResult..firstResult+maxResults-1]
		}
		renderList (datalist,totalRecords,params)
	}

    /**
     * Renders a list as JSON
     * Provides pagination controls based on the totalRecords value provided
     * Which is the most effecive way of providing pagination but you need an api that supports this to fetch the actual data
     *
     * @param datalist The list of items to show
     * @param totalRecords The total number of records (of which datalist is a subset)
     * @param params The params as provided to the controller
     * @return ready-to-be-rendered-as-JSON data
     */
	def renderList(datalist,totalRecords,params) {
		def data=[]
		datalist.each { item ->
            def row =[DT_RowId:item.id]
			def col=0
			this.columns.each { column ->
				def val=item."${column.name}"

				// Convert date to String -- if we don't do it here the JSON converter will do it AND correct it for locale
				if (val && val.class == Date) {
					val=val.format("yyyy-MM-dd'T'HH:mm:ss")
					val=val.replaceAll("T00:00:00","")
				}

				row.put(col,val)
				col++
			}

			def props=[detailTableId:"detailTable_${this.name}",propName:controller,itemId:item."${idName}"]
			def actionsString=renderActions(props)

			row.put(col,actionsString)
			data+=row
		}

		return [draw:params.draw,recordsTotal:totalRecords,recordsFiltered:totalRecords,data:data]
	}

    /**
     * Get the columns that have filtering switched on
     * @return The columns that have filtering switched on
     */
	def getFilterColumns() {
		columns.findAll { it.filter }.collect { it.name }
	}
}
