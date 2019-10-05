package biblivre.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import biblivre.core.auth.AuthorizationPoints;

public class AuthorizationPointsInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(
			HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		_includeAuthorizationPoints(request);

		return true;
	}

	private void _includeAuthorizationPoints(HttpServletRequest req) {
		String schema = (String) req.getAttribute("schema");

		HttpSession session = req.getSession();

		AuthorizationPoints authorizationPoints =
				(AuthorizationPoints)session.getAttribute("authorizationPoints");

		if (authorizationPoints == null) {
			authorizationPoints = AuthorizationPoints.getNotLoggedInstance(schema);
		}

		session.setAttribute("authorizationPoints", authorizationPoints);
	}
}
