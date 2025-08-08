package org.transport.response;

import java.util.List;

public record ListResult<T>(List<T> list, int pageNumber, int pageSize, int totalPages, long totalElements) {

	public static <T> DataResponse fromList(long requestStartMillis, List<T> list) {
		return DataResponse.create(requestStartMillis, new ListResult<>(list, 0, list.size(), 1, list.size()));
	}
}
