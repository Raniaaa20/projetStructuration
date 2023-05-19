package musique;


import java.util.List;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Controller {
	
	@FXML 
	Button btag;
	@FXML 
	Button balbum;
	@FXML 
	Button bartist;
	@FXML 
	Button bglob;
	@FXML 
	Button bpays;
	@FXML 
	Button bevolution;
	@FXML 
	Button bsimil;
	@FXML 
	ChoiceBox<String> taglist;
	@FXML 
	Text instruct;
	@FXML 
	TextArea result;
	@FXML 
	TextField artisttext;
	@FXML 
	TextField albumtext;
	@FXML 
	Button OK;
	
	
	@FXML 
	public void initialize() {
		
		taglist.setVisible(false);
		instruct.setVisible(false);
		artisttext.setVisible(false);
		albumtext.setVisible(false);
		OK.setVisible(false);
		
		
	}
	
	
	@FXML
	public void getTag() {
		artisttext.setVisible(false);
	    albumtext.setVisible(false);
	    OK.setVisible(false);
	    List<String> tags = APIManager.getTagList();
	    ObservableList<String> tagItems = FXCollections.observableArrayList(tags);
	    taglist.setItems(tagItems);

	    taglist.setVisible(true);
	    instruct.setText("Veuillez saisir le tag dont vous voulez consulter les informations : ");
	    instruct.setVisible(true);

	    taglist.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
	        if (newValue != null) {
	            String tagInfo = APIManager.getTagInfo(newValue);
	            result.setWrapText(true);
	            result.setText(tagInfo);
	        } else {
	            result.setText("");
	        }
	    });
	}

	@FXML
	public void getAlbum() {
	    taglist.setVisible(false);
	    instruct.setText("Veuillez saisir le nom de l'artiste et de l'album");
	    artisttext.setVisible(true);
	    albumtext.setVisible(true);
	    OK.setVisible(true);

	    OK.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	            // Récupérer les saisies de l'utilisateur pour l'artiste et l'album
	            String artist = artisttext.getText();
	            String album = albumtext.getText();

	            // Vérifier si les champs sont vides
	            if (artist.isEmpty() || album.isEmpty()) {
	                result.setText("Aucune information pour la requête saisie, veuillez saisir un nom d'artiste et d'album valides");
	            } else {
	                // Appeler la méthode getAlbumInfo de l'APIManager avec les saisies de l'utilisateur
	                String albumInfo = APIManager.getAlbumInfo(artist, album);

	                // Vérifier si aucun résultat n'est trouvé
	                if (albumInfo.isEmpty()) {
	                    result.setText("Aucune information pour la requête saisie, veuillez saisir un nom d'artiste et d'album valides");
	                } else {
	                    result.setWrapText(true);
	                    result.setText(albumInfo);
	                }
	            }
	        }
	    });
	}

	@FXML
	public void getArtist() {
	    taglist.setVisible(false);
	    instruct.setVisible(true);
	    albumtext.setVisible(false);
	    OK.setVisible(true);
	    instruct.setText("Veuillez saisir le nom d'artiste dont vous voulez consulter les informations");
	    artisttext.setVisible(true);
	    result.setText(""); // Réinitialiser le champ de résultat

	    OK.setOnAction(new EventHandler<ActionEvent>() {
	        @Override
	        public void handle(ActionEvent event) {
	            // Récupérer le nom d'artiste saisi par l'utilisateur
	            String artistName = artisttext.getText();

	            // Vérifier si le champ est vide
	            if (artistName.isEmpty()) {
	                result.setText("Veuillez saisir un nom d'artiste valide");
	            } else {
	                // Appeler la méthode getArtistInfo de l'APIManager avec le nom d'artiste saisi
	                String artistInfo = APIManager.getArtistInfo(artistName);

	                // Vérifier si aucun résultat n'est trouvé
	                if (artistInfo.isEmpty()) {
	                    result.setText("Aucune information pour l'artiste saisi");
	                } else {
	                    result.setWrapText(true);
	                    result.setText(artistInfo);
	                }
	            }
	        }
	    });
	}

	@FXML
	public void getTrendGlob() {
		taglist.setVisible(false);
		instruct.setVisible(false);
		artisttext.setVisible(false);
		albumtext.setVisible(false);
		OK.setVisible(false);
		
		result.setWrapText(true);
		String tendance = APIManager.getGlobalTrendsInfo();
		result.setText(tendance);
	}
	
	public void getTrendPays() {
	    taglist.setVisible(false);
	    instruct.setVisible(true);
	    artisttext.setVisible(true);
	    albumtext.setVisible(false);
	    OK.setVisible(true);

	    instruct.setText("Veuillez saisir le pays dont vous voulez voir les tendances : ");
	    artisttext.setPromptText("Nom pays");

	    OK.setOnAction(event -> {
	        // Récupération du nom du pays saisi
	        String country = artisttext.getText();

	        // Appel de la méthode getGlobalTrendsInfoCountry de APIManager
	        String trendsInfo = APIManager.getGlobalTrendsInfoCountry(country);

	        result.setText(trendsInfo);
	    });
	}

	
	public void getEvolution() {}
	
	public void getSimilar() {
	    taglist.setVisible(false);
	    instruct.setVisible(true);
	    artisttext.setVisible(true);
	    albumtext.setVisible(true);
	    OK.setVisible(true);

	    instruct.setText("Veuillez saisir le nom des deux artistes pour voir les similarités : ");
	    artisttext.setPromptText("Nom artiste 1");
	    albumtext.setPromptText("Nom artiste 2");

	    OK.setOnAction(event -> {
	        // Récupération des noms des artistes saisis
	        String artist1 = artisttext.getText();
	        String artist2 = albumtext.getText();

	        // Appel de la méthode searchSimilarAlbumsAndSongs de APIManager
	        String similarInfo = APIManager.searchSimilarAlbumsAndSongs(artist1, artist2);

	        result.setText(similarInfo);
	    });
	}

}
