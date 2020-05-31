package biblivre.z3950.server;

import org.jzkit.z3950.server.Z3950Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Z3950LocalServer {
	Z3950Listener listener;

	private boolean active;

	Logger logger = LoggerFactory.getLogger(Z3950LocalServer.class);

	public boolean isActive() {
		return this.active;
	}

	private void setActive(boolean isActive) {
		this.active = isActive;
	}

	public Z3950Listener getListener() {
		return this.listener;
	}

	public void setListener(Z3950Listener listener) {
		this.listener = listener;
	}

	public void startServer() {
		if (!isActive()) {
			this.listener.start();

			setActive(true);
		}
	}

	public void stopServer() {
		if (isActive()) {
			try {
				this.listener.shutdown(0);
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}

			setActive(false);
		}
	}
}
