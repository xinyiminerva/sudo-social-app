"use strict";

// Define the object:
window.aztecs = window.aztecs || {};
(function() {
	// Read the query:
	var query = window.location.search.slice(1).split("&");
	var i;

	// Detect the mode:
	var _mode = "";
	for (i = 0; i < query.length; ++i)
		if (query[i].split("=")[0] === "mode") {
			_mode = query[i].split("=")[1];
			break;
		}

	// Detect the mid:
	var _mid = "";
	for (i = 0; i < query.length; ++i)
		if (query[i].split("=")[0] === "mid") {
			_mid = query[i].split("=")[1];
			break;
		}

	// Detect the uid:
	var _uid = "";
	for (i = 0; i < query.length; ++i)
		if (query[i].split("=")[0] === "uid") {
			_uid = query[i].split("=")[1];
			break;
		}


	window.aztecs.location = {
		// Define globals:
		mode: _mode,
		mid: _mid,
		uid: _uid,

		// Define getters:
		getMode: function() { return _mode; },
		getMid: function() { return _mid; },
		getUid: function() { return _uid; },

		// Define the refresh method:
		refresh: function() {
			// Build the search string:
			var search = "";
			if (window.aztecs.location.mode !== "")
				search += "?mode=" + window.aztecs.location.mode;
			if (window.aztecs.location.mid !== "")
				search += "?mid=" + window.aztecs.location.mid;
			if (window.aztecs.location.uid !== "")
				search += "?uid=" + window.aztecs.location.uid;

			// Reload the window:
			window.location.replace(window.location.origin + search);
		}
	};
})();

// ex: set ft=javascript ff=unix ts=4 sw=4 tw=0 noet :

