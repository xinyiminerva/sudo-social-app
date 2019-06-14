# Makefile for the web branch

# Define some global variables:
DEPLOYMENT_DIR=../backend/src/main/resources/web
HB_FILES=hb/*.hbs
LOCAL_DEPS=www/lib/templates.js www/lib/jquery.min.js www/lib/bootstrap.min.js www/lib/bootstrap.min.css www/lib/handlebars.min.js www/lib/fonts www/lib/jasmine.css www/lib/jasmine.js www/lib/boot.js www/lib/jasmine-html.js


# Define all targets:
.PHONY: android android-run init web web-run deploy

# By default, do everything:
all: init android-build android-run local deploy

# Create a target for compiling the Handlebars templates:
www/lib/templates.js: $(HB_FILES)
	node_modules/.bin/handlebars $(HB_FILES) > www/lib/templates.js

# Create targets to copy over dependencies:
www/lib/jquery.min.js: node_modules/jquery/dist/jquery.min.js
	cp -v node_modules/jquery/dist/jquery.min.js www/lib/

www/lib/bootstrap.min.js: node_modules/bootstrap/dist/js/bootstrap.min.js
	cp -v node_modules/bootstrap/dist/js/bootstrap.min.js www/lib/

www/lib/bootstrap.min.css: node_modules/bootstrap/dist/css/bootstrap.min.css
	cp -v node_modules/bootstrap/dist/css/bootstrap.min.css www/lib/

www/lib/handlebars.min.js: node_modules/handlebars/dist/handlebars.min.js
	cp -v node_modules/handlebars/dist/handlebars.min.js www/lib/

www/lib/fonts: node_modules/bootstrap/dist/fonts
	cp -rv node_modules/bootstrap/dist/fonts www/lib/

www/lib/jasmine.css: node_modules/jasmine-core/lib/jasmine-core/jasmine.css
	cp node_modules/jasmine-core/lib/jasmine-core/jasmine.css www/lib/

www/lib/jasmine.js: node_modules/jasmine-core/lib/jasmine-core/jasmine.js
	cp -v node_modules/jasmine-core/lib/jasmine-core/jasmine.js www/lib/

www/lib/boot.js: node_modules/jasmine-core/lib/jasmine-core/boot.js
	cp -v node_modules/jasmine-core/lib/jasmine-core/boot.js www/lib/

www/lib/jasmine-html.js: node_modules/jasmine-core/lib/jasmine-core/jasmine-html.js
	cp -v node_modules/jasmine-core/lib/jasmine-core/jasmine-html.js www/lib/

# Define a target to initialize the build environment:
init:
	npm i
	npx cordova prepare

# Define a target for building the android client:
android: $(LOCAL_DEPS)
	npx cordova build android --release

# Define a target for running the android client:
android-run: $(LOCAL_DEPS)
	npx cordova run android --release

# Define a target for building the web client:
web: $(LOCAL_DEPS)
	npx cordova build browser --release

# Define a target for running the web client:
web-run: $(LOCAL_DEPS)
	npx cordova run browser --release

# Define a target for local testing:
test: local $(TEST_DEPS)

# Deploy the web data to the backend server's folder:
deploy: web
	rm -rf $(DEPLOYMENT_DIR)/*
	(GLOBIGNORE='.gitignore'; cp -rv platforms/browser/www/* $(DEPLOYMENT_DIR)/)

#  ex: set ft=make ff=unix ts=4 sw=4 tw=0 noet :

