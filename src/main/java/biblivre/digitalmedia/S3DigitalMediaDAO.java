package biblivre.digitalmedia;

import java.io.InputStream;
import java.sql.ResultSet;

import biblivre.core.file.BiblivreFile;
import biblivre.core.file.S3File;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3DigitalMediaDAO extends BaseDigitalMediaDAO {
        private final S3Client s3;
        private final String bucketName;

        public S3DigitalMediaDAO() {
                this.s3 = S3Client.builder()
                        .credentialsProvider(DefaultCredentialsProvider.create())
                        .build();

                this.bucketName = System.getenv("S3_BUCKET_NAME");
        }

        @Override
        protected void persistBinary(long oid, InputStream is, long size)
                throws Exception {

                PutObjectRequest request = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(String.valueOf(oid))
                        .build();

                s3.putObject(
                        request, RequestBody.fromInputStream(is, size));
        }

        @Override
        protected BiblivreFile populateBiblivreFile(ResultSet rs) throws Exception {
                long oid = rs.getLong("blob");

                GetObjectRequest request = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(String.valueOf(oid))
                        .build();

                ResponseInputStream<GetObjectResponse> inputStream =
                        s3.getObject(request);

                S3File file = new S3File(inputStream);

                file.setName(rs.getString("name"));
                file.setContentType(rs.getString("content_type"));
                file.setLastModified(rs.getTimestamp("created").getTime());
                file.setSize(rs.getLong("size"));

                return file;
        }
}