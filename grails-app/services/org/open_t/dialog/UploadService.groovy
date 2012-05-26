package org.open_t.dialog
import java.io.File;
import java.io.InputStream;

import java.io.Reader;
import grails.converters.*

class UploadService {

	def uploadFile(request,params) {
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
		
		//def res = [success : true]
		//render res as JSON
		//text/html
		
		//render(contentType:"text/html",text:"{success:true}")
		return "{success:true}"
		
		
}
}
