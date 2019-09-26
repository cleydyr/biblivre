package biblivre.administration.permissions;

public interface PermissionDAOFactory {
	public PermissionDAO getInstance(String schema);
}
