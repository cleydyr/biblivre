package biblivre.core;

public interface DAOFactory<T extends DAO> {
	public T getInstance(String schema);
}
