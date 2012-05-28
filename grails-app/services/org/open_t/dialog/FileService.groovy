package org.open_t.dialog
import java.io.File;
import java.io.InputStream;

import java.io.Reader;
import grails.converters.*
import org.codehaus.groovy.grails.commons.*
import org.apache.commons.io.FileUtils
import java.text.*

class FileService {

	def uploadFile(request,params,fileCategory="images",dc=null) {
		def filename
		def is
		def mimetype
		if (params.qqfile.class.name=="org.springframework.web.multipart.commons.CommonsMultipartFile") {
			filename=params.qqfile.getOriginalFilename()
			is =params.qqfile.getInputStream()
			mimetype=params.qqfile.getContentType()
		} else {
			filename=params.qqfile
			is =request.getInputStream()
			mimetype=request.getHeader("Content-Type")
		}
		
		char[] cbuf=new char[100000]
		byte[] bbuf=new byte[100000]
		
		File tempFile=File.createTempFile("upload", "bin");
		OutputStream os=new FileOutputStream(tempFile)
		
		int nread =is.read(bbuf, 0, 100000)
		int total=nread
		while (nread>0) {
			os.write(bbuf, 0, nread)
			nread =is.read(bbuf, 0, 100000)
			if (nread>0)
				total+=nread
		}
		os.flush()

		is.close()
		os.close()
		if (params.direct && dc!=null && (params.identifier!=null && params.identifier!="null")) {
			println "DIRECT"
			def diPath=filePath(dc,params.identifier,fileCategory)
			def destFile= new File("${diPath}/${filename}")
			println "Copying to ${diPath}/${filename}"
			FileUtils.copyFile(tempFile,destFile)
			//tempFile.delete()
		}
		
		def res=[path:tempFile.absolutePath,name:tempFile.name,success:true,mimetype:mimetype,identifier:params.identifier,sFileName:params.sFileName]
		return res		
	}
	// TODO offer possibility to provide alternate location per category.
	// TODO offer balanced tree	
	def filePath(dc,id,fileCategory) {
		def basePath=ConfigurationHolder.config.bookstore.files.basePath
		def name=dc.getName();
		name=name.replaceAll (".*\\.", "")
		return "${basePath}/${fileCategory}/${name}/${id}"
	}
	
	// TODO offer possibility to provide alternate location per category.
	// TODO offer balanced tree
	def fileUrl(dc,id,fileCategory) {
		def baseUrl=ConfigurationHolder.config.bookstore.files.baseUrl
		def name=dc.getName();
		name=name.replaceAll (".*\\.", "")
		return "${baseUrl}/${fileCategory}/${name}/${id}"
	}
	
	def filelist(dc,params,fileCategory="images") {
		/*
		def defaultDomainClass = new DefaultGrailsDomainClass( dc )
		
		def domainClassInstance
		if (params.id && params.id !='null') {
			if (params.id.contains("_")){
				params.id=params.id.split("_")[1]
			}
			domainClassInstance = domainClass.get(params.id)
		} else {
			domainClassInstance = domainClass.newInstance()		
		}
		*/
		
		def format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale('nl'))
		
		
		
		def aaData=[:]
		//def baseUrl=request.contextPath
		if(params.id&& params.id!="null") {
			File dir = new File(filePath(dc,params.id,fileCategory))
			aaData=dir.listFiles().collect { file ->

				[0:file.name,
				 1:file.length(),
				 2:format.format(file.lastModified()),
				 3:"""<span class="list-action-button ui-state-default" onclick="dialog.deleteFile(${params.id},'${params.controller}','${file.name}',null)">&times;</span>"""]
			}.sort { file -> file[new Integer(params.iSortCol_0)] }
		
			if (params.sSortDir_0=="desc") {
				aaData=aaData.reverse()
			}
		}
		def json = [sEcho:params.sEcho,iTotalRecords:aaData.size(),iTotalDisplayRecords:aaData.size(),aaData:aaData]
	}
	
	def imagelist(dc,params,fileCategory="images") {
		def diUrl=fileUrl(dc,params.id,fileCategory)
		def diPath=filePath(dc,params.id,fileCategory)
		File dir = new File(diPath)

		String text="var tinyMCEImageList = new Array("
		dir.eachFile { file ->
			text+="""\n["${file.name}", "${diUrl}/${file.name}"],"""
		}
		if (text[text.length()-1]==",") {
			text=text.substring(0,text.length()-1)
		}
		text+=");"
		//render text as text
		return text
	}
	
	def medialist(dc,params,fileCategory="media") {
		def diUrl=fileUrl(dc,params.id,fileCategory)
		def diPath=filePath(dc,params.id,fileCategory)
		File dir = new File(diPath)

		String text="var tinyMCEMediaList = new Array("
		dir.eachFile { file ->
			text+="""\n["${file.name}", "${diUrl}/${file.name}"],"""
		}
		if (text[text.length()-1]==",") {
			text=text.substring(0,text.length()-1)
		}
		text+=");"
		//render text as text
		return text
	}
	
	
	def submitFile(dc,id,fileupload,fileCategory="images") {
	
		println "Uploading ${fileupload}"
		def fileInfo=fileupload.split("\\|")
		println "fileInfo=${fileInfo}"
		// create folder structure
		def diPath=filePath(dc,id,fileCategory)
		new File(diPath).mkdirs()
		// upload the file
		File file=new File(fileInfo[1])
		if (file.exists()) {
			println "SUBMITFILES: moving ${file} ${fileInfo[1]} to ${diPath}"
			def destFile= new File("${diPath}/${fileInfo[0]}")
			FileUtils.copyFile(file,destFile)
			file.delete()
		}		
	}
	
	
	// TODO make this work for multiple files
	// TODO error handling: return error that dialog can show
	def submitFiles(dc,params,fileCategory="images") 
	{
		
		if (params.fileupload) {
			println "Uploading ${params.fileupload} class ${params.fileupload.class.name}"
			if (params.fileupload.class.name=="java.lang.String") {
				submitFile(dc,params.id,params.fileupload,fileCategory)
			} else {
				params.fileupload.each { fileupload ->
					submitFile(dc,params.id,fileupload,fileCategory)					
				}
			}
			
			/*
			
			String fileupload=params.fileupload
			def fileInfo=fileupload.split("\\|")
			println "fileInfo=${fileInfo}"
			//def dcInstance=dc.get(params.id)
			// create folder structure
			def diPath=filePath(dc,params.id,fileCategory)
			new File(diPath).mkdirs()
			// upload the file
			File file=new File(fileInfo[1])
			println "SUBMITFILES: moving ${file} ${fileInfo[1]} to ${diPath}"
			def destFile= new File("${diPath}/${fileInfo[0]}")			
			FileUtils.copyFile(file,destFile)
			file.delete()
			*/						
		}
	}
	
	def deleteFile(dc,params,fileCategory="images") {
		File file = new File(filePath(dc,params.id,fileCategory)+"/"+params.filename);
		Boolean success= file.delete()
		def result=[success:success,mesage:"${params.filename} deleted"]
		return [result:result]
	}
	
}
