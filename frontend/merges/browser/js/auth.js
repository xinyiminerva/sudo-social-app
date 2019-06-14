"use strict";

// Define the object:
window.aztecs = window.aztecs || {};
window.aztecs.auth = {
	/**
	* Handle the result of the OAuth Authentication
	*/
	onSignin: function(googleUser) {
		var id_token = googleUser.getAuthResponse().id_token;
		window.aztecs.requests.handler("GET", "/users?authenticate=" + id_token, null, function(user) {
			// Set the necessary cookies:
			window.aztecs.storage.set("uid", user.id);
			window.aztecs.storage.set("token", user.token);
			console.log("User ID: " + user.id + "\nUser Token: " + user.token);
            window.location.reload();
		});
	},

	/**
	* Handle signin errors
	*/
	onSigninError: function(err) {
		alert("ERROR: Unable to signin: " + err);
	},

	/**
	* Attempt to sign the user in.
	*/
	login: function() {
		if (typeof gapi === "undefined") {
			alert("Error: unable to load Google Signin Services. Press \"Ok\" to try again.");
			window.aztecs.location.refresh();
		}
		gapi.load("auth2", function() {
			var auth2 = gapi.auth2.init({ client_id: "974723804142-m67ipjompus4i4ts8i43n8qndjh8ts8d.apps.googleusercontent.com" });
			auth2.then(function() {
				auth2.signIn({ scope: "profile email" }).then(window.aztecs.auth.onSignin);
			}, window.aztecs.auth.onSigninError);
		});
	},

	/**
	* Sign the user out
	*/
	logout: function() {
		window.aztecs.storage.remove("uid");
		window.aztecs.storage.remove("token");
		window.aztecs.location.refresh();
	}
};
