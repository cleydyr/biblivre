package biblivre.core;

public class RequestContext {
	private ExtendedRequest xRequest;
	private ExtendedResponse xResponse;
	private boolean headerOnly;
	private AbstractHandler handler;

	
	public RequestContext(ExtendedRequest xRequest, ExtendedResponse xResponse, boolean headerOnly,
			AbstractHandler handler) {
		super();
		this.xRequest = xRequest;
		this.xResponse = xResponse;
		this.headerOnly = headerOnly;
		this.handler = handler;
	}


	public ExtendedRequest getxRequest() {
		return xRequest;
	}


	public ExtendedResponse getxResponse() {
		return xResponse;
	}


	public boolean isHeaderOnly() {
		return headerOnly;
	}


	public AbstractHandler getHandler() {
		return handler;
	}
}
