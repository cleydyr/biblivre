package biblivre.digitalmedia;

import biblivre.core.file.BiblivreFile;
import java.net.URI;

/** How a media request is answered: bytes through Tomcat, or a redirect to a signed URL. */
public sealed interface MediaDownload {

    record Streamed(BiblivreFile file) implements MediaDownload {}

    record Redirected(URI location) implements MediaDownload {}
}
