package biblivre.update.v4_1_0;

import org.springframework.stereotype.Service;

import biblivre.update.UpdateService;

@Service("v4_1_0")
public class Update implements UpdateService {

	@Override
	public String getVersion() {
		return "4.1.0";
	}

}
