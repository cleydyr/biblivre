package biblivre.update.v6_0_0$1_1_0$alpha;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import biblivre.update.UpdateService;

public class Update implements UpdateService {

	@Override
	public void doUpdateScopedBySchema(Connection connection) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			URL systemResource = ClassLoader.getSystemResource("update/" + getVersion() + "/update.sql");

			URI uri = systemResource.toURI();

			StringBuilder sb = new StringBuilder();

			Files.lines(Paths.get(uri)).forEachOrdered(sb::append);

			statement.execute(sb.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getVersion() {
		return "6.0.0-1.1.0-alpha";
	}
}
