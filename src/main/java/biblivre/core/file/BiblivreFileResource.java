package biblivre.core.file;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.AbstractResource;

/**
 * Exposes a {@link BiblivreFile} as a Spring {@link org.springframework.core.io.Resource}, so
 * Spring's resource and resource region message converters can write it. The resource does not own
 * the file: whoever created the file still closes it.
 *
 * <p>A file that is not {@link BiblivreFile#isSeekable() seekable} wraps a single stream, so this
 * resource can then be read once only, like an {@link
 * org.springframework.core.io.InputStreamResource}.
 */
public class BiblivreFileResource extends AbstractResource {

    private final BiblivreFile file;

    private boolean read;

    public BiblivreFileResource(BiblivreFile file) {
        this.file = file;
    }

    public BiblivreFile getBiblivreFile() {
        return this.file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.read && !this.file.isSeekable()) {
            throw new IllegalStateException(
                    "%s can be read only once".formatted(this.getDescription()));
        }

        this.read = true;

        return this.file.openStream();
    }

    @Override
    public boolean exists() {
        return this.file.exists();
    }

    @Override
    public boolean isReadable() {
        return this.exists();
    }

    @Override
    public long contentLength() {
        return this.file.getSize();
    }

    @Override
    public long lastModified() {
        return this.file.getLastModified();
    }

    @Override
    public String getFilename() {
        return this.file.getName();
    }

    @Override
    public String getDescription() {
        return "BiblivreFile [%s]".formatted(this.file.getName());
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || (other instanceof BiblivreFileResource resource && resource.file == this.file);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this.file);
    }
}
