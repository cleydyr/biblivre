package biblivre.core;

import biblivre.core.auth.AuthorizationBO;

public class AuthorizationThreadLocal {
    private static final ThreadLocal<AuthorizationBO> threadLocal = new ThreadLocal<>();

    public static void setAuthorizationBO(AuthorizationBO authorizationBO) {
        threadLocal.set(authorizationBO);
    }

    public static AuthorizationBO get() {
        return threadLocal.get();
    }

    public static AuthorizationBO remove() {
    	AuthorizationBO value = threadLocal.get();

        threadLocal.remove();

        return value;
    }
}
