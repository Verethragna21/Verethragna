package mx.hiaxis.neutrino.photon.service;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.hiaxis.neutrino.photon.Application;
import mx.hiaxis.neutrino.photon.data.Credential;
import mx.hiaxis.util.Storage;

@RestController
public class CredentialService {

	@Autowired private Storage storage;
	
	@RequestMapping(
			path="/user/{userId}/credentials",
			method=GET,
			produces=Application.NEUTRINO_MEDIA_V1_VALUE)
	public List<Credential> getCredentials(@PathVariable("userId") String id){
		new Credential(id);
		return Credential.getCredential(storage);
	}
	
	@RequestMapping(
		path="/user/{personId}/credentials",
		method=POST,
		produces=Application.NEUTRINO_MEDIA_V1_VALUE,
		consumes=Application.NEUTRINO_MEDIA_V1_VALUE)
	public void saveCredential(@RequestBody Credential credential,
			@PathVariable("personId") Long id){
		credential.saveTo(storage);
	}
	
}