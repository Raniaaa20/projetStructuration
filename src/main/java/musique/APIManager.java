package musique;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
    
    private static List<String> extractTagNames(String jsonResponse) {
        List<String> tagNames = new ArrayList<>();

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
        JsonObject toptagsObject = jsonObject.getAsJsonObject("toptags");
        JsonArray tagsArray = toptagsObject.getAsJsonArray("tag");

        for (JsonElement tagElement : tagsArray) {
            JsonObject tagObject = tagElement.getAsJsonObject();
            String tagName = tagObject.get("name").getAsString();
            tagNames.add(tagName);
        }

        return tagNames;
    }
    
   
   
	

    public void getTagInfo(String tag) {
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
                    String tagInfo = extractTagInfo(jsonResponse.toString());
                    System.out.println(tagInfo);
                } else {
                    System.out.println("Aucune information disponible pour le tag : " + tag);
                }
            } else {
                System.out.println("Une erreur s'est produite lors de la récupération des informations du tag.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractTagInfo(String jsonResponse) {
        StringBuilder tagInfo = new StringBuilder();

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject tagObject = jsonObject.getJSONObject("tag");

            String tagName = tagObject.getString("name");
            int tagReach = tagObject.getInt("reach");
            int tagTaggings = tagObject.optInt("taggings", 0);
            int tagStreamable = tagObject.optInt("streamable", 0);

            tagInfo.append("Nom du tag : ").append(tagName).append("\n");
            tagInfo.append("Portée du tag : ").append(tagReach).append("\n");
            tagInfo.append("Nombre de taggings : ").append(tagTaggings).append("\n");
            tagInfo.append("Streamable : ").append(tagStreamable == 1 ? "Oui" : "Non").append("\n");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return tagInfo.toString();
    }
    
    //-------------------------------------------------------------------------------------//
    
    //---------------------Informations sur un album---------------------------------------//
    
    public void getAlbumInfo(String artistName, String albumName) {
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

            extractAlbumInfo(jsonResponse.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractAlbumInfo(String jsonResponse) {
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

            System.out.println("Artiste : " + artist);
            System.out.println("Album : " + albumName);
            System.out.println("Nombre de pistes : " + trackCount);
            
            if (!releaseDate.equals("N/A")) {
                System.out.println("Date de publication : " + releaseDate);
            } else {
                System.out.println("Date de publication : N/A");
            }
            
            System.out.println("Pistes :");

            for (int i = 0; i < trackCount; i++) {
                JSONObject trackObject = tracksArray.getJSONObject(i);
                String trackName = trackObject.getString("name");
                int duration = trackObject.getInt("duration");

                totalDuration += duration;

                System.out.println("- " + trackName + " (" + duration + " secondes)");
            }

            System.out.println("Durée totale : " + totalDuration + " secondes");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String encodeParameter(String parameter) {
        try {
            return URLEncoder.encode(parameter, StandardCharsets.UTF_8.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    
    //---------------------------------------------------------------------------------//
    //----------------------------------ARTIST-----------------------------------------//
    
    public void getArtistInfo(String artistName) {
        try {
            String encodedArtistName = URLEncoder.encode(artistName, "UTF-8");
            String url = BASE_URL + "?method=artist.getinfo&artist=" + encodedArtistName + "&api_key=" + API_KEY + "&format=json";

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

            extractArtistInfo(jsonResponse.toString());
            
         // Obtenir le top des albums
            String topAlbumsUrl = BASE_URL + "?method=artist.gettopalbums&artist=" + encodedArtistName + "&api_key=" + API_KEY + "&format=json";
            StringBuilder topAlbumsResponse = new StringBuilder();

            URL topAlbumsUrlObject = new URL(topAlbumsUrl);
            HttpURLConnection topAlbumsConnection = (HttpURLConnection) topAlbumsUrlObject.openConnection();
            topAlbumsConnection.setRequestMethod("GET");

            int topAlbumsResponseCode = topAlbumsConnection.getResponseCode();
            if (topAlbumsResponseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader topAlbumsReader = new BufferedReader(new InputStreamReader(topAlbumsConnection.getInputStream()));
                String line;
                while ((line = topAlbumsReader.readLine()) != null) {
                    topAlbumsResponse.append(line);
                }
                topAlbumsReader.close();
            }

            extractTopAlbumsInfo(topAlbumsResponse.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractArtistInfo(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject artistObject = jsonObject.getJSONObject("artist");

            String artistName = artistObject.getString("name");
            System.out.println("Nom de l'artiste : " + artistName);

            // Récupérer les artistes similaires
            if (artistObject.has("similar")) {
                JSONObject similarObject = artistObject.getJSONObject("similar");
                if (similarObject.has("artist")) {
                    JSONArray artistArray = similarObject.getJSONArray("artist");
                    System.out.println("Artistes similaires :");
                    for (int i = 0; i < artistArray.length(); i++) {
                        JSONObject similarArtist = artistArray.getJSONObject(i);
                        String similarArtistName = similarArtist.getString("name");
                        System.out.println("- " + similarArtistName);
                    }
                }
            }

            // Récupérer le top des albums de l'artiste
            if (artistObject.has("topalbums")) {
                JSONObject topAlbumsObject = artistObject.getJSONObject("topalbums");
                if (topAlbumsObject.has("album")) {
                    JSONArray albumArray = topAlbumsObject.getJSONArray("album");
                    System.out.println("Top des albums de l'artiste :");
                    for (int i = 0; i < albumArray.length(); i++) {
                        JSONObject album = albumArray.getJSONObject(i);
                        String albumName = album.getString("name");
                        System.out.println("- " + albumName);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
   /* public void getInfoArtist(String artistName) {
        try {
            String encodedArtistName = URLEncoder.encode(artistName, "UTF-8");
            String url = BASE_URL + "?method=artist.getinfo&artist=" + encodedArtistName + "&api_key=" + API_KEY + "&format=json";

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

            extractArtistInfo(jsonResponse.toString());

            // Obtenir le top des albums
            String topAlbumsUrl = BASE_URL + "?method=artist.gettopalbums&artist=" + encodedArtistName + "&api_key=" + API_KEY + "&format=json";
            StringBuilder topAlbumsResponse = new StringBuilder();

            URL topAlbumsUrlObject = new URL(topAlbumsUrl);
            HttpURLConnection topAlbumsConnection = (HttpURLConnection) topAlbumsUrlObject.openConnection();
            topAlbumsConnection.setRequestMethod("GET");

            int topAlbumsResponseCode = topAlbumsConnection.getResponseCode();
            if (topAlbumsResponseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader topAlbumsReader = new BufferedReader(new InputStreamReader(topAlbumsConnection.getInputStream()));
                String line;
                while ((line = topAlbumsReader.readLine()) != null) {
                    topAlbumsResponse.append(line);
                }
                topAlbumsReader.close();
            }

            extractTopAlbumsInfo(topAlbumsResponse.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private static void extractTopAlbumsInfo(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject topAlbumsObject = jsonObject.getJSONObject("topalbums");

            if (topAlbumsObject.has("album")) {
                JSONArray albumArray = topAlbumsObject.getJSONArray("album");
                System.out.println("Top des albums : ");
                for (int i = 0; i < albumArray.length(); i++) {
                    JSONObject album = albumArray.getJSONObject(i);
                    String albumName = album.getString("name");
                    System.out.println("- " + albumName);
                }
            } else {
                System.out.println("Aucun album trouvé pour cet artiste.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

   
    
    //-------------------------TRENDS--------------------------------------------//
    
    public void getGlobalTrendsInfo() {
        try {
            // Classement top 10 chansons
            String topTracksUrl = "http://ws.audioscrobbler.com/2.0/?method=chart.getTopTracks&api_key=" + API_KEY + "&format=json&limit=10";
            String topTracksResponse = sendGetRequest1(topTracksUrl);
            extractTopTracksInfo(topTracksResponse);

            // Classement top 10 albums
            String topAlbumsUrl = "http://ws.audioscrobbler.com/2.0/?method=chart.getTopAlbums&api_key=" + API_KEY + "&format=json&limit=10";
            String topAlbumsResponse = sendGetRequest1(topAlbumsUrl);
            extractTopAlbumsInfo(topAlbumsResponse);

            // Classement top 10 styles (tags)
            String topTagsUrl = "http://ws.audioscrobbler.com/2.0/?method=chart.getTopTags&api_key=" + API_KEY + "&format=json&limit=10";
            String topTagsResponse = sendGetRequest1(topTagsUrl);
            extractTopTagsInfo(topTagsResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String sendGetRequest1(String url) throws IOException {
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
            throw new IOException("Request failed with response code: " + responseCode);
        }
    }

    private static void extractTopTracksInfo(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        JSONArray itemsArray = jsonObject.getJSONArray("items");

        System.out.println("Classement top 10 chansons :");
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject track = itemsArray.getJSONObject(i);
            String trackName = track.getString("name");
            String artistName = track.getJSONArray("artists").getJSONObject(0).getString("name");
            int popularity = track.getInt("popularity");
            System.out.println("- Titre : " + trackName);
            System.out.println("  Artiste : "+artistName);
            System.out.println(" Popularité : " + popularity);
            System.out.println();
      }
      }
    
    
     private void extractTopAlbumsInfos(String response) throws JSONException {
     JSONObject jsonObject = new JSONObject(response);
     JSONArray itemsArray = jsonObject.getJSONArray("items");
     System.out.println("Classement top 10 albums :");
     for (int i = 0; i < itemsArray.length(); i++) {
         JSONObject album = itemsArray.getJSONObject(i);
         String albumName = album.getString("name");
         JSONArray artistsArray = album.getJSONArray("artists");
         StringBuilder artistNames = new StringBuilder();
         for (int j = 0; j < artistsArray.length(); j++) {
             if (j > 0) {
                 artistNames.append(", ");
             }
             artistNames.append(artistsArray.getJSONObject(j).getString("name"));
         }
         int popularity = album.getInt("popularity");
         System.out.println("- Album : " + albumName);
         System.out.println("  Artistes : " + artistNames.toString());
         System.out.println("  Popularité : " + popularity);
         System.out.println();
     }
     }

     private void extractTopGenresInfo(String response) throws JSONException {
     JSONObject jsonObject = new JSONObject(response);
     JSONArray itemsArray = jsonObject.getJSONArray("items");
     System.out.println("Classement top 10 styles (genres) :");
     for (int i = 0; i < itemsArray.length(); i++) {
         JSONObject artist = itemsArray.getJSONObject(i);
         JSONArray genresArray = artist.getJSONArray("genres");
         String artistName = artist.getString("name");
         StringBuilder genres = new StringBuilder();
         for (int j = 0; j < genresArray.length(); j++) {
             if (j > 0) {
                 genres.append(", ");
             }
             genres.append(genresArray.getString(j));
         }
         System.out.println("- Artiste : " + artistName);
         System.out.println("  Styles : " + genres.toString());
         System.out.println();
     }
     }
    
   

    //------------------------TRENDS PAR COUNTRY---------------------------------//
     
     public static void getCountryTrendsInfo(String country) {
         try {
             // Classement top 10 chansons par pays
             String topTracksUrl = "http://ws.audioscrobbler.com/2.0/?method=geo.getTopTracks&country=" + country + "&api_key=" + API_KEY + "&format=json";
             String topTracksResponse = sendGetRequest1(topTracksUrl);
             extractTopTracksInfo(topTracksResponse);

             // Classement top 10 albums par pays
             String topAlbumsUrl = "http://ws.audioscrobbler.com/2.0/?method=geo.getTopAlbums&country=" + country + "&api_key=" + API_KEY + "&format=json";
             String topAlbumsResponse = sendGetRequest1(topAlbumsUrl);
             extractTopAlbumsInfo(topAlbumsResponse);

             // Classement top 10 styles (tags) par pays
             String topTagsUrl = "http://ws.audioscrobbler.com/2.0/?method=geo.getTopTags&country=" + country + "&api_key=" + API_KEY + "&format=json";
             String topTagsResponse = sendGetRequest1(topTagsUrl);
             extractTopTagsInfo(topTagsResponse);

         } catch (IOException | JSONException e) {
             e.printStackTrace();
         }
     }
     
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
     
     private static void extractTopTagsInfo(String response) throws JSONException {
    	    JSONObject jsonObject = new JSONObject(response);
    	    JSONObject tagsObject = jsonObject.getJSONObject("tags");
    	    JSONArray tagArray = tagsObject.getJSONArray("tag");

    	    System.out.println("Classement top 10 styles (tags) :");
    	    for (int i = 0; i < tagArray.length(); i++) {
    	        JSONObject tag = tagArray.getJSONObject(i);
    	        String tagName = tag.getString("name");
    	        int reach = tag.getInt("reach");
    	        int taggings = tag.getInt("taggings");

    	        System.out.println("- Style : " + tagName);
    	        System.out.println("  Portée : " + reach);
    	        System.out.println("  Nombre de marquages : " + taggings);
    	        System.out.println();
    	    }
    	}
     
     
     //-----------------------------ALBUMS & CHANSONS SIMILAIRES ENTRE ARTISTES------------//
     
     
     public void searchSimilarAlbumsAndSongs(String artist1, String artist2) {
    	    try {
    	        
    	        String url = "http://ws.audioscrobbler.com/2.0/?method=artist.getsimilar&artist=" + artist1 +
    	                "&api_key=" + API_KEY + "&format=json";

    	        String response = sendGetRequest1(url);
    	        JSONObject json = new JSONObject(response);

    	        JSONArray similarArtistsArray = json.getJSONObject("similarartists").getJSONArray("artist");
    	        List<String> similarArtists = new ArrayList<>();

    	        // Obtenez une liste d'artistes similaires
    	        for (int i = 0; i < similarArtistsArray.length(); i++) {
    	            String similarArtist = similarArtistsArray.getJSONObject(i).getString("name");
    	            similarArtists.add(similarArtist);
    	        }

    	        // Recherchez des albums similaires pour chaque artiste similaire
    	        for (String similarArtist : similarArtists) {
    	            url = "http://ws.audioscrobbler.com/2.0/?method=artist.gettopalbums&artist=" + similarArtist +
    	                    "&api_key=" + API_KEY + "&format=json";
    	            response = sendGetRequest1(url);
    	            json = new JSONObject(response);

    	            JSONArray topAlbumsArray = json.getJSONObject("topalbums").getJSONArray("album");

    	            // Obtenez les noms des albums similaires
    	            for (int i = 0; i < topAlbumsArray.length(); i++) {
    	                String albumName = topAlbumsArray.getJSONObject(i).getString("name");
    	                System.out.println("Similar Album: " + albumName);
    	            }
    	        }

    	        // Recherchez des chansons similaires pour chaque artiste similaire
    	        for (String similarArtist : similarArtists) {
    	            url = "http://ws.audioscrobbler.com/2.0/?method=artist.gettoptracks&artist=" + similarArtist +
    	                    "&api_key=" + API_KEY + "&format=json";
    	            response = sendGetRequest1(url);
    	            json = new JSONObject(response);

    	            JSONArray topTracksArray = json.getJSONObject("toptracks").getJSONArray("track");

    	            // Obtenez les noms des chansons similaires
    	            for (int i = 0; i < topTracksArray.length(); i++) {
    	                String trackName = topTracksArray.getJSONObject(i).getString("name");
    	                System.out.println("Similar Track: " + trackName);
    	            }
    	        }

    	    } catch (IOException | JSONException e) {
    	        e.printStackTrace();
    	    }
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