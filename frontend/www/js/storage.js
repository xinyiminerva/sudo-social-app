"use strict";

// Define the object:
window.aztecs = window.aztecs || {};
window.aztecs.storage = {
	// Define a function to read a value from storage:
	get: function(key) {
		 return window.localStorage.getItem("storage-" + key) || undefined;
	},
	
	// Define a function to remove a value from storage:
	remove: function(key) {
		window.localStorage.removeItem("storage-" + key);
	},

	// Define a function to apply a value into storage:
	set: function(key, value) {
		window.localStorage.setItem("storage-" + key, value);
	}
}

// ex: set ft=javascript ff=unix ts=4 sw=4 tw=0 noet :

