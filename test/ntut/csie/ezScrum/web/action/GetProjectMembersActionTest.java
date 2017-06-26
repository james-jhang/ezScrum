package ntut.csie.ezScrum.web.action;

import java.io.File;

import ntut.csie.ezScru.web.microservice.IAccountController;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.InitialSQL;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.test.CreateData.AddUserToRole;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.AccountHelper;
import servletunit.struts.MockStrutsTestCase;

public class GetProjectMembersActionTest extends MockStrutsTestCase {

	private CreateProject mCP;
	private Configuration mConfig;
	private final String mACTION_PATH = "/getProjectMembers";
	private ProjectObject mProject;

	public GetProjectMembersActionTest(String testName) {
		super(testName);
	}

	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();

		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		// 新增一測試專案
		mCP = new CreateProject(1);
		mCP.exeCreateForDb();
		mProject = mCP.getAllProjects().get(0);

		super.setUp();

		// ================ set action info ========================
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent"));
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mACTION_PATH);

		ini = null;
	}

	protected void tearDown() throws Exception {
		// 刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();

		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();

		ini = null;
		mCP = null;
		mConfig = null;
		mProject = null;
	}

	private String getExpectedProjectMember(AccountObject user) {
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("<Members>")
		        .append("<Member>")
		        .append("<ID>").append(user.getId()).append("</ID>")
		        .append("<Account>").append(user.getUsername()).append("</Account>")
		        .append("<Name>").append(user.getNickName()).append("</Name>")
		        .append("<Role>ScrumMaster</Role>")
		        .append("<Enable>").append(user.getEnable()).append("</Enable>")
		        .append("</Member>")
		        .append("</Members>");
		return expectedResponseText.toString();
	}

	/**
	 * 正常狀態，新增一名帳號至專案
	 * 
	 * @throws InterruptedException
	 */
	public void testGetProjcetMembers1() throws InterruptedException {
		CreateAccount CA = new CreateAccount(1);
		CA.exe();

		AddUserToRole addUserToRole = new AddUserToRole(mCP, CA);
		addUserToRole.exe_SM();

		// ================ set request info ========================
		String projectName = this.mProject.getName();
		request.setHeader("Referer", "?projectName=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();

		// assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(getExpectedProjectMember(CA.getAccountList().get(0)), actualResponseText);
	}

	/**
	 * 修改顯示名稱
	 */
	public void testGetProjcetMembers2() {
		CreateAccount CA = new CreateAccount(1);
		CA.exe();
		CA.setAccount_RealName(1);

		AddUserToRole addUserToRole = new AddUserToRole(mCP, CA);
		addUserToRole.exe_SM();

		// ================ set request info ========================
		String projectName = this.mProject.getName();
		request.setHeader("Referer", "?projectName=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();

		// assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(getExpectedProjectMember(CA.getAccountList().get(0)), actualResponseText);
	}

	/**
	 * 修改Role
	 * 
	 * @throws Exception
	 */
	public void testGetProjcetMembers3() throws Exception {
		CreateAccount CA = new CreateAccount(1);
		CA.exe();

		AddUserToRole AUTR = new AddUserToRole(mCP, CA);
		AUTR.exe_PO();

		IAccountController accountHelper = new AccountHelper();
		accountHelper.removeAssignRole(CA.getAccountList().get(0).getId(), mCP.getAllProjects().get(0).getId(), ScrumEnum.SCRUMROLE_PRODUCTOWNER);

		AUTR.exe_SM();

		// ================ set request info ========================
		String projectName = this.mProject.getName();
		request.setHeader("Referer", "?projectName=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();

		// assert response text
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(getExpectedProjectMember(CA.getAccountList().get(0)), actualResponseText);
	}

	/**
	 * disable account
	 */
	public void testGetProjcetMembers4() throws InterruptedException {
		CreateAccount CA = new CreateAccount(1);
		CA.exe();

		AddUserToRole AUTR = new AddUserToRole(mCP, CA);
		AUTR.exe_SM();

		AUTR.setEnable(CA, 0, false);

		// ================ set request info ========================
		String projectName = this.mProject.getName();
		request.setHeader("Referer", "?projectName=" + projectName);
		addRequestParameter("_dc", String.valueOf(System.currentTimeMillis()));

		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());

		// ================ 執行 action ======================
		actionPerform();

		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();

		// assert response text
		String expectedResponseText = "<Members></Members>";
		String actualResponseText = response.getWriterBuffer().toString();

		assertEquals(expectedResponseText, actualResponseText);
	}
}
