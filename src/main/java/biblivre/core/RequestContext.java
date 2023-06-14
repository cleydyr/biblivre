package biblivre.core;

public record RequestContext(
        ExtendedRequest xRequest,
        ExtendedResponse xResponse,
        boolean headerOnly,
        AbstractHandler handler) {}
