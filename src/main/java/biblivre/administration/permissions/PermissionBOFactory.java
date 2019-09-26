package biblivre.administration.permissions;

public interface PermissionBOFactory {
	public PermissionBO getInstance(String datasource, String schema);

	public PermissionBO getInstance(String schema);
}
