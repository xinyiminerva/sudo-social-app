"use strict";

// Define the object:
window.aztecs = window.aztecs || {};
window.aztecs.nav = {
	// Set global variables:
	NAME: "Navbar",

	/**
	* Initialize the navbar
	*/
	init: function() {
		$("body").prepend(Handlebars.templates[window.aztecs.nav.NAME + ".hbs"]({ uid: window.aztecs.storage.get("uid") }));
		$("#" + window.aztecs.nav.NAME + "-add").click(window.aztecs.newMessage.show);
		// Set the behavior of all username entries:
		$("#" + window.aztecs.nav.NAME + "-profile").click(window.aztecs.nav.clickUserProfile);
		$("#" + window.aztecs.nav.NAME + "-logout").click(window.aztecs.auth.logout);
		$("#" + window.aztecs.nav.NAME + "-help").click(function() { window.aztecs.help.show(); });
	},

	/**
	* Open the User Profile page
	*/
	clickUserProfile: function () {
        // Get the comment ID:
        var id = window.aztecs.storage.get("uid");
        // Goto the new page
        window.aztecs.profile.open(id);
    }
};

// ex: set ft=javascript ff=unix ts=4 sw=4 tw=0 noet :

