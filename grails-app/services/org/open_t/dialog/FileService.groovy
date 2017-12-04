/*
 * Dialog
 *
 * Copyright 2009-2017, Open-T B.V., and individual contributors as indicated
 * by the @author tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License
 * version 3 published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see http://www.gnu.org/licenses
 */
package org.open_t.dialog

import java.net.URLDecoder
import java.text.SimpleDateFormat

import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FileFileFilter
import org.apache.commons.io.IOUtils

/**
 * 11/13/2017 - Improved the path calculation methods to now handle longer file
 * paths.
 * 11/30/2017 - Cleanup. Adjusted several methods (submitFile(...), submitFiles(...),
 * fetchFileStream(...), uploadFile(...) and getBasePath(...)) to now retrieve the
 * fileupload information from the session object provided by the controller.
 */
class FileService {

    /* STATIC VARIABLES */

    /**
     * java.lang.Integer to define the default depth for a content element path.
     * @since 11/13/2017
     */
    static final int CONTENT_PATH_DEPTH = 14

    /**
     * java.lang.String to define the file category 'common'.
     * @since 11/13/2017
     */
    static final String COMMON_CATEGORY_NAME = "common"

    /**
     * java.lang.String to define the file category 'images'.
     * @since 11/30/2017
     */
    static final String IMAGES_CATEGORY_NAME = "images"

    /**
     * java.lang.String to define the method 'getFolderPath'.
     * @since 11/13/2017
     */
    static final String GET_FOLDER_PATH = "getFolderPath"

    /**
     * java.lang.String to define 'null'.
     * @since 11/13/2017
     */
    static final String STR_NULL = "null"

    /**
     * java.lang.String to define 'fileuploads' for session objects.
     * @since 11/30/2017
     */
    static final String SESSION_FILEUPLOADS = "fileuploads"

    /**
     * The FileService is NOT transactional.
     * @since 11/13/2017
     */
    static transactional = false


    /* GLOBAL VARIABLES */

	def grailsApplication
    def g = new org.codehaus.groovy.grails.plugins.web.taglib.ApplicationTagLib()

    /**
     * Upload a file in response to the dialog:upload tag.
     *
     * @param request The web request as provided by the controller.
     * @param params The parameter map as provided by the controller.
     * @param fileCategory (Optional) The category to store the files in; 'images'
     * by default.
     * @param dc (Optional) The domain class or String representing the domain class.
     * Null by default.
     * @return Map to be rendered as JSON containing the temporary file information.
     *
     * 11/30/2017 - Cleanup. Removed obsolete parameters in the return map and changed
     * some variable names.
     */
    def uploadFile(request, params, fileCategory = IMAGES_CATEGORY_NAME, dc = null) {
        def filenameHeader = request.getHeader("X-File-Name") ?: "unknown-file-name.bin"
        def filename = URLDecoder.decode(filenameHeader, "UTF-8")

        def inputStream = request.getInputStream()
        def mimetype = request.getHeader("Content-Type")

        log.debug("Mimetype for ${filename} is ${mimetype}.")

        def temporaryFile = File.createTempFile("upload", "bin")
        def outputStream = new FileOutputStream(temporaryFile)
        IOUtils.copy(inputStream, outputStream)

        outputStream.flush()
        inputStream.close()
        outputStream.close()

        def direct = params.direct
        def identifier = params.identifier
        def isDirect = (direct == true || direct == "true")
        def hasIdentifier = (identifier != null && identifier != "null" && identifier != "undefined")

        if (isDirect && hasIdentifier && dc != null) {
            def directPath = filePath(dc, identifier, fileCategory)
            def destinationFile = new File("${directPath}/${filename}")
            FileUtils.copyFile(temporaryFile, destinationFile)
            temporaryFile.delete()
        }

        return [
            temporaryPath: temporaryFile.absolutePath,
            temporaryName: temporaryFile.name,
            originalName: filename,
            originalMimetype: mimetype,
            identifier: identifier,
            message: "dialog.messages.uploadcompleted",
            success: true
        ]
    }

