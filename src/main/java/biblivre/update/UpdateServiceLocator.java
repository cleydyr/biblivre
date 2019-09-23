package biblivre.update;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateServiceLocator {
	private List<UpdateService> updateServices;

	@Autowired
	public UpdateServiceLocator(List<UpdateService> updateServices) {
		this.updateServices = updateServices;
	}

	public List<UpdateService> getUpdateServices() {
		return updateServices;
	}
}
