package ntut.csie.ezScrum.web.action;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ntut.csie.ezScru.web.microservice.AccountRESTClientProxy;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AccountObject;

public class NotifyNotificationLogout extends Action{
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		AccountObject account = session.getAccount();
		String firebaseToken = account.getFirebaseToken();
		String token = account.getToken();
		
		AccountRESTClientProxy ap = new AccountRESTClientProxy(token);
		String s = ap.notifyServiceLogout(account.getId(), firebaseToken);
		response.setContentType("text/html; charset=utf-8");
		
		try {
			response.getWriter().write(s);
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
