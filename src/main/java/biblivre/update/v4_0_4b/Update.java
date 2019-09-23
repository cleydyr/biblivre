package biblivre.update.v4_0_4b;

import org.springframework.stereotype.Service;

import biblivre.update.UpdateService;

@Service("v4_0_4b")
public class Update implements UpdateService {

	@Override
	public String getVersion() {
		return "4.0.4b";
	}

}
