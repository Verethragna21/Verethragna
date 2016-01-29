package mx.hiaxis.neutrino.pos.controller;

import static javafx.scene.layout.BackgroundPosition.DEFAULT;
import static javafx.scene.layout.BackgroundRepeat.NO_REPEAT;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import mx.hiaxis.commons.fx.FxController;
import mx.hiaxis.commons.fx.FxNavigableController;
import mx.hiaxis.neutrino.batavia.api.Repository;
import mx.hiaxis.neutrino.entity.Credential;
import mx.hiaxis.neutrino.pos.provider.NeutrinoCurrentUserProvider;
import mx.hiaxis.neutrino.pos.view.AnimatedAuthorization;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs.MessageIcon;

import org.springframework.beans.factory.annotation.Autowired;

import com.hiaxis.commons.spring.util.OnDisplayAware;
import com.hiaxis.commons.spring.util.OnLoadAware;

@FxController(
		title = "Inicio de sesión",
		fxmlPath  = "/mx/hiaxis/neutrino/pos/fxml/login.fxml")
public class LogInController extends FxNavigableController implements OnLoadAware, OnDisplayAware{

//    private static final Logger LOG = LoggerFactory.getLogger(LogInController.class);

	@FXML private TextField	userName;
	@FXML private Pane      container;
	@FXML private Pane		innerContainer;
	@FXML private Pane      stackedContainer;
	@FXML private Label     outputMessage;
	
	@Autowired private DashboardController dashboard;
	@Autowired private NeutrinoCurrentUserProvider currentUser;
	@Autowired private Repository repository;
	
	private AnimatedAuthorization passwordPrompt;
	
	@Override
	public void onLoad() {
		Image image = new Image("/mx/hiaxis/neutrino/pos/media/background1920.png");
		BackgroundImage bgImage = new BackgroundImage(image, NO_REPEAT, NO_REPEAT, DEFAULT, BackgroundSize.DEFAULT);
		container.setBackground(new Background(bgImage));
		
		passwordPrompt = new AnimatedAuthorization(
				a -> {
					outputMessage.setVisible(false);
					userName.clear();
					userName.requestFocus();
				}, 
				innerContainer);
		
		stackedContainer.getChildren().add(passwordPrompt);
	}
	
	@Override
	public void onDisplay() {
		passwordPrompt.restart();
		outputMessage.setVisible(false);
	}

	@FXML
	private void loginHandler(ActionEvent event){
		String name = userName.getText().trim();
		if (name.isEmpty() == false) {	
			Credential credential = repository.executeQuery(Credential.BY_USERNAME, name);
			if(credential != null){
				authorize(credential);
			}
			else if(currentUser.isNameOfRoot(name)){
				authorizeRoot();
			}
			else{
				showUserNotFound();
			}
		}
		else{
			showPleaseFillUserField();
		}
	}
	
	private void authorizeRoot(){
		passwordPrompt.authorizeFor( 
				pass -> {
					String name = userName.getText().trim();
					if(currentUser.authAsRoot(name, pass)){
						currentUser.setAsRoot();
						dashboard.setAsCurrentScreen();
						return true;
					}
					else{
						showNotAuthorizedMessage();
						return false;
					}
				});	
	}
	
	private void authorize(Credential credential){
		switch (credential.getAuthType()) {
		case PASSWORD:
			passwordPrompt.authorizeFor(
				pass -> { 
					switch(credential.authenticate(pass)){ 
					case ATHORIZED:
							//sessionDao.getNew())){
						currentUser.setAs(credential);
						dashboard.setAsCurrentScreen();
						return true;
					case DENIED:
					case BANNED:
					default:
						showNotAuthorizedMessage();
						return false;
					}
				});
			break;
		default:
			throw new UnsupportedOperationException("por ahora sólo se soporta el inicio de sesión con password.");
		}
	}

	private void showPleaseFillUserField(){
		outputMessage.setText("SE REQUIERE USUARIO");
		outputMessage.setVisible(true);
	}
	
	private void showUserNotFound(){
		outputMessage.setText("USUARIO NO RECONOCIDO");
		outputMessage.setVisible(true);
	}
	
	private void showNotAuthorizedMessage(){
		NeutrinoMsgs.showDialog(
				"Lo sentimos", 
				"La información proporcionada no es correcta.", 
				MessageIcon.INFO_ICON);
	}

}
