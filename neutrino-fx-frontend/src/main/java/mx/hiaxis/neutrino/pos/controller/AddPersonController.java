package mx.hiaxis.neutrino.pos.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import mx.hiaxis.commons.fx.FxController;
import mx.hiaxis.commons.fx.util.EmbeddedScreenController;
import mx.hiaxis.neutrino.batavia.api.Repository;
import mx.hiaxis.neutrino.entity.Person;
import mx.hiaxis.neutrino.entity.Person.Gender;
import mx.hiaxis.neutrino.pos.util.UploadPictures;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs.MessageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@FxController(
		fxmlPath = "/mx/hiaxis/neutrino/pos/fxml/add-person.fxml",
		title    = "Nueva Persona")
public class AddPersonController extends EmbeddedScreenController implements Initializable{
	
	private static final Logger LOG = LoggerFactory.getLogger(AddPersonController.class);


//	@FXML private ComboBox<Title> 		personalTitle;
	@FXML private ComboBox<Gender> 		gender;
	@FXML private TextField 			idCode;
	@FXML private TextField 			name;
	@FXML private TextField 			optionalName;
	@FXML private TextField 			secondName;
	@FXML private TextField 			optionalSecondName;
	@FXML private ImageView 			imageView;
	@FXML private GridPane 				gridPane;
	@FXML private Button				uploadPicture;
	
//	ObservableList<Title> titleItems = FXCollections.observableArrayList(Title.values());
	ObservableList<Gender> genderItems = FXCollections.observableArrayList(Gender.values());
	
	@Autowired 
	private UploadPictures uploadPictures;
	
	@Autowired private AddCredentialController addCredentials;
	
	@Autowired private Repository repository;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		fillComboBoxes();
		fileChooser();
	}

    @FXML
    @Transactional(propagation=Propagation.REQUIRED)
    void saveNewUser(ActionEvent event) {
    	
    	Person person = repository.getNew(Person.class);
//    	person.setTitle(personalTitle.getSelectionModel().getSelectedItem());
    	person.setGender(gender.getSelectionModel().getSelectedItem());
    	person.setUniqueCode(idCode.getText());
    	person.setFirstName(name.getText());
    	person.setMiddleName(optionalName.getText());
    	person.setLastName(secondName.getText());
    	person.setNameSuffix(optionalSecondName.getText());
    	person.save();
    	
    	uploadPictures.savePicture(idCode.getText());
    	
    	NeutrinoMsgs.showDialog("Informacion Almacenada", "La informacion se ah guardado correctamente", 
    			MessageIcon.INFO_ICON);
    }
    
    @FXML
    void returnLookup(ActionEvent event){
    	addCredentials.setAsChildrenOf(getParent());
    }
    
    private void fillComboBoxes(){
//    	personalTitle.getItems().addAll(titleItems);
		gender.getItems().addAll(genderItems);
    }
    
    private void fileChooser(){
    	
    	uploadPicture.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				
				if (!idCode.getText().isEmpty()) {
					
					try {
						uploadPictures.pictureManagment(imageView);
					} catch (IOException e) {
						LOG.debug("Ah ocurrido una excepciòn: " + e.getMessage());
					}
					
				} else {
					NeutrinoMsgs.showDialog("Error", "Codigo Identificador no puede estar vacio", 
							MessageIcon.WARNING_ICON);
				}
				
			}
		});	
    	
    }
    
}
