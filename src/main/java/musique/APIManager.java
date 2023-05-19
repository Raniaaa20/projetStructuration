package musique;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class APIManager {
    static final String API_KEY = "db41db8cc7990c75540f6c75871ef239";
    private static final String BASE_URL = "https://ws.audioscrobbler.com/2.0/";

   

    public APIManager() {
        
    }
    
    
    //---------------------------Informations sur un tag -----------------------------//
    
    //retourne la liste des tag à partir de l'api pour l'afficher dans le choiceBox
    public static List<String> extractTagNames(String jsonResponse) {
        List<String> tagNames = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject toptagsObject = jsonObject.getJSONObject("toptags");
            JSONArray tagArray = toptagsObject.getJSONArray("tag");

            for (int i = 0; i < tagArray.length(); i++) {
                JSONObject tagObject = tagArray.getJSONObject(i);
                String tagName = tagObject.getString("name");
                tagNames.add(tagName);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tagNames;
    }

    public static List<String> getTagList() {
        List<String> tagNames = new ArrayList<>();
        String url = APIManager.getBaseUrl() + "?method=tag.getTopTags&api_key=" + APIManager.API_KEY + "&format=json";
        StringBuilder jsonResponse = new StringBuilder();

        try {
            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
                reader.close();

                if (jsonResponse.length() > 0) {
                    tagNames = extractTagNames(jsonResponse.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tagNames;
    }



    public static String getTagInfo(String tag) {
        String url = BASE_URL + "?method=tag.getinfo&tag=" + tag + "&api_key=" + API_KEY + "&format=json";
        StringBuilder jsonResponse = new StringBuilder();

        try {
            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
                reader.close();

                if (jsonResponse.length() > 0) {
                    JSONObject jsonObject = new JSONObject(jsonResponse.toString());
                    JSONObject tagObject = jsonObject.getJSONObject("tag");

                    // Extraction des informations du tag
                    String tagName = tagObject.getString("name"); // Nom du tag
                    int tagCount = tagObject.getInt("total"); // Nombre de fois que le tag a été utilisé
                    int tagReach = tagObject.getInt("reach"); // Portée du tag
                    String tagSummary = tagObject.getJSONObject("wiki").getString("summary"); // Résumé du tag

                    // Construction de la chaîne d'informations sur le tag
                    StringBuilder tagInfo = new StringBuilder();
                    tagInfo.append("Nom du tag : ").append(tagName).append("\n");
                    tagInfo.append("Nombre de fois utilisé : ").append(tagCount).append("\n");
                    tagInfo.append("Portée du tag : ").append(tagReach).append("\n");
                    tagInfo.append("Résumé : ").append(tagSummary).append("\n");

                    System.out.println(tagInfo.toString());
                    return tagInfo.toString();
                } else {
                    System.out.println("Aucune information disponible pour le tag : " + tag);
                }
            } else {
                System.out.println("Une erreur s'est produite lors de la récupération des informations du tag.");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return ""; // Retourne une chaîne vide si aucune information n'est disponible
    }


    
    //---------------------Informations sur un album---------------------------------------//
    
    public static String getAlbumInfo(String artistName, String albumName) {
        try {
            String encodedArtistName = encodeParameter(artistName);
            String encodedAlbumName = encodeParameter(albumName);

            String url = BASE_URL + "?method=album.getinfo&artist=" + encodedArtistName + "&album=" + encodedAlbumName + "&api_key=" + API_KEY + "&format=json";

            StringBuilder jsonResponse = new StringBuilder();

            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonResponse.append(line);
                }
                reader.close();
            }

            return extractAlbumInfo(jsonResponse.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ""; // Retourne une chaîne vide si une erreur s'est produite ou si aucune information n'est disponible
    }

    
    private static String extractAlbumInfo(String jsonResponse) {
        StringBuilder albumInfo = new StringBuilder();

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject albumObject = jsonObject.getJSONObject("album");

            String artist = albumObject.getString("artist");
            String albumName = albumObject.getString("name");

            String releaseDate = "N/A";
            if (albumObject.has("releasedate")) {
                releaseDate = albumObject.getString("releasedate");
            }

            JSONArray tracksArray = albumObject.getJSONObject("tracks").getJSONArray("track");
            int trackCount = tracksArray.length();

            int totalDuration = 0;

            albumInfo.append("Artiste : ").append(artist).append("\n");
            albumInfo.append("Album : ").append(albumName).append("\n");
            albumInfo.append("Nombre de pistes : ").append(trackCount).append("\n");
            
            if (!releaseDate.equals("N/A")) {
                albumInfo.append("Date de publication : ").append(releaseDate).append("\n");
            } else {
                albumInfo.append("Date de publication : N/A").append("\n");
            }
            
            albumInfo.append("Pistes :").append("\n");

            for (int i = 0; i < trackCount; i++) {
                JSONObject trackObject = tracksArray.getJSONObject(i);
                String trackName = trackObject.getString("name");
                int duration = trackObject.getInt("duration");

                totalDuration += duration;

                albumInfo.append("- ").append(trackName).append(" (").append(duration).append(" secondes)").append("\n");
            }

            albumInfo.append("Durée totale : ").append(totalDuration).append(" secondes");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return albumInfo.toString();
    }

    private static String encodeParameter(String parameter) {
        try {
            return URLEncoder.encode(parameter, StandardCharsets.UTF_8.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    //---------------------------------------------------------------------------------//
    //----------------------------------ARTIST-----------------------------------------//
    
    public static String getArtistInfo(String artistName) {
        try {
            String encodedArtistName = URLEncoder.encode(artistName, "UTF-8");
            String artistInfoUrl = BASE_URL + "?method=artist.getinfo&artist=" + encodedArtistName + "&api_key=" + API_KEY + "&format=json";
            String topAlbumsUrl = BASE_URL + "?method=artist.gettopalbums&artist=" + encodedArtistName + "&api_key=" + API_KEY + "&format=json";

            StringBuilder artistInfoAndTopAlbums = new StringBuilder();

            // Get artist info
            JSONObject artistInfoObject = sendRequest(artistInfoUrl);
            if (artistInfoObject != null) {
                // Get artist biography
                String artistBiography = artistInfoObject.getJSONObject("artist").getJSONObject("bio").getString("summary");

             // Get number of listeners
                JSONObject statsObject = artistInfoObject.getJSONObject("artist").getJSONObject("stats");
                String listenersString = statsObject.getString("listeners");
                int listeners = Integer.parseInt(listenersString);
                


             // Get number of plays
                int playCount = statsObject.getInt("playcount");
                

                artistInfoAndTopAlbums.append("---Nom de l'artiste : ").append(artistName).append("\n\n");
                artistInfoAndTopAlbums.append("---Biographie : ").append(artistBiography).append("\n\n");
                artistInfoAndTopAlbums.append("---Nombre d'auditeurs : ").append(listeners).append("\n\n");
                artistInfoAndTopAlbums.append("---Nombre de lectures : ").append(playCount).append("\n\n");
            }

            // Get top albums info
            JSONObject topAlbumsObject = sendRequest(topAlbumsUrl);
            if (topAlbumsObject != null) {
                String topAlbumsInfo = extractTopAlbumsInfo(topAlbumsObject.toString());
                artistInfoAndTopAlbums.append(topAlbumsInfo);
            }

            return artistInfoAndTopAlbums.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ""; // En cas d'erreur ou d'exception, retourner une chaîne vide
    }

    private static JSONObject sendRequest(String url) throws IOException, JSONException {
        StringBuilder jsonResponse = new StringBuilder();

        URL urlObject = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                jsonResponse.append(line);
            }
            reader.close();

            return new JSONObject(jsonResponse.toString());
        }

        return null;
    }

    
    private static String extractTopAlbumsInfo(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject topAlbumsObject = jsonObject.getJSONObject("topalbums");

            StringBuilder topAlbumsInfo = new StringBuilder();
            if (topAlbumsObject.has("album")) {
                JSONArray albumArray = topAlbumsObject.getJSONArray("album");
                topAlbumsInfo.append("Top des albums :\n");
                for (int i = 0; i < albumArray.length(); i++) {
                    JSONObject album = albumArray.getJSONObject(i);
                    String albumName = album.getString("name");
                    topAlbumsInfo.append("- ").append(albumName).append("\n");
                }
            } else {
                topAlbumsInfo.append("Aucun album trouvé pour cet artiste.");
            }

            return topAlbumsInfo.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

   
    
    //-------------------------TRENDS--------------------------------------------//
    
    public static String getGlobalTrendsInfo() {
        StringBuilder trendsInfo = new StringBuilder();

        trendsInfo.append("Top Artists:\n");
        JSONArray topArtists = getTopArtists();
        trendsInfo.append(getJSONArrayValues(topArtists, "name", "listeners", "playcount"));
        trendsInfo.append("\n");

        trendsInfo.append("Top Tracks:\n");
        JSONArray topTracks = getTopTracks();
        trendsInfo.append(getJSONArrayValues(topTracks, "name", "listeners", "playcount"));
        trendsInfo.append("\n");

        trendsInfo.append("Top Tags:\n");
        JSONArray topTags = getTopTags();
        trendsInfo.append(getJSONArrayValues(topTags, "name", "reach", "taggings"));
        trendsInfo.append("\n");

        return trendsInfo.toString();
    }

    public static JSONArray getTopArtists() {
        String url = BASE_URL + "?method=chart.gettopartists&api_key=" + API_KEY + "&format=json";
        return getJSONArrayFromUrl(url, "artists", "artist");
    }

    public static JSONArray getTopTags() {
        String url = BASE_URL + "?method=chart.gettoptags&api_key=" + API_KEY + "&format=json";
        return getJSONArrayFromUrl(url, "tags", "tag");
    }

    public static JSONArray getTopTracks() {
        String url = BASE_URL + "?method=chart.gettoptracks&api_key=" + API_KEY + "&format=json";
        return getJSONArrayFromUrl(url, "tracks", "track");
    }

    private static String getJSONArrayValues(JSONArray jsonArray, String nameKey, String value1Key, String value2Key) {
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String name = object.getString(nameKey);
            int value1 = object.getInt(value1Key);
            int value2 = object.getInt(value2Key);

            values.append("Name: ").append(name).append(", ").append(value1Key).append(" : ").append(value1).append(", ").append(value2Key).append(" : ").append(value2).append("\n");
        }
       
        return values.toString();
    }


    
    
    //----------------------------GLOBAL TRENDS BY COUNTRY-------------------//
   
    public static String getGlobalTrendsInfoCountry(String country) {
        StringBuilder trendsInfo = new StringBuilder();

        // Obtention des tendances des top artistes par pays
        JSONArray topArtistsByCountry = getTopArtistsByCountry(country);
        trendsInfo.append("Top Artists in ").append(country).append(":\n");
        trendsInfo.append(getJSONArrayValues(topArtistsByCountry, "name", "listeners"));
        trendsInfo.append("\n");

        // Obtention des tendances des top tracks par pays
        JSONArray topTracksByCountry = getTopTracksByCountry(country);
        trendsInfo.append("Top Tracks in ").append(country).append(":\n");
        trendsInfo.append(getJSONArrayValues(topTracksByCountry, "name", "listeners"));
        trendsInfo.append("\n");

        return trendsInfo.toString();
    }

   private static Object getJSONArrayValues(JSONArray jsonArray, String nameKey, String value1Key) {
	   StringBuilder values = new StringBuilder();
       for (int i = 0; i < jsonArray.length(); i++) {
           JSONObject object = jsonArray.getJSONObject(i);
           String name = object.getString(nameKey);
           int value1 = object.getInt(value1Key);
          

           values.append("Nom : ").append(name).append(", ").append("Nombre d'auditeurs : ").append(" : ").append(value1).append(", ").append("\n");
       }
      
       return values.toString();
		
	}



    public static JSONArray getTopArtistsByCountry(String country) {
        String url = BASE_URL + "?method=geo.gettopartists&country=" + country + "&api_key=" + API_KEY + "&format=json";
        return getJSONArrayFromUrl(url, "topartists", "artist");
    }

    public static JSONArray getTopTracksByCountry(String country) {
        String url = BASE_URL + "?method=geo.gettoptracks&country=" + country + "&api_key=" + API_KEY + "&format=json";
        return getJSONArrayFromUrl(url, "tracks", "track");
    }

    
  

    //------------------------TRENDS PAR COUNTRY---------------------------------//
     
   
     
     private static String sendGetRequest(String url) throws IOException {
         URL urlObject = new URL(url);
         HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
         connection.setRequestMethod("GET");

         int responseCode = connection.getResponseCode();
         if (responseCode == HttpURLConnection.HTTP_OK) {
             BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             StringBuilder response = new StringBuilder();
             String line;
             
             while ((line = reader.readLine()) != null) {
                 response.append(line);
             }
             reader.close();
             return response.toString();
         } else {
             throw new IOException("Error response code: " + responseCode);
         }
     }
     
   
     
     //-----------------------------ALBUMS & CHANSONS SIMILAIRES ENTRE ARTISTES------------//
     
     
     public static String searchSimilarAlbumsAndSongs(String artist1, String artist2) {
    	    StringBuilder similarInfo = new StringBuilder();

    	    try {
    	        String url = "http://ws.audioscrobbler.com/2.0/?method=artist.getsimilar&artist=" + artist1 +
    	                "&api_key=" + API_KEY + "&format=json";

    	        String response = sendGetRequest(url);
    	        JSONObject json = new JSONObject(response);

    	        JSONArray similarArtistsArray = json.getJSONObject("similarartists").getJSONArray("artist");
    	        List<String> similarArtists = new ArrayList<>();

    	        // Obtenez une liste d'artistes similaires
    	        for (int i = 0; i < similarArtistsArray.length(); i++) {
    	            String similarArtist = similarArtistsArray.getJSONObject(i).getString("name");
    	            similarArtists.add(similarArtist);
    	        }

    	        // Recherchez des albums similaires pour chaque artiste similaire
    	        int albumCount = 0;
    	        for (String similarArtist : similarArtists) {
    	            if (albumCount >= 10) {
    	                break;
    	            }
    	            
    	            url = "http://ws.audioscrobbler.com/2.0/?method=artist.gettopalbums&artist=" + similarArtist +
    	                    "&api_key=" + API_KEY + "&format=json";
    	            response = sendGetRequest(url);
    	            json = new JSONObject(response);

    	            JSONArray topAlbumsArray = json.getJSONObject("topalbums").getJSONArray("album");

    	            // Obtenez les noms des albums similaires
    	            for (int i = 0; i < topAlbumsArray.length(); i++) {
    	                if (albumCount >= 10) {
    	                    break;
    	                }

    	                String albumName = topAlbumsArray.getJSONObject(i).getString("name");
    	                similarInfo.append("Similar Album: ").append(albumName).append("\n");
    	                albumCount++;
    	            }
    	        }

    	        similarInfo.append("\n"); // Retour de ligne entre la partie "Tracks" et "Albums"

    	        // Recherchez des chansons similaires pour chaque artiste similaire
    	        int trackCount = 0;
    	        for (String similarArtist : similarArtists) {
    	            if (trackCount >= 10) {
    	                break;
    	            }
    	            
    	            url = "http://ws.audioscrobbler.com/2.0/?method=artist.gettoptracks&artist=" + similarArtist +
    	                    "&api_key=" + API_KEY + "&format=json";
    	            response = sendGetRequest(url);
    	            json = new JSONObject(response);

    	            JSONArray topTracksArray = json.getJSONObject("toptracks").getJSONArray("track");

    	            // Obtenez les noms des chansons similaires
    	            for (int i = 0; i < topTracksArray.length(); i++) {
    	                if (trackCount >= 10) {
    	                    break;
    	                }

    	                String trackName = topTracksArray.getJSONObject(i).getString("name");
    	                similarInfo.append("Similar Track: ").append(trackName).append("\n");
    	                trackCount++;
    	            }
    	        }

    	    } catch (IOException | JSONException e) {
    	        e.printStackTrace();
    	    }

    	    return similarInfo.toString();
    	}




    //--------------------------------------------------------------------------//
     
     

     private static JSONArray getJSONArrayFromUrl(String urlString, String parentObject, String childArray) {
         try {
             URL url = new URL(urlString);
             HttpURLConnection connection = (HttpURLConnection) url.openConnection();
             connection.setRequestMethod("GET");

             BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             StringBuilder response = new StringBuilder();
             String line;
             while ((line = reader.readLine()) != null) {
                 response.append(line);
             }
             reader.close();

             JSONObject jsonObject = new JSONObject(response.toString());
             return jsonObject.getJSONObject(parentObject).getJSONArray(childArray);
         } catch (Exception e) {
             e.printStackTrace();
         }
         
         
         return new JSONArray();
     }
 
  
     //--------------------------------------------------------------------------//


    //getters 
	public static String getApiKey() {
		return API_KEY;
	}

	public static String getBaseUrl() {
		return BASE_URL;
	}
	
	

}