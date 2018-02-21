package com.duapp.vo;

/**
 * 操作结果消息
 * 
 * @author Js
 * @date 2013-04-09
 */
public class BaseMessage {

	private boolean result;
	private String msg;
	private Object data;

	public BaseMessage(boolean result) {
		this.result = result;
	}
	
	public BaseMessage(boolean result, String msg) {
		this.result = result;
		this.msg = msg;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
