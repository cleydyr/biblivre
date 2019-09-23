package biblivre.update.v4_1_6;

import org.springframework.stereotype.Service;

import biblivre.update.UpdateService;

@Service("v4_1_6")
public class Update implements UpdateService {

	@Override
	public String getVersion() {
		return "4.1.6";
	}

}
