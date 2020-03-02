package com.jp.lib;

import java.util.*;

public class ResponseMaker {

	public ResponseMaker() {
		// TODO Auto-generated constructor stub
	}
	
	public String status;
	
	public String message;
	
	public Map<String, Object> body;
	
	public String error;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Object> getBody() {
		return body;
	}

	public void setBody(Map<String, Object> body) {
		this.body = body;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	public String toString() {
		return "ResponseMaker [status=" + status + ", message=" + message + ", body=" + body + ", error=" + error + "]";
	}

	
}
