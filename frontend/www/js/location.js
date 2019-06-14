"use strict";

// Define the object:
window.aztecs = window.aztecs || {};
(function() {
	// Find the globals:
	var _mode = window.localStorage.getItem("location-mode") || "";
	var _mid = window.localStorage.getItem("location-mid") || "";
	var _uid = window.localStorage.getItem("location-uid") || "";
	
	window.aztecs.location = {
		// Define public state variables:
		mode: _mode,
		mid: _mid,
		uid: _uid,

		// Define getters:
		getMode: function() { return _mode; },
		getMid: function() { return _mid; },
		getUid: function() { return _uid; },

		// Define the refresh method:
		refresh: function() {
			window.localStorage.setItem("location-mode", window.aztecs.location.mode);
			window.localStorage.setItem("location-mid", window.aztecs.location.mid);
			window.localStorage.setItem("location-uid", window.aztecs.location.uid);
			window.location.reload();
		}
	};
})();

// ex: set ft=javascript ff=unix ts=4 sw=4 tw=0 noet :

