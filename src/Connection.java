import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

/**
 * A lot of this code came from: http://www.mkyong.com/java/how-to-send-http-request-getpost-in-java/
 * 
 * I simply put into place a few parameters and constants needed to fill in the proper URLs for
 * Riot's API, which can be found here: https://developer.riotgames.com/api/methods
 * @author marcelp
 *
 */
public class Connection {
	private final static String USER_AGENT = "Mozilla/5.0";
	
	// TODO: Fill in your api key here
	private final static String API_KEY = "?api_key=" + "<your-API-key>";
	
	public static JSONObject sendGet(String version, String urlAfterRegion, boolean isStaticData) throws Exception {
		
		String url = "https://na.api.pvp.net/api/lol/" + (isStaticData? "static-data/" : "") + "na/"
					+ version + urlAfterRegion + API_KEY;
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
 
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		return new JSONObject(response.toString());
	}
}
