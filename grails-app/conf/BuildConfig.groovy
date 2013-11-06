grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	legacyResolve false

	repositories {
		grailsPlugins()
        grailsHome()
        mavenLocal()
        grailsCentral()
        mavenCentral()


	}

	plugins {
		runtime 'org.grails.plugins:twitter-bootstrap:2.3.2',':resources:1.2.1'
	}
}
