"use strict";

/**
* Function to be called by the event listener once the user's login has been verified.
*/
function onAuth() {
	// Add a Handlebars Helper:
	Handlebars.registerHelper('if_eq', function (a, b, opts) {
        if (a == b) {
            console.log("\"if_eq\" determined \"" + a + "\" and \"" + b + "\" are equal");
            return opts.fn(this);
        }
        else {
            console.log("\"if_eq\" determined \"" + a + "\" and \"" + b + "\" are not equal");
            return opts.inverse(this);
        }
    });

	// Hide the default screen:
	$("#default").hide();
	
	// Initialize the window:
	window.aztecs.nav.init();
	window.aztecs.newMessage.init();
	window.aztecs.list.init();
	window.aztecs.profile.init();
	window.aztecs.help.init();

	// TODO: Add edit entry form here.
}

// Add an event listener to login when the device has entered a "ready" state:
document.addEventListener('deviceready', function() {
	if (typeof window.aztecs.storage.get("token") === "undefined") {
		$("#default").show();
		window.aztecs.auth.login();
	} else
		onAuth();
}, false);


// ex: set ft=javascript ff=unix ts=4 sw=4 tw=0 noet :

