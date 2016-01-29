package mx.hiaxis.neutrino.photon.service;

import static mx.hiaxis.neutrino.photon.Application.NEUTRINO_MEDIA_V1_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.hiaxis.neutrino.photon.data.Person;
import mx.hiaxis.util.DocumentSupplier;
import mx.hiaxis.util.Storage;

@RestController
public class PersonService {
	
	@Autowired
	private Storage storage;
	
	@RequestMapping(
			method = POST,
			path = "/person/", 
			consumes = NEUTRINO_MEDIA_V1_VALUE,
			produces = NEUTRINO_MEDIA_V1_VALUE)
	public DocumentSupplier createPerson(@RequestBody Person person){
		person.saveTo(storage);
		return person;
	}  

}
