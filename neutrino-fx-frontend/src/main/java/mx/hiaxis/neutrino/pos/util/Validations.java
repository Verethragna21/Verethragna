package mx.hiaxis.neutrino.pos.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.Control;
import javafx.scene.control.TextInputControl;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs;
import mx.hiaxis.neutrino.pos.view.NeutrinoMsgs.MessageIcon;

public class Validations {
	
	public Validations() {	}
	
	private Pattern p;
	private Matcher m;
	
	public boolean notEmpty(Control field, String fieldName){
		if (((TextInputControl) field).getText().isEmpty()) {
			NeutrinoMsgs.showDialog("Problemas", "El campo " + fieldName + " no puede quedar vacio.", 
					MessageIcon.WARNING_ICON);
			field.requestFocus();
			return false;
		}
		return true;
	}
	
	public boolean onlyNumbers(Control field, String fieldName){
		p = Pattern.compile("^[0-9]*$");
		m = p.matcher(((TextInputControl) field).getText());
		if (!((TextInputControl) field).getText().isEmpty()) {
			if (!m.find()) {
				NeutrinoMsgs.showDialog("Problemas", "El campo " + fieldName + " solo acepta numeros.", 
						MessageIcon.WARNING_ICON);
				((TextInputControl) field).clear();
				((TextInputControl) field).requestFocus();
				return false;
			}
		}
		return true;
	}

}