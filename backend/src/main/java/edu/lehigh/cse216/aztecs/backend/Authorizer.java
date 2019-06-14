package edu.lehigh.cse216.aztecs.backend;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;
import java.util.Random;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.auth.AuthInfo;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;

import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.List;

/**
 * Define a class to provide generic authorization functionality to the API.
 */
public abstract class Authorizer {

	private static final NetHttpTransport transport = new NetHttpTransport();
	private static final JacksonFactory jsonFactory = new JacksonFactory();

	//public static Hashtable<Integer, Integer> logged_in = new Hashtable<Integer, Integer>();
	
	public static MemcachedClient cache;
	
	public static void init() throws IOException
	{
		List<InetSocketAddress> servers =
      AddrUtil.getAddresses(System.getenv("MEMCACHIER_SERVERS").replace(",", " "));
      AuthInfo authInfo =
      AuthInfo.plain(System.getenv("MEMCACHIER_USERNAME"),
                     System.getenv("MEMCACHIER_PASSWORD"));

    MemcachedClientBuilder builder = new XMemcachedClientBuilder(servers);

    // Configure SASL auth for each server
    for(InetSocketAddress server : servers) {
      builder.addAuthInfo(server, authInfo);
    }

    // Use binary protocol
    builder.setCommandFactory(new BinaryCommandFactory());
    // Connection timeout in milliseconds (default: )
    builder.setConnectTimeout(1000);
    // Reconnect to servers (default: true)
    builder.setEnableHealSession(true);
    // Delay until reconnect attempt in milliseconds (default: 2000)
    builder.setHealSessionInterval(2000);

    
      cache = builder.build();
      //cache.set("foo", 0, "bar");
      //String val = cache.get("foo");
      //System.out.println(val);
    
	}

	/**
	 * Generates and appends an authorization token to the given user.
	 * @param idToke The user to authorize
	 * @return The new Authorized User object
	 */
	public static User authorize(String idToke, Database db) throws APIException {
		String email;
		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
			// Specify the CLIENT_ID of the app that accesses the backend:
			.setAudience(Collections.singletonList("974723804142-m67ipjompus4i4ts8i43n8qndjh8ts8d.apps.googleusercontent.com"))
			// Or, if multiple clients access the backend:
			//.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
			.build();

		GoogleIdToken idToken;
		try {
			idToken = verifier.verify(idToke);
		} catch(Exception e) {
			throw new GoogleOAuthException("Server was denied access to Google's OAuth Services");
		}

		if(idToken == null)
			throw new AuthorizationFailedException("Oauth verification failed");

		Payload payload = idToken.getPayload();
		//Get profile information from payload
		 email =(String) payload.getEmail();
		if(email != null){
			String domain = email.substring(email.length()-10, email.length());
			//check that only domain end with lehigh.edu can have the authorzation to login
			if(domain.equals("lehigh.edu")){
				Integer key = Authorizer.keyGenerator(); // create key
				User user = db.users.readOne(email);
				try {
					Authorizer.cache.set(user.id.toString(), 0, key);
				} catch(Exception e) {
					throw new GoogleOAuthException("Failed to commit user token to MemCacheD");
				}
				// Format the tokens "<email>:<token>":
				user.token = user.id + ":" + key.toString();
				//stire unique user identification (assume email);
				return user;
			}
			throw new AccessForbiddenException("Not valid domain");
		}
		throw new AccessForbiddenException("Unable to access email");
	}

	/** This method randomly creates key to return to the android and web */
	public static int keyGenerator (){
		Random rand = new Random();
		int random = rand.nextInt(10000) + 1000;
		return random;
	};
	
	/**
	 * Obtains the ID number of the user referenced by the token.
	 * @param token The JWT authorization token provided in the request.
	 * @return The ID number of the user.
	 */
	public static Integer getUIDFromToken(String token) {
		try {
			String uID = token.split(":")[0];
			Integer uToken = Integer.parseInt(token.split(":")[1]);
			if(cache.get(uID).equals(uToken))
				return Integer.parseInt(uID);
		} catch (Exception e){}
		return null;
	}
	
	/**
	 * Validates that an authorization token represents the provided user.
	 * @param token The token to analyze.
	 * @param user The ID number of the user to validate.
	 * @return True if the token does indeed represent the user, false otherwise.
	 */
	public static boolean validateUser(String token, int user) {
		Integer tokenId = getUIDFromToken(token);
		if(tokenId != null && tokenId == user)
			return true;
		return false;
	}
}
