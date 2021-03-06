package ntut.csie.ezScrum.web.action.backlog.product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.refactoring.manager.ProjectManager;
import ntut.csie.ezScrum.test.CreateData.CreateProductBacklog;
import ntut.csie.ezScrum.test.CreateData.CreateProject;
import ntut.csie.ezScrum.test.CreateData.CreateTag;
import ntut.csie.ezScrum.test.CreateData.InitialSQL;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import servletunit.struts.MockStrutsTestCase;

public class AjaxRemoveStoryTagActionTest extends MockStrutsTestCase {
	private CreateProject mCP;
	private CreateTag mCT;
	private CreateProductBacklog mCPB;
	private int mProjectCount = 1;
	private Configuration mConfig;
	private final String mActionPath = "/AjaxRemoveStoryTag";
	
	public AjaxRemoveStoryTagActionTest(String testMethod) {
        super(testMethod);
    }
	
	protected void setUp() throws Exception {
		mConfig = new Configuration();
		mConfig.setTestMode(true);
		mConfig.save();
		
		// 初始化 SQL
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		// 新增Project
		mCP = new CreateProject(mProjectCount);
		mCP.exeCreate();
		
		mCT = new CreateTag(2, mCP);
		mCT.exe();
		
		mCPB = new CreateProductBacklog(2, mCP);
		mCPB.exe();
		
		super.setUp();
		
		// 設定讀取的 struts-config 檔案路徑
		setContextDirectory(new File(mConfig.getBaseDirPath() + "/WebContent")); 
		setServletConfigFile("/WEB-INF/struts-config.xml");
		setRequestPathInfo(mActionPath);
		
		// ============= release ==============
		ini = null;
	}
	
	protected void tearDown() throws IOException, Exception {
		//	刪除資料庫
		InitialSQL ini = new InitialSQL(mConfig);
		ini.exe();
		
		//	刪除外部檔案
		ProjectManager projectManager = new ProjectManager();
		projectManager.deleteAllProject();
		
		mConfig.setTestMode(false);
		mConfig.save();

		super.tearDown();
		
		ini = null;
		projectManager = null;
		mCP = null;
		mCT = null;
		mCPB = null;
		mConfig = null;
	}
	
	/**
	 * setup:
	 * 1. 新增一個專案
	 * 2. 新增兩個Tag
	 * 3. 新增兩個Story
	 * 4. 個別替這兩個Story新增兩個Tags
	 * 
	 * Test:
	 * 1. 移除某一個story的一個Tag
	 * 2. 驗證此Story還存在一個Tag
	 */
	public void testRemoveTagFromStory(){
		mCT.attachTagToStory(mCPB);
		
		// ================ set request info ========================
		ArrayList<Long> storyIds = mCPB.getStoryIds();
		ArrayList<TagObject> tags = mCT.getTagList();
		
		request.setHeader("Referer", "?PID=" + mCP.getProjectList().get(0).getName());
		addRequestParameter("tagId", String.valueOf(tags.get(0).getId()));
		addRequestParameter("storyId", String.valueOf(storyIds.get(0)));
		String expectedStoryName = mCPB.getStories().get(0).getName();
		int expectedStoryImportance = mCPB.getStories().get(0).getImportance();
		int expectedStoryEstimate = mCPB.getStories().get(0).getEstimate();
		int expectedStoryValue = mCPB.getStories().get(0).getValue();
		String expectedStoryHoewToDemo = mCPB.getStories().get(0).getHowToDemo();
		String expectedStoryNote = mCPB.getStories().get(0).getNotes();
		long storyId = mCPB.getStoryIds().get(0);
		// ================ set session info ========================
		request.getSession().setAttribute("UserSession", mConfig.getUserSession());
		// ================ 執行 action ======================
		actionPerform();
		// ================ assert ========================
		verifyNoActionErrors();
		verifyNoActionMessages();
		
		//	assert response text
		StringBuilder expectedResponseText = new StringBuilder();
		expectedResponseText.append("{\"success\":true,")
							.append("\"Total\":1,")
							.append("\"Stories\":[{")
							.append("\"Id\":").append(storyId).append(",")
							.append("\"Type\":\"Story\",")
							.append("\"Name\":\"").append(expectedStoryName).append("\",")
							.append("\"Value\":").append(expectedStoryValue).append(",")	
							.append("\"Estimate\":").append(expectedStoryEstimate).append(",")
							.append("\"Importance\":").append(expectedStoryImportance).append(",")		
							.append("\"Tag\":\"").append(tags.get(1).getName()).append("\",")
							.append("\"Status\":\"new\",")
							.append("\"Notes\":\"").append(expectedStoryNote).append("\",")
							.append("\"HowToDemo\":\"").append(expectedStoryHoewToDemo).append("\",")
							.append("\"Link\":\"\",")
							.append("\"Release\":\"\",")
							.append("\"Sprint\":\"None\",")
							.append("\"FilterType\":\"DETAIL\",")
							.append("\"Attach\":false,")
							.append("\"AttachFileList\":[]")
							.append("}]}");
		String actualResponseText = response.getWriterBuffer().toString();
		assertEquals(expectedResponseText.toString(), actualResponseText);
	}
}