    /**
     * This calculates the full URL for the files of a domain object
     *
     * @param dc The domain class or a String representing the domain class. A string is allowed so there is no need to have access to the actual domain class
     * @param id The id of the domain object
     * @param fileCategory The file category. This is a string allowing a top-level tree split which is helpful if permissions of categories are different
     * @return The URL
     */
	def fileUrl(dc,id,fileCategory) {
		def baseUrl=grailsApplication.config.dialog.files.baseUrl
        def name = dc.class==java.lang.String ? dc : dc.getName()
		name=name.replaceAll (".*\\.", "")
		return "${baseUrl}/${fileCategory}/${name}/${packedPath(id)}"
	}

    /**
     * Return ready-to-be-rendered-as-JSON file list
     * This is used to provide a list of files attached to a domain object
     * The intended use is to place a construct like:
     * def filelist() {
     *   render fileService.filelist(DomainClass,params) as JSON
     * }
     * in the associated controller.
     */
	def filelist(dc,params,fileCategory="images",linkType="external",actions=null) {
		def format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale('nl'))
        log.debug "PARAMS: ${params}"
		def diUrl=fileUrl(dc,params.id,fileCategory)

		def data=[:]
		Integer recordsTotal=0
		if(params.id&& params.id!="null") {
			File dir = new File(filePath(dc,params.id,fileCategory))
			data=dir.listFiles().collect { file ->
                def downloadLink
                if (linkType=="external") {
                    downloadLink="${diUrl}/${file.name}"
                } else {
                    log.debug "params: ${params}"
                    downloadLink=g.createLink(action:"streamfile",id:params.id,params:[filename:file.name])
                }

                if(!actions) {
        			actions= { aParams,aFile ->
                        def actionsString="""<div class="btn-group">"""
                        def actionsParameter=aParams.actions?:"none"
                        def actionsList=actionsParameter.split(',')
                        // TODO add other actions ('show','edit')
                        if (actionsList.contains("delete")) {
                            actionsString +="""<a class="btn btn-default btn-sm" href="#" onclick="dialog.deleteFile(${aParams.id},'${aParams.controller}','${aFile.name}',null)" title="delete"><i class="fa fa-trash"></a>"""
                        }
                        actionsString+="</div>"
                        return actionsString
                    }
        		}

				[0:"""<a href="${downloadLink}" target="_blank">${file.name}</a>""",
				 1:file.length(),
				 2:format.format(file.lastModified()),
                 3: actions(params,file)]
			}.sort { file -> file[new Integer(params."order[0][column]")] }

			if (params."order[0][dir]"=="desc") {
				data=data.reverse()
			}
			recordsTotal=data.size()

			if (recordsTotal>0) {

				Integer firstResult = params.start ? new Integer(params.start) : 0
				Integer maxResults = params.length ? new Integer(params.length) : 10

				// pagination
				if (firstResult > recordsTotal) {
					firstResult = recordsTotal
				}
				if ((firstResult + maxResults) > recordsTotal) {
					maxResults = recordsTotal - firstResult
				}
				data = data[firstResult..firstResult + maxResults - 1]
			}

		}
		def json = [draw:params.draw,recordsTotal:recordsTotal,recordsFiltered:recordsTotal,data:data]
	}

    /**
     * File map for CKEditor
     *
     * @param dc The domain class
     * @param params The parameters as provided to the controller
     * @param fileCategory The file category, default is "images"
     * @return Map to be rendered as JSON
     */
	def filemap(dc,params,fileCategory="images",linkType="external") {
		def diUrl=fileUrl(dc,params.id,fileCategory)
		def diPath=filePath(dc,params.id,fileCategory)
		File dir = new File(diPath)

		def map = dir.listFiles().collect { file ->
            if (linkType=="external") {
    			[file:file,url:"${diUrl}/${file.name}"]
            } else {
                    [file:file,url:g.createLink(action:"streamfile",id:params.id,params:[filename:file.name])]
            }
		}
		return map
	}

    /**
     * Provide image list for tinyMCE
     *
     * @param dc The domain class
     * @param params The parameters as provided to the controller
     * @param fileCategory The file category, default is "images"
     * @return text for tinyMCE
     */
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

    /**
     * Provide media list for tinyMCE
     *
     * @param dc The domain class
     * @param params The parameters as provided to the controller
     * @param fileCategory The file category, default is "images"
     * @return text for tinyMCE
     */
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

    /**
     * Move the uploaded file to domain object folder directly.
     *
     * @param dc The domain class.
     * @param session The session object received from the controller.
     * @param id The document ID retrieved from the controllers' params.
     * @param fileuploadUUID The UUID with which we should retrieve the file from
     * the session object.
     * @param params The params object received from the controller.
     * @param fileCategory (Optional) The file category, default is "images".
     *
     * 11/30/2017 - Cleanup. Changed the fileupload handling. The fileupload information
     * is now retrieved from the session.
     */
    def submitFile(dc, session, id, fileuploadUUID, fileCategory = IMAGES_CATEGORY_NAME) {
        def fileupload = session[SESSION_FILEUPLOADS][fileuploadUUID]

        def path = fileupload.path
        def filename = fileupload.name
        /* Create the direct path. */
        def directPath = filePath(dc, id, fileCategory)
        new File(directPath).mkdirs()
        /* Upload the file. */
        def file = new File(path)
        if (file.exists()) {
            def destinationFile = new File("${directPath}/${filename}")
            FileUtils.copyFile(file, destinationFile)
            file.delete()
        }
    }

    /**
     * Move uploaded files to domain object folder. This allows files to be attached
     * to the original submission of a form after the domain object is created.
     *
     * @param dc The domain class.
     * @param session The session object received from the controller.
     * @param params The params object received from the controller.
     * @param fileCategory (Optional) The file category, default is "images".
     *
     * 11/30/2017 - Cleanup. Added the 'session' parameter.
     */
    def submitFiles(dc, session, params, fileCategory = IMAGES_CATEGORY_NAME) {
        def fileuploadUUIDs = params.fileupload
        if (fileuploadUUIDs) {
            def id = params.id
            if (fileuploadUUIDs instanceof java.lang.String) {
                submitFile(dc, session, id, fileuploadUUIDs, fileCategory)
            } else {
                fileuploadUUIDs.each { fileuploadUUID ->
                    submitFile(dc, session, id, fileuploadUUID, fileCategory)
                }
            }
        }
    }

    /**
     * Delete a file form a domain object's folder
     *
     * @param dc The domain class
     * @param params The parameters as provided to the controller
     * @param fileCategory The file category, default is "images"
     *
     * @return result map
     */
	def deleteFile(dc,params,fileCategory="images") {
		File file = new File(filePath(dc,params.id,fileCategory)+"/"+params.filename);
		Boolean success= file.delete()
		def result=[success:success,mesage:"${params.filename} deleted"]
		return [result:result]
	}

	/**
	 * Stream any given file over an HTTP response as an octet-stream.
	 *
	 * @param file The file to stream.
	 * @param response The HTTP response to write the file to.
	 * @since 09/01/2016
	 */
	def stream(def file, def name, def response, def contentType = "application/pdf") {
		response.setHeader("Content-Disposition", "inline; filename=\"${name}\"")
		response.setHeader("Content-Type", contentType)

        // Check if file is present and readable
        if (file && file.canRead()) {
    		def inputStream = new FileInputStream(file)
    		def bufsize = 100000
    		byte[] bytes = new byte[(int) bufsize]

    		def offset = 0
    		def len = 1
    		while (len > 0) {
    			len = inputStream.read(bytes, 0, bufsize)
    			if (len > 0)
    			response.outputStream.write(bytes, 0, len)
    			offset += bufsize
    		}

    		try {
    			response.outputStream.flush()
    		} catch (Exception e) {
    			/* Do nothing... */
    		}
        }
	}

    /**
	 * Stream a file to the outputstream of a response.
	 *
	 * @param dc The domain class.
     * @param params The parameters as provided to the controller.
     * @param fileCategory The file category.
     * @param name The name of the file.
     * @param response The response object as provided to the controller.
	 */
	def streamFile(def dc, def id, def fileCategory, def name, def response) {
        def filePath = filePath(dc, id, fileCategory) + "/${name}"
        def file = new File(filePath)
		stream(file, file.name, response)
	}

    /**
	 * Copy files from one domain object to another
	 *
	 * @param dc The domain class
     * @param fileCategory The file category,default is "images"
     * @param fromId The id of the source domain objetc
     * @param toId The id of the target domain object
	 */
    def copyFiles(dc=null, fileCategory="images", fromId, toId) {
        if( (fromId != null) && (toId != null) ) {
            File fromDir = new File(filePath(dc,fromId,fileCategory))
            File toDir = new File(filePath(dc,toId,fileCategory))
            FileUtils.copyDirectory(fromDir,toDir,FileFileFilter.FILE)
        }
    }

    /**
     * Fetch a file input stream from a fileupload that was generated by JS.
     *
     * @param path The path where the file can be found.
     * @return A FileInputStream instance if there was a file found at the fileupload
     * location. Null if otherwise.
     * @since 08/30/2016
     *
     * 11/30/2017 - Cleanup. Removed all parameters and only set one parameter: path.
     */
    def fetchFileStream(path) {
        def file = new File(path)
        if (file.exists()) {
            return new FileInputStream(file)
        }

        return null
    }


    /* NON-VOID METHODS ON PACKING FILE PATHS */

    /**
     * Retrieve the base path from the grails applications' config.
     *
     * @since 03/09/2017
     * @return A string representing the base path.
     *
     * 11/30/2017 - This method wasn't generic. Therefore the base path has been
     * relocated to config.dialog.files.basePath
     */
    def getBasePath() {
        return grailsApplication.config.dialog.files.basePath
    }

    /**
     * Calculate the file path for the given parameters. Looks something like:
     * /var/opt/wfp/files/common/Document/00/00/00/ff
     * or:
     * /var/opt/wfp/files/common/Document/00/00/00/00/00/00/ff
     * depending on the number of layers.
     *
     * @param domainClass java.lang.String or java.lang.Object to define the domain
     * class we'd like to store files for.
     * @param id The document ID.
     * @param category (Optional) The file category. 'common' by default.
     * @return A string with the path for the file to be saved.
     *
     * 08/01/2017 - Removed the dependency to Dialog's File Service. Set the calculation
     * of the path to the calculation methods in this class.
     */
    def filePath(domainClass, id, category = COMMON_CATEGORY_NAME) {
        def basePath = getBasePath()
        def path = "${basePath}/${relativePath(domainClass, id, category)}"
        def file = new File(path)
        if (!file.exists()) {
            file.mkdirs()
        }

        return path
    }

    /**
     * Calculate a string from a Long. This will provide a hexadecimal approach
     * for path calculation later on.
     *
     * @param no An arbitrary number. Could be an ID from a domain class.
     * @param depth (Optional) The depth of the string formatting (CONTENT_PATH_DEPTH
     * by default).
     * @return A java.lang.String that is formatted to hexadecimally represent a
     * long. For example: 00000000ff
     *
     * @since 08/01/2017
     */
    def pack(no, depth = CONTENT_PATH_DEPTH) {
        if (null == no || STR_NULL == no) {
            no = 0
        }

        def balance = Long.toString(new Long(no), 36)
        return String.format("%1\$${depth}s", balance).replace(" ", "0")
    }

    /**
     * Calculate the actual path from a pack. Will first create a pack like this:
     * 00000000ff
     * and convert it to a path like this:
     * 00/00/00/00/ff
     *
     * @param no An arbitrary number. Could be an ID from a domain class.
     * @return The calculated packed path.
     *
     * @since 08/01/2017
     */
    def packedPath(no) {
        def balance = pack(no)
        def balancedPath = ""
        def length = balance.length() / 2 - 1
        (0..(length)).each { i ->
            def subBalance = balance.substring(i * 2, i * 2 + 2)
            balancedPath += "/${subBalance}"
        }

        /* Finally, remove the first slash and return. */
        return balancedPath.substring(1)
    }

    /**
     * Calculate a relative path (relative to the domain class).
     *
     * @param domainClass java.lang.String or java.lang.Object to define the domain
     * class we'd like to store files for.
     * @param id The document ID.
     * @param category (Optional) The file category. 'common' by default.
     * @return Either the folder path of the domain class if it has a folder path,
     * or a calculated relative path like 'common/Document/00/00/00/00/ff'.
     *
     * @since 08/01/2017
     */
    def relativePath(domainClass, id, category = COMMON_CATEGORY_NAME) {
        def name = null
        def hasFolderPathMethod = false
        if (domainClass.getClass() == java.lang.String) {
            name = domainClass
        } else {
            name = domainClass.getName()
            hasFolderPathMethod = domainClass.methods.collect { method -> method.getName() }.contains(GET_FOLDER_PATH)
        }

        name = name.replaceAll(".*\\.", "")
        if (hasFolderPathMethod) {
            def domainClassInstance = domainClass.get(id)
            return domainClassInstance.getFolderPath(category)
        } else {
            return "${category}/${name}/${packedPath(id)}"
        }
    }
}
