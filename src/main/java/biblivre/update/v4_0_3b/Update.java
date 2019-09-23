package biblivre.update.v4_0_3b;

import org.springframework.stereotype.Service;

import biblivre.update.UpdateService;

@Service("v4_0_3b")
public class Update implements UpdateService {

	@Override
	public String getVersion() {
		return "4.0.3b";
	}

}
