"use strict";

// Define the object:
window.aztecs = window.aztecs || {};
window.aztecs.newMessage = {
	// Declare the name of the file:
	NAME: "NewEntryForm",

	/**
	* Define an initialization method to open the message dialogue
	*/
	init: function() {
		$("body").append(Handlebars.templates[window.aztecs.newMessage.NAME + ".hbs"]());
		$("#" + window.aztecs.newMessage.NAME + "-OK").click(window.aztecs.newMessage.submitForm);
		$("#" + window.aztecs.newMessage.NAME + "-Close").click(window.aztecs.newMessage.hide);
	},

	/**
	* Hide the contents of the form
	*/
	hide: function() {
		$("#" + window.aztecs.newMessage.NAME + "-message").val("");
		$("#" + window.aztecs.newMessage.NAME).modal("hide");
	},

	/**
	* Define a method to upload images
	*/
	uploadImage: function(file) {
		var reader = new FileReader();
		reader.readAsDataURL(file);
		reader.onload = function() {
			var image = "";
			if (reader.result != null)
				image = reader.result.toString();
			console.log(image);
			return image;
		};
		return "";
	},

	/**
	* Define a method to show the form
	*/
	show: function() {
		$("#" + window.aztecs.newMessage.NAME + "-message").val("");
		$("#" + window.aztecs.newMessage.NAME).modal("show");
	},

	/**
	* Submit the form data
	*/
	submitForm: function() {
		// Get the message:
		var msg = "" + $("#" + window.aztecs.newMessage.NAME + "-message").val();
		if (msg === "") {
			alert("Invalid message");
			return;
		}

		// Link attachments:
		var link = "" + $("#" + window.aztecs.newMessage.NAME + "-links").val();
		if (link !== "") {
			msg += "<br/><br/><a href=\"" + link + "\" new target=\"_blank\">Link</a>";
		}

		// Handle sub-threads:
		var thread = window.aztecs.location.getMid();
		
		// Post the message:
		window.aztecs.requests.handler("POST", "/messages", {
			message: msg,
			parent_id: thread || null
		}, function(data) {
			var upload = document.querySelector("#" + window.aztecs.newMessage.NAME + "-file").value;
			if (upload == null || upload.files == null)
				window.aztecs.location.refresh();
			console.log(upload);

			// File attachments:
			var formData = new FormData();
			formData.append("file", upload.files[0]);
			$("#" + window.aztecs.newMessage.NAME + "-file").replaceWith($("#" + window.aztecs.newMessage.NAME + "-file").clone());
			window.aztecs.newMessage.hide();

			var input = $("#" + window.aztecs.newMessage.NAME + "-file");
			input.replaceWith(input.val("").clone(true));
			$.ajax({
				url: "https://cse216aztecs.herokuapp.com/api/attachments/" + data.id,
				data: formData,
				processData: false,
				contentType: false,
				type: "POST",
				headers: {
					Authorization: window.aztecs.storage.get("token")
				},
				success: function(data) {
					window.aztecs.location.refresh();
				}
			});
			console.log(data.id);
		});
	}
};

// ex: set ft=javascript ff=unix ts=4 sw=4 tw=0 noet :

