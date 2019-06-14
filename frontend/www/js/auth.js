"use strict";

// Define the object:
window.aztecs = window.aztecs || {};
window.aztecs.auth = {
	/**
	* Handle the result of the OAuth Authentication
	*/
	onSignin: function(token) {
		window.aztecs.requests.handler("GET", "/users?authenticate=" + token.idToken, null, function (user) {
			// Set the necessary cookies:
			window.aztecs.storage.set("uid", user.id);
			window.aztecs.storage.set("token", user.token);
			console.log("User ID: " + user.id + "\nUser Token: " + user.token);
			window.location.reload();
		});
	},

	/*
	* Handle signin errors
	*/
	onSigninError: function(err) {
		if(confirm("Error: Unable to signin: " + err + "\nPress \"Ok\" to enter testing mode."))
			window.location.replace("testing.html");
		else
			window.aztecs.location.refresh();
	},

	/*
	* Attempt to sign the user in.
	*/
	login: function() {
		window.plugins.googleplus.login({
			scopes: "profile email",
			webClientId: "974723804142-m67ipjompus4i4ts8i43n8qndjh8ts8d.apps.googleusercontent.com",
			offlilne: true
		}, window.aztecs.auth.onSignin, window.aztecs.auth.onSigninError);
	},

	/**
	* Sign the user out
	*/
	logout: function() {
		window.aztecs.storage.remove("uid");
		window.aztecs.storage.remove("token");
		window.plugins.googleplus.disconnect(function() {
			window.aztecs.location.refresh();
		});
	}
};

// ex: set ft=javascript ff=unix ts=4 sw=4 tw=0 noet :

