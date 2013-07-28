package org.open_t.dialog

import grails.util.Holders

import org.codehaus.groovy.grails.web.util.WebUtils
import org.springframework.web.servlet.support.RequestContextUtils as RCU

class ListConfig {
	String name
	String controller
	String action='jsonlist'
	Boolean bFilter=false
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

	class ListConfigColumn {
		String name
		Boolean sortable=false
		Boolean filter=false
	}

	def column (params){
		columns.add (new ListConfigColumn(params))
	}

	def actions=['dialog','delete']

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
					s+="""<span class="btn btn-small" onclick="dialog.deleteDialog('${props.itemId}','${props.propName}',{ refresh : '${props.detailTableId}'}, null)">${label}</span>"""
				break

				default:
					s+="""<span class="btn btn-small" onclick="dialog.formDialog('${props.itemId}','${this.controller}',{ dialogname:'${action}',refresh : '${props.detailTableId}'}, null)">${label}</span>"""
			}
		}
		s+="""</div>"""
		return s
	}

	def paginateList(datalist,params) {
		def totalRecords=datalist.size()
		if (totalRecords>0) {
			if (params.iSortCol_0 && params.iSortCol_0.length()>0) {
				def columnName=params.iSortCol_0
				def name=this.columns[new Integer(columnName)].name
				datalist=datalist.sort { it."${name}" }
			}
			if (params.sSortDir_0=='desc') {
				datalist=datalist.reverse()
			}

			Integer firstResult=params.iDisplayStart?new Integer(params.iDisplayStart):0
			Integer maxResults=params.iDisplayLength?new Integer(params.iDisplayLength):10

			// pagination
			if (firstResult>totalRecords) { firstResult=totalRecords }
			if ((firstResult+maxResults)>totalRecords) {maxResults=totalRecords-firstResult}
			datalist=datalist[firstResult..maxResults-1]
		}
		renderList (datalist,totalRecords,params)
	}


	def renderList(datalist,totalRecords,params) {
		def aaData=[]
		datalist.each { item ->
			def row=[:]
			def col=0
			this.columns.each { column ->
				//row.put(col,item."${column.name}")
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
			aaData+=row
		}

		return [sEcho:params.sEcho,iTotalRecords:totalRecords,iTotalDisplayRecords:totalRecords,aaData:aaData]
	}

	def getFilterColumns() {
		columns.findAll { it.filter }.collect { it.name }
	}
}
