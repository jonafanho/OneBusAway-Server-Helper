package org.transport.response;

import org.springframework.http.HttpStatus;

import java.util.HashMap;

public final class DataResponse {

	public final long currentTime = System.currentTimeMillis();
	public final long requestTime;
	public final int code;
	public final Object data;
	public final String text;

	private DataResponse(long requestStartMillis, int code, Object data, String text) {
		requestTime = currentTime - requestStartMillis;
		this.code = code;
		this.data = data;
		this.text = text;
	}

	public static DataResponse create(long requestStartMillis, Object data) {
		final HttpStatus status = HttpStatus.OK;
		return new DataResponse(requestStartMillis, status.value(), data, status.getReasonPhrase());
	}

	public static DataResponse createError(long requestStartMillis, Exception e) {
		final HttpStatus status = HttpStatus.BAD_REQUEST;
		return new DataResponse(requestStartMillis, status.value(), new HashMap<>(), e.getMessage());
	}
}
