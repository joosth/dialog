class DialogGrailsPlugin {
    // the plugin version
    def version = "2.0.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = ['twitterBootstrap': "2.2.2"]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def author = "Joost Horward"
    def authorEmail = "joost@open-t.nl"
    def title = "Dialog"
    def description = '''\\
Provides easy-to-maintain CRUD popup dialogs and lists with JQuery, JQuery datatables and twitter bootstrap.
'''

    // URL to the plugin's documentation
	def documentation = "http://www.open-t.nl/catviz/p/projects/dialog"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
	def license = "AGPL3"

    // Details of company behind the plugin (if there is one)
	def organization = [ name: "Open-T", url: "http://www.open-t.nl" ]

    // Any additional developers beyond the author specified above.
	def developers = [ [ name: "Peter van Gestel", email: "peter@open-t.nl" ]]

    // Location of the plugin's issue tracker.
	def issueManagement = [ system: "GITHUB", url: "https://github.com/joosth/dialog/issues" ]

    // Online location of the plugin's browseable source code.
	def scm = [ url: "https://github.com/joosth/dialog.git" ]

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // TODO Implement code that is executed when the application shuts down (optional)
    }
}
