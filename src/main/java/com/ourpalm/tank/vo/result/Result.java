package com.ourpalm.tank.vo.result;

public class Result {

	public static final byte SUCCESS = 1;
	public static final byte FAILURE = 0;

	private byte result;
	private String info = "";

	public byte getResult() {
		return result;
	}
	public void setResult(byte result) {
		this.result = result;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public boolean isSuccess(){
		return this.result == Result.SUCCESS;
	}

	public Result success(){
		this.result = Result.SUCCESS;
		return this;
	}

	public boolean isFailure() {
		return this.result == Result.FAILURE;
	}

	public Result failure(String info){
		this.result = Result.FAILURE;
		this.info = info;
		return this;
	}

	public static Result newSuccess(){
		Result result = new Result();
		result.setResult(Result.SUCCESS);
		return result;
	}

	public static Result newFailure(String info){
		Result result = new Result();
		result.setResult(Result.FAILURE);
		result.setInfo(info);
		return result;
	}
}
