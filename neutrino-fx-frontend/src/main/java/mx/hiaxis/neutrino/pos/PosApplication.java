package mx.hiaxis.neutrino.pos;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import mx.hiaxis.commons.fx.application.HiaxisApplication;
import mx.hiaxis.neutrino.pos.controller.LogInController;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs.MessageIcon;
import mx.hiaxis.utils.HiaxisException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PosApplication {
	
    private static final Logger log = LoggerFactory.getLogger(PosApplication.class);
    
    public PosApplication() {

	}
    
    public static void main(String[] args) {
        log.warn("La aplicación ha sido lanzada a través de un método main, " +
                 "esto significa que la aplicación JavaFX no ha sido deployado correctamente");
        launch(args);
    }

    @Override
	protected final void doBeforeSceneLoading(Stage stage) {
    	try {
			String propertiesPath = "/mx/hiaxis/neutrino/pos/config/exception-config.properties";
			InputStream stream = PosApplication.class.getResourceAsStream(propertiesPath);
			Properties properties = new Properties();
			properties.load(stream);
		} catch (IOException e) {
			throw new RuntimeException("Exception while loading exception-config.properties", e);
		}
    	
	}
    
}