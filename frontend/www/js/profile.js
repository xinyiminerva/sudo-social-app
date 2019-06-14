"use strict";

// Define the object:
window.aztecs = window.aztecs || {};
window.aztecs.profile = {
	// Declare global variables:
	NAME: "UserProfile",
	bio: "",
	uid: "",

	/**
	* Initialize the user profile.
	*/
	init: function() {
		// Check the document mode:
		var mode = window.aztecs.location.getMode();
		var uid = window.aztecs.location.getUid();
		if (mode !== "u" || uid === "")
			return;

		// Retrieve the data:
		window.aztecs.requests.handler("GET", "/users/" + uid, null, function (user) {
			console.log("Opening the User Profile Page");
			window.aztecs.profile.bio = user.bio;
			window.aztecs.profile.uid = user.uid;
			$("body").append(Handlebars.templates[window.aztecs.profile.NAME + ".hbs"]({
				target: user,
				uid: window.aztecs.storage.get("uid")
			}));
			window.aztecs.profile.show();
			$("#" + window.aztecs.profile.NAME + "-Update").click(window.aztecs.profile.change);
			$("#" + window.aztecs.profile.NAME + "-bio").val(user.bio);
			$("#" + window.aztecs.profile.NAME + "-Close").click(window.aztecs.profile.reset);
		});
	},

	/**
	* Hide the modal
	*/
	hide: function() {
		$("#" + window.aztecs.profile.NAME).modal("hide");
	},

	/**
	* Reset the view
	*/
	reset: function() {
		window.aztecs.location.mode = "";
		window.aztecs.location.refresh();
	},

	/**
	* Open a User's profile
	*/
	open: function(uid) {
		window.aztecs.location.mode = "u";
		window.aztecs.location.uid = uid;
		window.aztecs.location.refresh();
	},

	/**
	* Show the content of the profile
	*/
	show: function() {
		$("#" + window.aztecs.profile.NAME).modal("show");
	},

	/**
	* Update the content of the user's profile in the database
	*/
	change: function() {
		// Read data:
		var bio = $("#" + window.aztecs.profile.NAME + "-bio").val();

		// Check that edits were made:
		if (bio === window.aztecs.profile.bio) {
			alert("Error: no changes have been made ...");
			return;
		}

		// Send the request:
		window.aztecs.requests.handler("PUT", "/users/" + window.aztecs.profile.uid, { bio: bio }, function() { window.aztecs.location.refresh(); });
	}
};

// ex: set ft=javascript ff=unix ts=4 sw=4 tw=0 noet :

