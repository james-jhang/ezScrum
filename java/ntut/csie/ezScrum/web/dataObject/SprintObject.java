package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;
import java.util.Date;

import ntut.csie.ezScrum.dao.SprintDAO;
import ntut.csie.ezScrum.dao.StoryDAO;
import ntut.csie.ezScrum.web.databasEnum.SprintEnum;
import ntut.csie.jcis.core.util.DateUtil;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class SprintObject implements IBaseObject {
	private final static int DEFAULT_VALUE = -1;

	private long mId = DEFAULT_VALUE;
	private long mSerialId = DEFAULT_VALUE;
	private long mProjectId = DEFAULT_VALUE;

	private int mInterval = 0;
	private int mMembersAmount = 0;
	private int mHoursCanCommit = 0;
	private int mFocusFactor = 0;
	private String mSprintGoal = "";
	private Date mStartDate = new Date();
	private Date mDemoDate = new Date();
	private Date mDueDate = new Date();
	private String mDemoPlace = "";
	private String mDailyInfo = "";

	private long mCreateTime = 0;
	private long mUpdateTime = 0;

	public SprintObject(long projectId) {
		mProjectId = projectId;
	}

	public SprintObject(long id, Long serialId, long projectId) {
		mId = id;
		mSerialId = serialId;
		mProjectId = projectId;
	}

	public SprintObject setInterval(int interval) {
		mInterval = interval;
		return this;
	}

	public SprintObject setMembers(int membersAmount) {
		mMembersAmount = membersAmount;
		return this;
	}

	public SprintObject setHoursCanCommit(int hoursCanCommit) {
		mHoursCanCommit = hoursCanCommit;
		return this;
	}

	public SprintObject setFocusFactor(int focusFactor) {
		mFocusFactor = focusFactor;
		return this;
	}

	public SprintObject setSprintGoal(String sprintGoal) {
		mSprintGoal = sprintGoal;
		return this;
	}

	public SprintObject setStartDate(String startDate) {
		mStartDate = DateUtil.dayFilter(startDate);
		return this;
	}

	public SprintObject setDueDate(String dueDate) {
		mDueDate = DateUtil.dayFilter(dueDate);
		return this;
	}

	public SprintObject setDemoDate(String demoDate) {
		mDemoDate = DateUtil.dayFilter(demoDate);
		return this;
	}

	public SprintObject setDemoPlace(String demoPlace) {
		mDemoPlace = demoPlace;
		return this;
	}

	public SprintObject setDailyInfo(String dailyInfo) {
		mDailyInfo = dailyInfo;
		return this;
	}

	public SprintObject setCreateTime(long createTime) {
		mCreateTime = createTime;
		return this;
	}

	public SprintObject setUpdateTime(long updateTime) {
		mUpdateTime = updateTime;
		return this;
	}

	public long getId() {
		return mId;
	}

	public long getSerialId() {
		return mSerialId;
	}

	public long getProjectId() {
		return mProjectId;
	}

	public int getInterval() {
		return mInterval;
	}

	public int getMembersAmount() {
		return mMembersAmount;
	}

	public int getHoursCanCommit() {
		return mHoursCanCommit;
	}

	public int getFocusFactor() {
		return mFocusFactor;
	}

	public String getSprintGoal() {
		return mSprintGoal;
	}

	public String getStartDateString() {
		return DateUtil.formatBySlashForm(mStartDate);
	}

	public String getDueDateString() {
		return DateUtil.formatBySlashForm(mDueDate);
	}

	public String getDemoDateString() {
		return DateUtil.formatBySlashForm(mDemoDate);
	}

	public String getDemoPlace() {
		return mDemoPlace;
	}

	public String getDailyInfo() {
		return mDailyInfo;
	}

	public long getCreateTime() {
		return mCreateTime;
	}

	public long getUpdateTime() {
		return mUpdateTime;
	}

	public static SprintObject get(long id) {
		return SprintDAO.getInstance().get(id);
	}

	public ArrayList<StoryObject> getStories() {
		return StoryDAO.getInstance().getStoriesBySprintId(mId);
	}

	@Override
	public void save() {
		if (exists()) {
			doUpdate();
		} else {
			doCreate();
		}
	}

	@Override
	public void reload() {
		if (exists()) {
			SprintObject sprint = SprintDAO.getInstance().get(mId);
			resetData(sprint);
		}
	}

	@Override
	public boolean delete() {
		boolean success = SprintDAO.getInstance().delete(mId);
		if (success) {
			mId = DEFAULT_VALUE;
			mSerialId = DEFAULT_VALUE;
			mProjectId = DEFAULT_VALUE;
		}
		return success;
	}

	private void doCreate() {
		mId = SprintDAO.getInstance().create(this);
		reload();
	}

	private void doUpdate() {
		mUpdateTime = System.currentTimeMillis();
		SprintDAO.getInstance().update(this);
	}

	public void updateSerialId(long newSerialId) {
		mSerialId = newSerialId;
		SprintDAO.getInstance().updateSerialId(mId, newSerialId);
	}

	private void resetData(SprintObject sprint) {
		mId = sprint.getId();
		mProjectId = sprint.getProjectId();
		mSerialId = sprint.getSerialId();

		setInterval(sprint.getInterval());
		setMembers(sprint.getMembersAmount());
		setHoursCanCommit(sprint.getHoursCanCommit());
		setFocusFactor(sprint.getFocusFactor());
		setSprintGoal(sprint.getSprintGoal());
		setStartDate(sprint.getStartDateString());
		setDemoDate(sprint.getDemoDateString());
		setDemoPlace(sprint.getDemoPlace());
		setDailyInfo(sprint.getDailyInfo());
		setCreateTime(sprint.getCreateTime());
		setUpdateTime(sprint.getUpdateTime());
	}

	private boolean exists() {
		SprintObject sprint = SprintDAO.getInstance().get(mId);
		return sprint != null;
	}

	public boolean contains(Date date) {
		if ((date.compareTo(mStartDate) >= 0)
				&& (date.compareTo(mDueDate) <= 0)) {
			return true;
		}
		return false;
	}

	public boolean containsStory(StoryObject story) {
		boolean isContainsStory = false;
		ArrayList<StoryObject> stories = getStories();
		for (StoryObject currentStory : stories) {
			if (currentStory.getId() == story.getId()) {
				isContainsStory = true;
			}
		}
		return isContainsStory;
	}

	public boolean containsTask(TaskObject task) {
		boolean isContainsTask = false;
		ArrayList<StoryObject> stories = getStories();
		for (StoryObject story : stories) {
			if (story.containsTask(task)) {
				isContainsTask = true;
			}
		}
		return isContainsTask;
	}
	
	public static long getNextSprintId() {
		return SprintDAO.getInstance().getNextSprintId();
	}

	public String toString() {
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			return "JSON Exception";
		}
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject sprint = new JSONObject();
		JSONArray stories = new JSONArray();

		for (StoryObject story : getStories()) {
			stories.put(story.getId());
		}

		sprint.put(SprintEnum.ID, mId).put(SprintEnum.PROJECT_ID, mProjectId)
				.put(SprintEnum.START_DATE, getStartDateString())
				.put(SprintEnum.DUE_DATE, getDueDateString())
				.put(SprintEnum.INTERVAL, mInterval)
				.put(SprintEnum.MEMBERS, mMembersAmount)
				.put(SprintEnum.SERIAL_ID, mSerialId)
				.put(SprintEnum.GOAL, mSprintGoal)
				.put(SprintEnum.AVAILABLE_HOURS, mHoursCanCommit)
				.put(SprintEnum.FOCUS_FACTOR, mFocusFactor)
				.put(SprintEnum.DEMO_DATE, getDemoDateString())
				.put(SprintEnum.DEMO_PLACE, mDemoPlace)
				.put(SprintEnum.DAILY_INFO, mDailyInfo)
				.put(SprintEnum.CREATE_TIME, mCreateTime)
				.put(SprintEnum.UPDATE_TIME, mUpdateTime)
				.put("stories", stories);
		return sprint;
	}
}
