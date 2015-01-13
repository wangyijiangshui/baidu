package com.duapp.vo;

/**
 * 操作结果消息
 * 
 * @author Js
 * @date 2013-04-09
 */
public class Message {

	private boolean result;
	private String msg;

	public Message(boolean result, String msg) {
		this.result = result;
		this.msg = msg;
	}
	
	public Message(String msg) {
		this.msg = msg;
	}

	public Message(boolean result) {
		this.result = result;
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
}
