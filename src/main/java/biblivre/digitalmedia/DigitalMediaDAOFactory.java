package biblivre.digitalmedia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biblivre.core.AbstractDAO;
import biblivre.core.exceptions.DAOException;

public class DigitalMediaDAOFactory extends AbstractDAO {
	private static final Logger logger =
		LoggerFactory.getLogger(DigitalMediaDAOFactory.class);

	private static final String _DIGITAL_MEDIA_DAO_IMPL_CLASS_NAME =
			System.getenv("DIGITAL_MEDIA_DAO_IMPL");

	private DigitalMediaDAOFactory() {}

	@SuppressWarnings("unchecked")
	public static BaseDigitalMediaDAO getDigitalMediaDAOImpl(String schema) {
		try {
			Class<? extends AbstractDAO> klass =
				(Class<? extends AbstractDAO>) Class.forName(
					_DIGITAL_MEDIA_DAO_IMPL_CLASS_NAME);

			return (BaseDigitalMediaDAO) AbstractDAO.getInstance(klass, schema);
		} catch (ClassNotFoundException e) {
			logger.error(e.getMessage(), e);

			throw new DAOException(e);
		}
	}
}
