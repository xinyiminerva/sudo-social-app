// Run all tests on the Navbar:
describe("nav", function() {
	// Manage the form:

	beforeAll(function() {
		spyOn(window.aztecs.storage, "get").and.callFake(function(key) {
			if(key === "uid")
				return "42";
			else
				return "bogus";
		});

		// Initialize the navbar:
		window.aztecs.nav.init();
		$("nav").hide();
	});

	// Test the UserProfile Page
	it("Should display the user profile link", function() {
		console.log(document.getElementById("Navbar-profile"));
		expect("Profile (#42)" === $("#Navbar-profile").innerText);
	});

	// Test the newMessage Page
	it("Should display the Add Message link", function() {
		expect("Add Message" === $("#Navbar-profile").innerText);
	});

	// Display the logout button
	it("Should display the logout button", function() {
		expect("Logout" === $("Navbar-logout").innerText);
	});

	// Display the help button
	it("Should display the help button", function() {
		expect("Help" === $("Navbar-help").innerText);
	});
});

// ex: set ft=javascript ff=unix ts=4 sw=4 tw=0 noet :

