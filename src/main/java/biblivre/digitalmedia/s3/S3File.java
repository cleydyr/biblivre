package biblivre.digitalmedia.s3;

import biblivre.core.file.BiblivreFile;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

public class S3File extends BiblivreFile {

    private final ResponseInputStream<GetObjectResponse> inputStream;

    public S3File(ResponseInputStream<GetObjectResponse> inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public boolean exists() {
        return inputStream != null;
    }

    @Override
    public void copy(OutputStream out, long start, long size) throws IOException {
        IOUtils.copyLarge(inputStream, out, start, size);
    }

    /** The single GetObject stream this file wraps; it can be read only once. */
    @Override
    public InputStream openStream() {
        return inputStream;
    }
}
