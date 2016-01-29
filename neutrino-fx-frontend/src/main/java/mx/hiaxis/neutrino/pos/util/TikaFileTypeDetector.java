package mx.hiaxis.neutrino.pos.util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileTypeDetector;

import org.apache.tika.Tika;
import org.apache.tika.mime.MimeTypes;

public class TikaFileTypeDetector extends FileTypeDetector{
	private final Tika tika = new Tika();
	
	public TikaFileTypeDetector() {
		super();
	}

	@Override
	public String probeContentType(Path path) throws IOException {
		
		String  fileNameDetected = tika.detect(path.toString());
		if (!fileNameDetected.equals(MimeTypes.OCTET_STREAM)) {
			return fileNameDetected;
		}
		
		String fileContentDetected = tika.detect(path.toFile());
		if (!fileContentDetected.equals(MimeTypes.OCTET_STREAM)) {
			return fileContentDetected;
		}
		
		return null;
	}

}