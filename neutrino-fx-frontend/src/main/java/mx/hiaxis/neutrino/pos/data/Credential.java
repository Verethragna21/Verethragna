package mx.hiaxis.neutrino.photon.data;

import java.util.ArrayList;
import java.util.List;

import mx.hiaxis.util.Document;
import mx.hiaxis.util.DocumentSupplier;
import mx.hiaxis.util.ElementMapper.Type;
import mx.hiaxis.util.MapDocument;
import mx.hiaxis.util.Persistent;
import mx.hiaxis.util.ResultEntry;
import mx.hiaxis.util.ResultMapper;
import mx.hiaxis.util.Storage;

public class Credential implements DocumentSupplier, Persistent {

	private static Document document;
	
	private static final String USERNAME = "username";
	private static final String SYSTEM_PASSWORD = "systemPassword";
	private static final String PERSON_ID = "person_id";
	private static final String CONTACT_EMAIL = "contactEmail";
	private static final String AUTH_TYPE = "authType";
	
	@Override
	public Document getDocument() {
		return document;
	}

	public Credential(Document document) {
		if (document.contains(USERNAME) && document.contains(SYSTEM_PASSWORD) &&
				document.contains(PERSON_ID) && document.contains(CONTACT_EMAIL) &&
				document.contains(AUTH_TYPE)) {
			Credential.document = document;
		} else {
			throw new RuntimeException("El documento no se puede leer");
		}
	}
	
	public Credential(String id){
		document = new MapDocument();
		document.put(PERSON_ID, id);
	}
	
	private Credential(ResultEntry resultEntry) {
		document = new MapDocument();
		document.put(USERNAME, resultEntry.get(USERNAME, String.class));
		document.put(PERSON_ID, resultEntry.get(PERSON_ID, Long.class));
		document.put(CONTACT_EMAIL, resultEntry.get(CONTACT_EMAIL, String.class));
		document.put(AUTH_TYPE, resultEntry.get(AUTH_TYPE, String.class));
	}
	
	@Override
	public void saveTo(Storage storage) {
		storage.execute(
			"INSERT INTO Credential(username,systemPassword,person_id,"
					+ "contactEmail,authType) VALUES(?,?,?,?,?);",
			params -> {
				params.set(document, USERNAME, Type.STRING);
				params.set(document, SYSTEM_PASSWORD, Type.STRING);
				params.set(document, PERSON_ID, Type.STRING);
				params.set(document, CONTACT_EMAIL, Type.STRING);
				params.set(document, AUTH_TYPE, Type.STRING);
		});
	}
	
	public static List<Credential> getCredential(Storage storage){
		return storage.execute(
			"SELECT username,person_id,contactEmail,authType FROM Credential "
			+ "WHERE person_id=(?)", 
			params -> {
				params.set(document, PERSON_ID, Type.STRING);
			}, 
			(ResultMapper result) -> {
				List<Credential> list = new ArrayList<>();
				for (ResultEntry resultEntry : result) {
					list.add(new Credential(resultEntry));
				}
				return list;
			}
		);
	}

}