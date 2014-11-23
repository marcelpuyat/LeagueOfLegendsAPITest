import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONObject; 
	
/**
 * This is an example of how you would find champions used in recent games
 * with a kda > 5.
 * 
 * Note that there is a lot of JSON to maneuver to get the necessary data.
 * To see the JSON schema, check out: https://developer.riotgames.com/api/methods
 * 
 * Also note that Riot API servers tend to respond with 503s every now and then... so you'll just
 * have to run again until getting a 200.
 */
public class RiotGamesAPITest {
	 
	public static void main(String[] args) throws Exception {
		RiotGamesAPITest test = new RiotGamesAPITest();
		
		// Example: Print champions with games (out of last 10) with KDA > 5
		HashSet<String> championsWithKDA5 = new HashSet<String>();  
		
		int myId = test.getIDForUsername("StanfordBoy");
		JSONArray matchesArray = test.getMatchHistoryForId(myId).getJSONArray("matches");
		
		for (int matchNum = 0; matchNum < matchesArray.length(); matchNum++) {
			JSONObject match = matchesArray.getJSONObject(matchNum);
			
			// This gives all participant ids in the match. Must find our own participant id.
			JSONArray participantIds = match.getJSONArray("participantIdentities");
			
			// We will get our participantId by looking through participantIds array
			int myParticipantId = -1;
			for (int participantNum = 0; participantNum < participantIds.length(); participantNum++) {
				JSONObject participantIdentity = participantIds.getJSONObject(participantNum);
				int userIdForParticipant = participantIdentity.getJSONObject("player").getInt("summonerId");
				if (userIdForParticipant == myId) {
					myParticipantId = participantIdentity.getInt("participantId");
				}
			}
			
			assert(myParticipantId != -1);
			
			// Now we must get the participant with our id from the participants array
			JSONArray participants = match.getJSONArray("participants");
			
			for (int participantNum = 0; participantNum < participantIds.length(); participantNum++) {
				JSONObject participantObject = participants.getJSONObject(participantNum);
				
				if (participantObject.getInt("participantId") != myParticipantId) {
					// Skip over everyone else's participant object
					continue;
				}
				
				// Now we finally have our Participant object. Just have to look through stats
				JSONObject stats = participantObject.getJSONObject("stats");
				
				// These are casted to doubles for KDA calculation
				double kills = stats.getInt("kills");
				double assists = stats.getInt("assists");
				double deaths = stats.getInt("deaths");
				
				if ((kills + assists) / deaths > 5.0) {
					int championId = participantObject.getInt("championId");
					
					// We then convert the id into a champion name and add to our set to remove duplicates
					championsWithKDA5.add(test.getChampionNameForChampionId(championId));
				}
			}
			
			if (championsWithKDA5.isEmpty()) {
				System.out.println("No champions with kda > 5");
			}
			
			for (String championName : championsWithKDA5) {
				System.out.println(championName);
			}
		}
	}
	
	/**
	 * Returns user id given username
	 * Summoner search by name schema: https://developer.riotgames.com/api/methods#!/877/3059
	 * @param username
	 * @return
	 */
	private int getIDForUsername(String username) {
		try {
			JSONObject response = Connection.sendGet("v1.4", "/summoner/by-name/" + username, false);
			return response.getJSONObject(username.toLowerCase()).getInt("id");
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	/**
	 * Returns champion name given id
	 * Champion search by id schema: https://developer.riotgames.com/api/methods#!/860/3011
	 * @param championId
	 * @return
	 */
	private String getChampionNameForChampionId(int championId) {
		try {
			JSONObject response = Connection.sendGet("v1.2", "/champion/" + championId, true);
			return response.getString("name");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns match history as a JSONObject given user id.
	 * Match history schema: https://developer.riotgames.com/api/methods#!/873/3054
	 * @param id
	 * @return
	 */
	private JSONObject getMatchHistoryForId(int id) {
		try {
			return Connection.sendGet("v2.2", "/matchhistory/" + id, false);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	 
}
