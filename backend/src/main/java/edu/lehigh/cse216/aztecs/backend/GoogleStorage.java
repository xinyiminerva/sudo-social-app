package edu.lehigh.cse216.aztecs.backend;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

//import java.util.UUID; // https://kodejava.org/how-do-i-generate-uuid-guid-in-java/

//Make a new file new file
//Implement methods for uploading and downloading
//Make static methods upload and download with string a parameter

/*
Step1: init() checks to see if Drive has been initialised, if the drive is null make a new one
then if its not null continue. look for checkconnection, and read documentation

Step2: Upload file static method: Takes a string as a parameter, dont return anything, it may throw exceptions and you should handle those in your spark

*/

/*
When putting a file messages/attachments you assign the file a uuid when there is a success
*/

public class GoogleStorage {
    /********** Static Fields *********** */

    /** Global Drive API client. */
    public static Drive _drive = null;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the scopes required by this quickstart. If modifying these
     * scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE); // https://developers.google.com/drive/api/v3/about-auth
    // Full, permissive scope to access all of a user's files,
    // excluding the Application Data folder.
    // Request this scope only when it is strictly necessary

    /** Path for client_secrets.json file */
    private static final String CREDENTIALS_FILE_PATH = "/app/target/classes/client_secrets.json";

    /********** Static Methods *********** */

    /**
     * Creates an authorized Credential object.
     * 
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential authorize(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
            
            // Load client secrets.
            /*InputStream in = GoogleStorage.class.getResourceAsStream("/client_secrets.json");
            if (in == null) {
                throw new FileNotFoundException("Resource not found: " + "/client_secrets.json");
            }

            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
    
            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
            */
            return GoogleCredential.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH)).createScoped(SCOPES);
    }

    /**
     * Build and return an authorized Drive client service.
     *
     * @return an authorized Drive client service
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static Drive getDriveActivityService() throws GoogleStorageException {
        if(_drive == null)
            try {
                final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                _drive = new Drive.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, authorize(HTTP_TRANSPORT))
                    .setApplicationName("APPLICATION_NAME")
                    .build();
            } catch(Exception e) {
                throw new GoogleStorageException("Failed to connect to Google Drive for the backend storage system", e);
            }
        return _drive;
    }

    public static void upload(Attachment in) throws GoogleStorageException {
        Drive drive = getDriveActivityService(); //Initialise Drive
        File fileMetadata = new File();
        fileMetadata.setName("File");
        FileContent mediaContent = new FileContent(in.mime_type,in.file);
        try{
            File file = drive.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute();
            System.out.println("File ID: " + file.getId());
            in.file_id = file.getId(); //get ID of the file added   
        }catch(IOException e)
        {
            throw new GoogleStorageException("Unable to upload provided attachment to Google Drive", e);
        }
    }

    public static Attachment download(Attachment in) throws GoogleStorageException {
        // NOTE: the filename on Google should be "in.id.toString()"
        Drive drive = getDriveActivityService();
        try{
            InputStream istream = drive.files().get(in.file_id)
                .executeMediaAsInputStream();
            return new Attachment(in, istream);
        }catch(IOException e){
            throw new GoogleStorageException("Unable to download provided attachment in Google Drive", e);
        }
    }

    public static void delete(Attachment in) throws GoogleStorageException {
        Drive drive = getDriveActivityService();
        try{
            drive.files().delete(in.file_id).execute();
        }
        catch(IOException e){
            throw new GoogleStorageException("Unable to delete provided attachment in Google Drive", e);
        }
    }

    public static void update(Attachment in) throws GoogleStorageException {
        Drive drive = getDriveActivityService();
        try{
            File fileMetadata = new File();
            fileMetadata.setName("File");
            fileMetadata.setMimeType(in.mime_type);
            FileContent mediaContent = new FileContent(in.mime_type,in.file);

             drive.files().update(in.file_id, fileMetadata, mediaContent).execute();

        } catch(IOException e) {
            throw new GoogleStorageException("Unable to update provided attachment in Google Drive", e);
        }
    }
}
