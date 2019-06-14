"use strict";

// Define the object:
window.aztecs = window.aztecs || {};
window.aztecs.requests = {
	// Set the API URL:
	apiUrl: "https://cse216aztecs.herokuapp.com/api",

	// Set the global request handler method:
	handler: function (method, endpoint, body, cb) {
		// Report debug info:
		console.log(method + ": " + window.aztecs.requests.apiUrl + endpoint);
		var call = {
			type: method,
			url: window.aztecs.requests.apiUrl + endpoint,
			headers: {
				Authorization: window.aztecs.storage.get("token")
			},
			statusCode: window.aztecs.requests.statusHandler,
			error: window.aztecs.requests.errorHandler,
			success: cb
		};

		// Add the body:
		if (body !== null) {
			call.dataType = "json";
			call.data = JSON.stringify(body);
		}

		// Make the call:
		$.ajax(call);
	},

	// Define the error handler:
	statusHandler: {
		401: function () {
			// Something went wrong with the token:
			console.log(window.aztecs.storage.get("token"));
			alert("Error 401: Login Invalid");
			window.aztecs.auth.logout();
			window.location.replace(window.location.origin);
		},
		403: function () {
			// You tried to access something that you weren't authorized to access:
			alert("Error 403: Access Denied");
			window.location.replace(window.location.origin);
		},
		404: function () {
			// You tried to access something that doesn't exist:
			alert("Error 404: Resource Not Found");
			window.location.replace(window.location.origin);
		},
		500: function () {
			// This should never happen, but better safe than sorry:
			alert("Error 500: Something went wrong.");
			window.aztecs.auth.logout();
			window.location.replace(window.location.origin);
		},
		501: function () {
			// This is mostly a sanity check, to catch incorrect HTTP methods during development:
			alert("Error 501: HTTP Method Not Implemented.");
			window.aztecs.auth.logout();
		}
	},

	// Define the error handler:
	errorHandler: function(err) {
		alert("Error sending web request: " + JSON.stringify(err));
		window.aztecs.auth.logout();
	}
};

// ex: set ft=javascript ff=unix ts=4 sw=4 tw=0 noet :

