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
        build "org.grails.plugins:release:3.1.1"
        runtime "org.grails.plugins:resources:1.2.14"
    }

}
