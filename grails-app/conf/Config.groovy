log4j = {
	error 'org.codehaus.groovy.grails',
	      'org.springframework',
	      'org.hibernate',
	      'net.sf.ehcache.hibernate'
}
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*', '/ext/*']
grails.resources.adhoc.includes = ['/images/**', '/css/**', '/js/**', '/plugins/**', '/ext/**']

//don't rewrite CKEditor CCS files, it breaks links to image resources..
grails.resources.mappers.csspreprocessor.excludes = ['**/ext/ckeditor/skins/**']
