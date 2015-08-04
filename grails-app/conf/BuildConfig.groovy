grails.project.work.dir = 'target'

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {

	inherits 'global'
	log 'warn'

	repositories {
		grailsCentral()

        grailsPlugins()
        grailsHome()

        mavenCentral()
        mavenRepo "http://repository.codehaus.org"
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://repository.jboss.com/maven2/"
        mavenRepo "https://repo.grails.org/grails/plugins"


	}

	plugins {
		runtime 'org.grails.plugins:twitter-bootstrap:2.3.2.2',':resources:1.2.14'
        compile ":resources:1.2.14"
    }
}
