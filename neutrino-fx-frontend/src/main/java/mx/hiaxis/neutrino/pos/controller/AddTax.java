package mx.hiaxis.neutrino.pos.controller;

import java.io.IOException;
import java.math.BigDecimal;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import mx.hiaxis.commons.fx.FxController;
import mx.hiaxis.commons.fx.FxNavigableController;
import mx.hiaxis.commons.persistence.api.BasicDao;
import mx.hiaxis.neutrino.pos.util.Validations;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs.MessageIcon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hiaxis.neutrino.entity.Tax;

@FxController(fxmlPath="/mx/hiaxis/neutrino/pos/fxml/add-tax.fxml")
public class AddTax extends FxNavigableController {

	@FXML private TextField name;
	@FXML private TextField code;
	@FXML private TextField	valor;
	
    @Autowired private BasicDao<Tax> taxesDao;

    private final Validations validations = new Validations();
     
	@FXML private void BtnExitHandler(ActionEvent event){
		
	}

	@FXML
	@Transactional(propagation=Propagation.REQUIRED)
	private void btnSaveTaxesHandler(ActionEvent event) throws IOException{

		if (validateFields() == false) {
			return;
		}


		Tax taxes = taxesDao.getNew();
		taxes.setName(name.getText());
		taxes.setCode(code.getText());
		BigDecimal value = new BigDecimal(valor.getText());
		taxes.setValue(value);
		taxes.save();

		
		//Lanzando el modal window, este codigo solo es de prueba.
		String headerMessage = "Operación Completada";
		String bodyMessage = "La información se ah salvado correctamente";
		NeutrinoMsgs.showDialog(headerMessage, bodyMessage, MessageIcon.INFO_ICON);
	}

	private boolean validateFields(){
		if(!validations.notEmpty(name, "NAME")){
			return false;
		}

		if (!validations.notEmpty(code, "CODE")) {
			return false;
		}

		if (!validations.notEmpty(valor, "VALOR")) {
			return false;
		}

		// eliminada temporalmente ya que no permite que el precio tenga punto decimal...
//		if (!validations.onlyNumbers(productPrice, "PRECIO")) {
//			return false;
//		}

		return true;
	}

	

}