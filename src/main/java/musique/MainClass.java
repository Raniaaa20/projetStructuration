package musique;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainClass {
    private static APIManager apiManager;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        apiManager = new APIManager();

        boolean exit = false;
        while (!exit) {
            printMenu();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    getInfoByTag(scanner);
                    break;
                case "2":
                	getInfoAlbum(scanner);
                    break;
                case "3":
                    getInfoArtist(scanner);
                    break;
                case "4":
                	getGlobalTrendsInfo();
                    break;
              /*  case "5":
                	getGlobalTrendsInfoCountry(scanner);
                    break;*/
                case "6":
                	searchSimilarAlbumsAndSongs(scanner);
                    break;
              /*  case "7":
                    (scanner);
                    break;
                case "8":
                    (scanner);
                    break;*/
                case "0":
                    exit = true;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
                    break;
            }
        }

        scanner.close();
    }

    

	


	private static void printMenu() {
        System.out.println("=== MENU ===");
        System.out.println("1. Obtenir des informations sur un tag");
        System.out.println("2. Obtenir des informations sur un album");
        System.out.println("3. Obtenir des informations sur un artiste");
        System.out.println("4. Obtenir des informations sur les tendances globales");
        System.out.println("5. Obtenir des informations sur les tendances globales par pays");
        System.out.println("6. Chercher des chansons/albums similaires entre artistes");
        System.out.println("7. ");
        System.out.println("8. ");
        System.out.println("9. ");
        System.out.println("0. Quitter");
        System.out.print("Choisissez une option : ");
    }

	
	//---------------------------------------TAG----------------------------------//
	private static void getInfoByTag(Scanner scanner) {
	    List<String> tagNames = getTagList();
	    for (String s : tagNames) {
	    	System.out.println(s);
	    }
	    System.out.print("Entrez le nom du tag : ");
	    String tag = scanner.nextLine();

	    // Appel de la méthode correspondante dans LastFmAPIManager
	    //apiManager.getTagInfo(tag);
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
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    if (jsonResponse.length() > 0) {
	        tagNames = extractTagNames(jsonResponse.toString());
	    }

	    return tagNames;
	}

	private static List<String> extractTagNames(String jsonResponse) {
	    List<String> tagNames = new ArrayList<>();

	    try {
	        JSONObject jsonObject = new JSONObject(jsonResponse);
	        JSONObject toptagsObject = jsonObject.getJSONObject("toptags");
	        JSONArray tagsArray = toptagsObject.getJSONArray("tag");

	        for (int i = 0; i < tagsArray.length(); i++) {
	            JSONObject tagObject = tagsArray.getJSONObject(i);
	            String tagName = tagObject.getString("name");
	            tagNames.add(tagName);
	        }
	    } catch (JSONException e) {
	        e.printStackTrace();
	    }

	    return tagNames;
	}
	
	
	//----------------------------------ALBUM------------------------------------------//
	
	private static void getInfoAlbum(Scanner scanner) {
		
		System.out.print("Entrez le nom de l'artiste : ");
        String artistName = scanner.nextLine();

        System.out.print("Entrez le nom de l'album : ");
        String albumName = scanner.nextLine();

        apiManager.getAlbumInfo(artistName, albumName);
        
    }
	
	

	//------------------------------ARTIST---------------------------------------------//
	
	private static void getInfoArtist(Scanner scanner) {
		System.out.print("Entrez le nom de l'artiste : ");
	    String artistName = scanner.nextLine();

	    apiManager.getArtistInfo(artistName);
		
	}
	
    
    //--------------------------Global TREND-------------------------------------------//
	
	private static void getGlobalTrendsInfo() {
	    apiManager.getGlobalTrendsInfo();
	}
	

	//-------------------------TREND BY COUNTRY----------------------------------------//
	/*public static void getGlobalTrendsInfoCountry(Scanner scanner) {
	   
	    System.out.println("Veuillez saisir le pays pour obtenir les tendances : ");
	    String country = scanner.nextLine();

	    APIManager apiManager = new APIManager();
	    apiManager.getGlobalTrendsInfoCountry(country);
	}*/
	
	//----------------------SIMILAR SONGS & ALBUMS------------------------------------//
	
	public static void searchSimilarAlbumsAndSongs(Scanner scanner) {
	    
	    System.out.println("Veuillez entrer le nom du premier artiste : ");
	    String artist1 = scanner.nextLine();
	    
	    System.out.println("Veuillez entrer le nom du deuxième artiste : ");
	    String artist2 = scanner.nextLine();
	    
	    APIManager apiManager = new APIManager();
	    apiManager.searchSimilarAlbumsAndSongs(artist1, artist2);
	}

	

}
