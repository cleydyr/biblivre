package biblivre.digitalmedia.s3;

import biblivre.core.file.BiblivreFile;
import biblivre.core.utils.Constants;
import java.io.IOException;
import java.io.OutputStream;
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
        byte[] buffer = new byte[Constants.DEFAULT_BUFFER_SIZE];

        int read;

        while ((read = inputStream.read(buffer)) > 0) {
            out.write(buffer, 0, read);
        }
    }
}
