package mx.hiaxis.neutrino.pos.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import mx.hiaxis.commons.fx.FxController;
import mx.hiaxis.commons.fx.util.EmbeddedScreenController;
import mx.hiaxis.neutrino.batavia.api.Repository;
import mx.hiaxis.neutrino.entity.Credential;
import mx.hiaxis.neutrino.entity.Person;
import mx.hiaxis.neutrino.entity.SecurityRole;
import mx.hiaxis.neutrino.pos.provider.IPersonLookupProvider;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs.MessageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@FxController(
		fxmlPath = "/mx/hiaxis/neutrino/pos/fxml/add-credential.fxml",
		title    = "Nueva Credencial")
public class AddCredentialController extends EmbeddedScreenController implements Initializable{

	private static final Logger LOG = LoggerFactory.getLogger(AddCredentialController.class);

	@FXML private ComboBox<SecurityRole> adminitrativeRol;
	@FXML private TextField selectedPerson;
	@FXML private TextField user;
	@FXML private TextField	email;
	@FXML private TextField password;
	@FXML private TextArea  comments;

	@Autowired private IPersonLookupProvider personLookupProvider;
	@Autowired private LookupPersonController lookupPerson;
	@Autowired private AddPersonController addPerson;
	@Autowired private LookupUserController lookupUsers;
	
	@Autowired private Repository repository;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		fillComboBoxes();
	}

	@FXML
	@Transactional(propagation=Propagation.REQUIRED)
	private void saveCredentials(ActionEvent event){
		
		Credential credential = repository.getNew(Credential.class);
		credential.setPerson(personLookupProvider.getSelectedPerson());
		credential.addRole(adminitrativeRol.getSelectionModel().getSelectedItem());
		credential.setUsername(user.getText());
		credential.setPassword(password.getText());
		credential.setContactEmail(email.getText());
		credential.setComment(comments.getText());
		credential.save();
		
		NeutrinoMsgs.showDialog("Informacion Almacenada", "la informacion se ah salvado correctamente", 
				MessageIcon.INFO_ICON);
	}

	@FXML
	private void findPerson(ActionEvent event) {
		lookupPerson.showAsModalScreen();
		Person person = personLookupProvider.getSelectedPerson();
		if (person != null) {
			selectedPerson.setText(person.toString());
			LOG.debug("Persona seleccionada cargada.");
		}
	}
	
	@FXML private void addNewUser(ActionEvent event){
		addPerson.setAsChildrenOf(getParent());
	}
	
	@FXML private void returnToAddCredentials(ActionEvent event){
		lookupUsers.setAsChildrenOf(getParent());
	}
	
	private void fillComboBoxes(){
		adminitrativeRol
			.getItems()
			.addAll( 
					repository.fetchAll( SecurityRole.class));
	}

}