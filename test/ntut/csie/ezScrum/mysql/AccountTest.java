package ntut.csie.ezScrum.mysql;

import java.util.List;

import junit.framework.TestCase;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateAccount;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.UserInformation;
import ntut.csie.ezScrum.web.dataObject.UserObject;
import ntut.csie.ezScrum.web.sqlService.MySQLService;

public class AccountTest extends TestCase {
	private MySQLService mService;
	private Configuration configuration = null;
	
	public AccountTest(String testMethod) {
		super(testMethod);
	}

	protected void setUp() throws Exception {
		configuration = new Configuration();
		configuration.setTestMode(true);
		configuration.store();
		
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		mService = new MySQLService(configuration);
		mService.openConnect();
	}

	protected void tearDown() throws Exception {
		InitialSQL ini = new InitialSQL(configuration);
		ini.exe();
		mService.closeConnect();
		// 刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		projectManager.initialRoleBase(configuration.getDataPath());
		
		configuration.setTestMode(false);
		configuration.store();
		
		mService = null;
		configuration = null;
		super.tearDown();
	}
	
	public void testCreateAccount() {
		String id = "account";
		String name = "account robot";
		String password = "account robot";
		String email = "iaccount@mail.com";
		String enable = "true";
		
		UserInformation user = new UserInformation(id, name, password, email, enable);
		boolean result = mService.createAccount(user);
		
		assertTrue(result);
	}
	
	public void testUpdateAccount() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccountList().get(0).getId();
		String account = createAccount.getAccount_ID(1);
		String name = "account robot";
		String password = "account robot";
		String email = "update@mail.com";
		String enable = "true";
		
		UserInformation user = new UserInformation(id, account, name, password, email, enable);
		boolean result = mService.updateAccount(user);
		
		assertTrue(result);
	}
	
	public void testDeleteAccount() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccountList().get(0).getId();
		
		boolean result = mService.deleteAccount(id);
		
		assertTrue(result);
	}
	
	public void testGetAccountById() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccount_ID(1);
		
		UserObject result = mService.getAccount(id);
		
		assertEquals(id, result.getAccount());
	}
	
	public void testGetAccountList() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		
		List<UserObject> result = mService.getAccountList();
		
		assertEquals(2, result.size());	// include admin
	}
	
	public void testConfirmAccount() {
		CreateAccount createAccount = new CreateAccount(1);
		createAccount.exe();
		String id = createAccount.getAccount_ID(1);
		String password = createAccount.getAccount_PWD(1);
		
		UserObject result = mService.confirmAccount(id, password);
		
		assertEquals(id, result.getAccount());
	}
}