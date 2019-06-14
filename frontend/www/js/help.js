"use strict";

// Define the object:
window.aztecs = window.aztecs || {};
window.aztecs.help = {
	// Define global variables:
	NAME: "Help",

	/**
	* Initialize the help dialogue
	*/
	init: function() {
		$("body").append(Handlebars.templates[window.aztecs.help.NAME + ".hbs"]());
		$("#" + window.aztecs.help.NAME + "-Ok").click(window.aztecs.help.submitForm);
		$("#" + window.aztecs.help.NAME + "-Close").click(window.aztecs.help.hide);
	},

	/**
	* Show th help form
	*/
	show: function() {
		$("#" + window.aztecs.help.NAME + "-message").val("");
		$("#" + window.aztecs.help.NAME).modal("show");
	},

	/**
	* Submit the form data
	*/
	submitForm() {
		// Get the message content:
		var msg = "" + $("#" + window.aztecs.help.NAME + "-message").val();
		if (msg === "") {
			alert("Please enter a message");
			return;
		}

		// Seng the request:
		window.aztecs.requests.handler("POST", "/slack", { text: msg }, function() { window.aztecs.location.refresh(); });
	},

	/**
	* Hide the form content
	*/
	hide: function() {
		$("#" + window.aztecs.help.NAME + "-message").val("");
		$("#" + window.aztecs.help.NAME).modal("hide");
	}
};

// ex: set ft=javascript ff=unix ts=4 sw=4 tw=0 noet :

