package biblivre.digitalmedia.s3;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

class S3FileTest {
    private static final byte[] CONTENT =
            "0123456789abcdefghijklmnopqrstuvwxyz".getBytes(StandardCharsets.US_ASCII);

    private static S3File newFile() {
        S3File file =
                new S3File(
                        new ResponseInputStream<>(
                                GetObjectResponse.builder().build(),
                                AbortableInputStream.create(new ByteArrayInputStream(CONTENT))));
        file.setSize(CONTENT.length);
        return file;
    }

    @Test
    void copy_middleSlice_writesOnlyRequestedBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        newFile().copy(out, 10, 5);

        assertArrayEquals(Arrays.copyOfRange(CONTENT, 10, 15), out.toByteArray());
    }

    @Test
    void copy_withoutRange_writesWholeFile() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        newFile().copy(out);

        assertArrayEquals(CONTENT, out.toByteArray());
    }
}
