package mx.hiaxis.neutrino.pos.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.spi.FileTypeDetector;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs.MessageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadPictures {
	
	private static final Logger LOG = LoggerFactory.getLogger(UploadPictures.class);
	
	private long defaultFileSize = 12582912;
	private File file = null;
	private Path source;
	private ByteArrayOutputStream baos;

	private static final String CHOOSER_GOOD_TITLE = "Seleccionar Imagen.";
	private static final String CHOOSER_BAD_MESSAGE = "Ah seleccionado un tipo de archivo incorrecto.";
	
	private void fileChooserConfig(FileChooser fileChooser, String text){
		fileChooser.setTitle(text);
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All Images", "*.*"),
				new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png"));
	}
	
	public void pictureManagment(ImageView imageView) throws IOException{
		FileChooser fileChooser = new FileChooser();
		fileChooserConfig(fileChooser, CHOOSER_GOOD_TITLE);
		Stage stage = new Stage();
		file = fileChooser.showOpenDialog(stage);
		
		if (file != null) {
			source = Paths.get(file.toString());

			if (Files.size(source) <= defaultFileSize) {
				baos = new ByteArrayOutputStream();
				Files.copy(source, baos);
				
				if (baos.size() != 0) {
					if (validateTypeOfContent(source, fileChooser) == true) {
						previewPicture(imageView);
					}
				}
			} else {
				NeutrinoMsgs.showDialog("Warning", "El archivo rebasa el tamaño permitido", MessageIcon.WARNING_ICON);
			}
			
		} else {
			LOG.debug("Seleccionar imagen cancelado");
		}

	}
	
	private boolean validateTypeOfContent(Path source, FileChooser fileChooser){
		FileTypeDetector detector = new TikaFileTypeDetector();
		
		try {
			String contentType = detector.probeContentType(source);
			
			if (contentType.equals("image/jpeg") || contentType.equals("image/png")) {
				return true;
			} else {
				NeutrinoMsgs.showDialog("Error", CHOOSER_BAD_MESSAGE, MessageIcon.WARNING_ICON);
			}
			
		} catch (IOException e) {
			e.getMessage();
		}
		return false;
	}
	
	private void previewPicture(ImageView previewPicture){
		Image selectedPicture = new Image(file.toURI().toString());
		previewPicture.setImage(selectedPicture);
	}
	
	public void savePicture(String newName) {
		try {
			if (source != null) {
				Path target = Paths.get("./images" + source.getFileName());
				ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
				Files.copy(bais, target.resolveSibling(newName), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			LOG.debug("Error al guardar imagen: " + e.getMessage());
			NeutrinoMsgs.showDialog("Ops!", "Error al guardar imagen", MessageIcon.INFO_ICON);
		}
	}
	
}