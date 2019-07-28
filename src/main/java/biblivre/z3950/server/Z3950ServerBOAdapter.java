package biblivre.z3950.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import biblivre.z3950.client.config.Z3950Config;
import br.org.biblivre.z3950server.Z3950ServerBO;

public class Z3950ServerBOAdapter extends Z3950ServerBO {
	private static ApplicationContext _context;

	public ApplicationContext getContext() {
		if (_context == null) {
			try {
				_context = new AnnotationConfigApplicationContext(Z3950Config.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return _context;
	}
}
