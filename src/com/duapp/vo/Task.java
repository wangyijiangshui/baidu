package com.duapp.vo;

import java.util.List;

/**
 * @author Administrator
 * @date 2018-02-21
 */
public class Task {

	private int id;
	private String task;
	private int taskStatus;
	private List<TaskRemark> taskRemarks;
	private String createTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public int getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(int taskStatus) {
		this.taskStatus = taskStatus;
	}

	public List<TaskRemark> getTaskRemarks() {
		return taskRemarks;
	}

	public void setTaskRemarks(List<TaskRemark> taskRemarks) {
		this.taskRemarks = taskRemarks;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}
