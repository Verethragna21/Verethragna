package mx.hiaxis.neutrino.photon.data;

import mx.hiaxis.util.Document;
import mx.hiaxis.util.DocumentSupplier;
import mx.hiaxis.util.Persistent;
import mx.hiaxis.util.Storage;
import mx.hiaxis.util.ElementMapper.Type;

public class Person implements DocumentSupplier, Persistent {

	public enum Gender {MALE, FEMALE, UNDEFINED, NONE, OTHER}
	private final Document document;
	
	
	public Person(Document document) {
		if(document.contains("gender")){
			document.forKey("gender", (String val)->{
				Gender.valueOf(val); //we try to convert, if it fails an exception will be thrown.
			});
		}
		this.document = document;
	}
	
	@Override
	public Document getDocument() {
		return Document.inmutableCopy(document);
	}

	@Override
	public void saveTo(Storage storage) {
		storage.execute("INSERT INTO Person ("
				+ "gender, "
				+ "namePrefix, "
				+ "firstName, "
				+ "middleName, "
				//-----------
				+ "lastName, "
				+ "nameSuffix, "
				+ "preferedName, "
				+ "uniqueCode) "
				//-----------
				+ "VALUES (?,?,?,?, ?,?,?,?)", mapper -> {
			mapper.set(document, "gender", Type.STRING);
			mapper.set(document, "name-prefix", Type.STRING);
			mapper.set(document, "first-name", Type.STRING);
			mapper.set(document, "middle-name", Type.STRING);
			
			mapper.set(document, "last-name", Type.STRING);
			mapper.set(document, "name-suffix", Type.STRING);
			mapper.set(document, "preferred-name", Type.STRING);
			mapper.set(document, "unique-code", Type.STRING);
		});
	}
	

}
