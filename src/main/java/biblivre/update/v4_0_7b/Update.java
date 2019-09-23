package biblivre.update.v4_0_7b;

import org.springframework.stereotype.Service;

import biblivre.update.UpdateService;

@Service("v4_0_7b")
public class Update implements UpdateService {

	@Override
	public String getVersion() {
		return "4.0.7b";
	}

}
