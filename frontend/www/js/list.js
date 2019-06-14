"use strict";

// Define the object:
window.aztecs = window.aztecs || {};
window.aztecs.list = {
	// Define global variables:
	thread: "",
	NAME: "ElementList",

	/**
	* Initialize the list
	*/
	init: function() {
		// Handle sub threads:
		var mid = window.aztecs.location.getMid();
		if (mid !== "")
			window.aztecs.list.thread = "?thread=" + mid;

		// Update the list:
		window.aztecs.requests.handler("GET", "/messages" + window.aztecs.list.thread, null, window.aztecs.list.update);
	},

	/**
	* Update the content of the list
	*/
	update: function(data) {
		// Handle the parent:
		var parent;
		if (window.aztecs.list.thread !== "") {
			parent = data[0];
			data = data.slice(1);
		}

		// Add the data:
		$("body").append(Handlebars.templates[window.aztecs.list.NAME + ".hbs"]({
			mData: data,
			uid: window.aztecs.storage.get("uid"),
			parent: parent
		}));

		// Find all of the delete buttons, and set their behavior
		$("." + window.aztecs.list.NAME + "-delbtn").click(window.aztecs.list.clickDelete);
		// Find all of the Edit buttons, and set their behavior
		//$("." + window.aztecs.list.NAME + "-editbtn").click(ElementList.clickEdit);
		// Find all of the Like buttons, and set their behavior
		$("." + window.aztecs.list.NAME + "-likebtn").click(window.aztecs.list.clickLike);
		// Find all of the disLike buttons, and set their behavior
		$("." + window.aztecs.list.NAME + "-dislikebtn").click(window.aztecs.list.clickDislike);
		// Find the parent button, and set its behavior
		$("." + window.aztecs.list.NAME + "-parentbtn").click(window.aztecs.list.clickParent);
		// Set the behavior of clicking on comments
		$("." + window.aztecs.list.NAME + "-comment").click(window.aztecs.list.clickComment);
		// Set the behavior of all username entries:
		$("." + window.aztecs.list.NAME + "-user").click(window.aztecs.list.clickUser);
		// Set the behavior of all flag buttons:
		$("." + window.aztecs.list.NAME + "-flagbtn").click(window.aztecs.list.clickFlag);
	},

	/**
	* Open the parent of the selected message
	*/
	clickParent: function() {
		var parentId = $(this).data("id");
		window.aztecs.location.mid = parentId || "";
		window.aztecs.location.refresh();
	},

	/**
	* Open the child of the selected message
	*/
	clickComment: function() {
		var id = $(this).data("id");
		window.aztecs.location.mid = id;
		window.aztecs.location.refresh();
	},

	/**
	* Switch the window mode to "u"
	*/
	clickUser: function() {
		var id = $(this).data("id");
		window.aztecs.profile.open(id);
	},

	/**
	* Handle the like functionality
	*/
	clickLike: function() {
		var id = $(this).data("id");
		var liked = $(this).data("liked");
		if (liked === true) {
			// Delete like:
			window.aztecs.requests.handler("DELETE", "/likes/" + id + "/" + window.aztecs.storage.get("uid"), null, function () { window.aztecs.location.refresh(); });
			return;
		}
		if (liked === false) {
			// Change like:
			window.aztecs.requests.handler("PUT", "/likes/" + id + "/" + window.aztecs.storage.get("uid"), { liked: true }, function () { window.aztecs.location.refresh(); });
			return;
		}
		window.aztecs.requests.handler("POST", "/likes", { message_id: id, liked: true }, function () { window.aztecs.location.refresh(); });
	},

	/**
	* Handle the dislike functionality
	*/
	clickDislike: function() {
		var id = $(this).data("id");
		var liked = $(this).data("liked");
		if (liked === false) {
			// Delete like:
			window.aztecs.requests.handler("DELETE", "/likes/" + id + "/" + window.aztecs.storage.get("uid"), null, function () { window.aztecs.location.refresh(); });
			return;
		}
		if (liked === true) {
			// Change like:
			window.aztecs.requests.handler("PUT", "/likes/" + id + "/" + window.aztecs.storage.get("uid"), { liked: false }, function () { window.aztecs.location.refresh(); });
			return;
		}
		window.aztecs.requests.handler("POST", "/likes", { message_id: id, liked: false }, function () { window.aztecs.location.refresh(); });
	},

	/**
	* Handle message deletion
	*/
	clickDelete: function() {
		var id = $(this).data("id");
		window.aztecs.requests.handler("DELETE", "/messages/" + id, null, function() { window.aztecs.location.refresh(); });
	},

	/**
	* Handle the behavior of the flag button
	*/
	clickFlag: function() {
		var id = $(this).data("id");
		window.aztecs.requests.handler("PUT", "/slack", { msg: "" + id }, function() {
			alert("Thank you for your feedback! The site's maintainers have been notified.");
			window.aztecs.location.refresh();
		});
	}
};

// ex: set ft=javascript ff=unix ts=4 sw=4 tw=0 noet :

