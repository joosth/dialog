package org.open_t.dialog

import java.text.SimpleDateFormat

import org.apache.commons.io.FileUtils

class FileService {

	static transactional = false

	def grailsApplication

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
			def diPath=filePath(dc,params.identifier,fileCategory)
			def destFile= new File("${diPath}/${filename}")
			FileUtils.copyFile(tempFile,destFile)
			tempFile.delete()
		}

		def res=[path:tempFile.absolutePath,name:tempFile.name,success:true,mimetype:mimetype,identifier:params.identifier,sFileName:params.sFileName]
		return res
	}

	def pack(n) {
		String s= Long.toString(new Long(n),36)
		return String.format("%1\$8s", s).replace(' ', '0')
	}

	def packedPath(n) {
		String s=pack(n)
		return s.substring(0,2)+"/"+s.substring(2,4)+"/"+s.substring(4,6)+"/"+s.substring(6,8)
	}

	// TODO offer possibility to provide alternate location per category.

	def relativePath(dc,id,fileCategory) {
		def name=dc.getName();
		name=name.replaceAll (".*\\.", "")

		Boolean flag=dc.methods.collect { method -> method.name }.contains("getFolderPath")

		if (flag) {
			def dcInstance=dc.get(id)
			return dcInstance.getFolderPath(fileCategory)
		} else {
			return "${fileCategory}/${name}/${packedPath(id)}"
		}
	}

	def filePath(dc,id,fileCategory) {
		def basePath=grailsApplication.config.dialog.files.basePath
		def name=dc.getName();
		name=name.replaceAll (".*\\.", "")
		return "${basePath}/${relativePath(dc,id,fileCategory)}"
	}

	// TODO offer possibility to provide alternate location per category.

	def fileUrl(dc,id,fileCategory) {
		def baseUrl=grailsApplication.config.dialog.files.baseUrl
		def name=dc.getName();
		name=name.replaceAll (".*\\.", "")
		return "${baseUrl}/${fileCategory}/${name}/${packedPath(id)}"
	}

	def filelist(dc,params,fileCategory="images") {
		def format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale('nl'))
		def diUrl=fileUrl(dc,params.id,fileCategory)

		def aaData=[:]
		//def baseUrl=request.contextPath
		if(params.id&& params.id!="null") {
			File dir = new File(filePath(dc,params.id,fileCategory))
			aaData=dir.listFiles().collect { file ->

				[0:"""<a href="${diUrl}/${file.name}">${file.name}</a>""",
				 1:file.length(),
				 2:format.format(file.lastModified()),


				 3:"""<div class="btn-group"><span class="btn btn-small" onclick="dialog.deleteFile(${params.id},'${params.controller}','${file.name}',null)">&times;</span></div>"""]
			}.sort { file -> file[new Integer(params.iSortCol_0)] }

			if (params.sSortDir_0=="desc") {
				aaData=aaData.reverse()
			}
		}
		def json = [sEcho:params.sEcho,iTotalRecords:aaData.size(),iTotalDisplayRecords:aaData.size(),aaData:aaData]
	}

	def filemap(dc,params,fileCategory="images") {
		def diUrl=fileUrl(dc,params.id,fileCategory)
		def diPath=filePath(dc,params.id,fileCategory)
		File dir = new File(diPath)

		def map = dir.listFiles().collect { file ->
			[file:file,url:"${diUrl}/${file.name}"]
		}
		return map
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
		return text
	}

	def submitFile(dc,id,fileupload,fileCategory="images") {

		def fileInfo=fileupload.split("\\|")

		// create folder structure
		def diPath=filePath(dc,id,fileCategory)
		new File(diPath).mkdirs()
		// upload the file
		File file=new File(fileInfo[1])
		if (file.exists()) {
			def destFile= new File("${diPath}/${fileInfo[0]}")
			FileUtils.copyFile(file,destFile)
			file.delete()
		}
	}

	// TODO error handling: return error that dialog can show
	def submitFiles(dc,params,fileCategory="images") {

		if (params.fileupload) {

			if (params.fileupload.class.name=="java.lang.String") {
				submitFile(dc,params.id,params.fileupload,fileCategory)
			} else {
				params.fileupload.each { fileupload ->
					submitFile(dc,params.id,fileupload,fileCategory)
				}
			}
		}
	}

	def deleteFile(dc,params,fileCategory="images") {
		File file = new File(filePath(dc,params.id,fileCategory)+"/"+params.filename);
		Boolean success= file.delete()
		def result=[success:success,mesage:"${params.filename} deleted"]
		return [result:result]
	}
}
