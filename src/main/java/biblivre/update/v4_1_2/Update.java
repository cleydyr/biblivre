package biblivre.update.v4_1_2;

import org.springframework.stereotype.Service;

import biblivre.update.UpdateService;

@Service("v4_1_2")
public class Update implements UpdateService {

	@Override
	public String getVersion() {
		return "4.1.2";
	}

}
