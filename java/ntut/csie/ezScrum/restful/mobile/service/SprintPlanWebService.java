package ntut.csie.ezScrum.restful.mobile.service;

import java.sql.SQLException;
import java.util.ArrayList;

import ntut.csie.ezScrum.restful.mobile.support.ConvertSprint;
import ntut.csie.ezScrum.restful.mobile.support.ConvertSprintBacklog;
import ntut.csie.ezScrum.web.dataInfo.SprintInfo;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

public class SprintPlanWebService extends ProjectWebService {
	ProjectObject mProject;
	SprintPlanHelper mSprintPlanHelper;
	
	public SprintPlanWebService(AccountObject user, String projectName) throws LogonException {
		super(user, projectName);
		mProject = getAllProjects().get(0);
		mSprintPlanHelper = new SprintPlanHelper(mProject);
	}

	/**
	 * 新增 sprint
	 */
	public void createSprint(String sprintJson) throws JSONException {
		SprintInfo sprintInfo = new SprintInfo(sprintJson);
		mSprintPlanHelper.createSprint(sprintInfo);
	}

	/**
	 * 刪除 sprint
	 */
	public void deleteSprint(long id) {
		mSprintPlanHelper.deleteSprint(id);
	}
	
	/**
	 * 更新修改後的 sprint 資料
	 * @throws JSONException 
	 */
	public void updateSprint(String sprintJson) throws JSONException {
		SprintInfo sprintInfo = new SprintInfo(sprintJson);
		mSprintPlanHelper.updateSprint(sprintInfo.id, sprintInfo);
	}

	/**
	 * 取得 Project 內所有 Sprint 以及當前 Sprint ID
	 **/
	public String getAllSprints() throws JSONException{
		ArrayList<SprintObject> sprintList = mSprintPlanHelper.getAllSprints();
		return ConvertSprint.convertSprintsToJsonString(sprintList);
	}

	public String getCurrentSprintJsonString() throws JSONException{
		// 以當前日期找進行中的Sprint，若無進行中的Sprint，則往後找未過期的Sprint.
		SprintObject currentSprint = mProject.getCurrentSprint();
		// 如果 project 中沒有 sprint 則回傳空字串
		if (currentSprint != null) {
			return ConvertSprint.convertSprintToJsonString(currentSprint);
		}
		return "";
	}

	/**
	 * 取得  Sprint 包含 story
	 * @throws JSONException 
	 */
	public String getSprintWithStories(long sprintId) throws SQLException, JSONException {
		SprintObject sprint = mSprintPlanHelper.getSprint(sprintId);
		return ConvertSprint.convertSprintToJsonString(sprint);
	}
	
	/****
	 * 取得所有Sprint information. 以及當前Sprint ID.
	 */
	public String getRESTFulResponseString() throws JSONException {
		// 以當前日期找進行中的Sprint ID，若無進行中的Sprint，則往後找未過期的Sprint ID.
		SprintObject currentSprint = mSprintPlanHelper.getCurrentSprint();
		return ConvertSprintBacklog.getSprintBacklogJsonString(currentSprint);
	}
}
