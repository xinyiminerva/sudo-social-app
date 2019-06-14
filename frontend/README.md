# Frontend

## Building android from source:
 1. Download dependencies: `npm i`
 2. Prepare cordova: `npx cordova prepare`
 3. Create your signing key: `keytool -genkey -v -keystore app.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias aztecs` (Enter all requested information, but press *[ENTER]* to avoid entering the second password).
 4. Populate the file 'platform/android/release-signing.properties' like so:
   ```
   storeFile=../../app.keystore
   storeType=jks
   keyAlias =aztecs
   keyPassword=<your password>
   storePassword=<your password>

   ```

 5. Go into Google Developer Console & add the exported keydata to the Oauth validation list.
 6. Edit "package.json", "index.html", & "js/auth.js" in order to correct the Oauth codes listed there.
 7. Run `npx cordova run --release` to build & run the application on an emulator or connected device.

