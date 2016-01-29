package mx.hiaxis.neutrino.pos.controller;

import static mx.hiaxis.neutrino.security.NeutrinoSecurityPermission.USE_POS;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import mx.hiaxis.commons.fx.FxController;
import mx.hiaxis.commons.fx.FxNavigableController;
import mx.hiaxis.neutrino.batavia.api.Repository;
import mx.hiaxis.neutrino.entity.Message;
import mx.hiaxis.neutrino.pos.controller.inventory.InventoryMainController;
import mx.hiaxis.neutrino.pos.provider.NeutrinoCurrentUserProvider;

import org.springframework.beans.factory.annotation.Autowired;

import com.hiaxis.commons.spring.util.OnDisplayAware;

@FxController(fxmlPath="/mx/hiaxis/neutrino/pos/fxml/dashboard.fxml")
public class DashboardController extends FxNavigableController implements OnDisplayAware{

	@FXML private ImageView accountIcon;
	@FXML private Label headerInfoWithUserName;
	@FXML private Label fullPersonName;
	@FXML private Label currentUserRol;	
	@FXML private Label sessionStartDateTime;
	@FXML private Label sessionDuration;
	@FXML private ListView<Message> messages;
	
	//Testing
	@FXML private HBox headerFrame;
	@FXML private VBox userInfoFrame;
	@FXML private VBox quickAccessFrame;
	
	@Autowired private LogInController    		logIn;
	@Autowired private AdminController    		admin;
	@Autowired private CheckoutController 		checkout;
	@Autowired private InventoryMainController	inventoryMain;
	@Autowired private Repository               repository;

	@Autowired private NeutrinoCurrentUserProvider currentUser;

	@FXML
	private void goSalesHandler(ActionEvent event){
		if(currentUser.credential().can(USE_POS)){
			checkout.setAsCurrentScreen();
		}
	}

	@FXML
	private void goAdministration(ActionEvent event){
		admin.setAsCurrentScreen();
	}
	
	@FXML
	private void goInventory(ActionEvent event){
//		if (currentUser.getCredential().can(USE_ADMIN)) {
			inventoryMain.setAsCurrentScreen();
//		}
	}

	@FXML
	private void exitHandler(ActionEvent event){
		logIn.setAsCurrentScreen();
	}

	@Override
	public void onDisplay() {
		//Codigo seteando el efecto personalizado dropshadow.
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5);
		dropShadow.setColor(Color.GREY);
		dropShadow.setOffsetY(2);
		
		headerFrame.setEffect(dropShadow);
		userInfoFrame.setEffect(dropShadow);
		quickAccessFrame.setEffect(dropShadow);
		
		//Nota: Este pedazo de codigo es solo temporal.
		if(currentUser.credential() != null && !currentUser.isRoot()) {
			String compositeInfo = "NeutrinoPOS  |  " + currentUser.credential().getUsername();
			
			headerInfoWithUserName.setText(compositeInfo);
			fullPersonName.setText(currentUser.credential().getPerson().getFullName());
			
			LocalDateTime ldt = currentUser.credential().currentSession().getStartTime();
			DateTimeFormatter format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
			sessionStartDateTime.setText(ldt.format(format));
			
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new UpdateFieldsTimerTask(), 0, 10000);
			
		}
		
		Image image = new Image("/mx/hiaxis/neutrino/pos/media/Account512.png");
		accountIcon.setImage(image);
		accountIcon.setPreserveRatio(true);
		
		initializeMessages();
	}
	
	/*
	 * Esta clase se movera a un archivo independiente mas adelante si es requerido
	 */
	private class UpdateFieldsTimerTask extends TimerTask {

		LocalDateTime ldt = currentUser.credential().currentSession().getStartTime();
		
		@Override
		public void run() {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					long hours = ChronoUnit.HOURS.between(ldt, LocalDateTime.now());
					long minutes = ChronoUnit.MINUTES.between(ldt, LocalDateTime.now());
					sessionDuration.setText(hours + " hrs. " + minutes + " m. ");
				}
			});
		}
		
	}

	private void initializeMessages(){
		messages.getItems().clear();
		messages.setPlaceholder(new Label("No tiene mensajes por mostrar"));
		
		if(currentUser.credential() != null && !currentUser.isRoot()){
			Long currentUserId = currentUser.credential().getId();
			Set<Message> currentMesssages = repository.executeQuery(Message.QUERY_MESSAGES_BY_USER_ID, currentUserId);
			if(!currentMesssages.isEmpty()){
				messages.getItems().clear();
				messages.getItems().addAll(currentMesssages);
			}
		}
	}
}