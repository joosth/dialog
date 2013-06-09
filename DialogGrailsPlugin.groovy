class DialogGrailsPlugin {
	def version = "2.0.0"
	def grailsVersion = "2.0 > *"
	def dependsOn = ['twitterBootstrap': "2.2.2"]

	def author = "Joost Horward"
	def authorEmail = "joost@open-t.nl"
	def title = "Dialog"
	def description = 'Provides easy-to-maintain CRUD popup dialogs and lists with JQuery, JQuery datatables and twitter bootstrap.'

	def documentation = "http://www.open-t.nl/catviz/p/projects/dialog"

	def license = "AGPL3"
	def organization = [ name: "Open-T", url: "http://www.open-t.nl" ]
	def developers = [ [ name: "Peter van Gestel", email: "peter@open-t.nl" ]]
	def issueManagement = [ system: "GITHUB", url: "https://github.com/joosth/dialog/issues" ]
	def scm = [ url: "https://github.com/joosth/dialog.git" ]
}
