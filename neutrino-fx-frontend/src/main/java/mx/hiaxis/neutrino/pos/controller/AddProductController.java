package mx.hiaxis.neutrino.pos.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import mx.hiaxis.commons.fx.FxController;
import mx.hiaxis.commons.fx.FxView;
import mx.hiaxis.commons.fx.util.EmbeddedScreenController;
import mx.hiaxis.commons.persistence.api.BasicDao;
import mx.hiaxis.neutrino.pos.util.UploadPictures;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs.MessageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hiaxis.commons.spring.util.OnDisplayAware;
import com.hiaxis.neutrino.entity.Product;

@FxController(
		fxmlPath = AddProductController.FXML_PATH,
		value    = AddProductController.QUALIFIER,
		title    = AddProductController.TITLE)
public class AddProductController extends EmbeddedScreenController implements OnDisplayAware{
	
	private static final Logger LOG = LoggerFactory.getLogger(AddProductController.class);
	
	public static final String FXML_PATH = "/mx/hiaxis/neutrino/pos/fxml/add-product.fxml";
	public static final String QUALIFIER = "add-product-view";
	public static final String TITLE     = "Nuevo Producto";

	@FXML private TextField skuId;
	@FXML private TextField skuDisambiguation;
	@FXML private TextField productName;
	@FXML private TextArea 	productDescription;
	@FXML private ImageView previewPicture;

    @Autowired  
    private BasicDao<Product> productDao;
    
    @Autowired @Qualifier(LookupProductController.QUALIFIER) 
    private FxView lookupProduct;
    
    @Autowired private UploadPictures uploadPictures;
	
	@FXML
	@Transactional(propagation=Propagation.REQUIRED)
	private void saveProductHandler(ActionEvent event) throws IOException{

		if (validateFields() == false) {
			return;
		}
		try{
			Product product = productDao.getNew();
			product.setName(productName.getText());
			product.setSkuCode(skuId.getText());
			product.setDisambiguation(skuDisambiguation.getText().charAt(0));
			product.setSummary(productDescription.getText());
			product.save();
			
			String newName = skuId.getText() + skuDisambiguation.getText();
			uploadPictures.savePicture(newName);
			
			NeutrinoMsgs.showDialog(
					"Operación Completada",
					"La información se ah salvado correctamente",
					MessageIcon.INFO_ICON);
			clearFields();
		}
		catch(DataIntegrityViolationException ex){
			//este error ocurrirá si el código sku y el desambiguador son idénticos.
			NeutrinoMsgs.showDialog(
					"No es posible agregar el producto", 
					ex.getLocalizedMessage() ,
					MessageIcon.INFO_ICON);
		}
	}
	
	@FXML 
	private void returnBack(ActionEvent event){
		lookupProduct.setAsChildrenOf(getParent());
	}
	
	@FXML
	private void onSelectPicture(ActionEvent event){
		try{
			if (!skuId.getText().isEmpty() && 
				!skuDisambiguation.getText().isEmpty()) {
				uploadPictures.pictureManagment(previewPicture);		
			} else {
				NeutrinoMsgs.showDialog(
						"Alerta", 
						"Sku y Disambiguacion no deben estar vacios", 
						MessageIcon.WARNING_ICON);
			}
		}
		catch (IOException e) {
			LOG.debug("Ah ocurrido una excepcion : " + e.getMessage());
		}
	}
	
	@Override
	public void onDisplay() {
		clearFields();
	}
	
	private void clearFields(){
		skuDisambiguation.setText("A");
		skuId.clear();
		productName.clear();
		productDescription.clear();
		previewPicture.setImage(null);
	}
	
	private boolean validateFields(){
		if(       skuId.getText().isEmpty()) { return false; }
		if( productName.getText().isEmpty()) { return false; }
		if( skuDisambiguation.getText().length() != 1){ return false; }
		
		return true;
	}

}