package biblivre.update.v4_0_2b;

import org.springframework.stereotype.Service;

import biblivre.update.UpdateService;

@Service("v4_0_2b")
public class Update implements UpdateService {

	@Override
	public String getVersion() {
		return "4.0.2b";
	}

}
