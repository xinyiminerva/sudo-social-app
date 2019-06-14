"use strict";

// Define the object:
window.aztecs = window.aztecs || {};
window.aztecs.storage = {
	// Define a function to read a value from storage:
	get: function(key) {
		var tmp = document.cookie.split("; "), tmp2;
		for (var i = 0; i < tmp.length; ++i) {
			tmp2 = tmp[i].split("=");
			if (tmp2[0] === key)
				if (tmp2[1] === "")
					return undefined;
				else
					return tmp2[1];
		}
		return undefined;
	},
	
	// Define a function to remove a value from storage:
	remove: function(key) {
		document.cookie = key + "=";
	},

	// Define a function to apply a value into storage:
	set: function(key, value) {
		document.cookie = key + "=" + value;
	}
}

// ex: set ft=javascript ff=unix ts=4 sw=4 tw=0 noet :

