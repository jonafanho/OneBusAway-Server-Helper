package org.transport.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record ListResult<T>(List<T> list, int pageNumber, int pageSize, int totalPages, long totalElements) {

	public static <T> DataResponse fromPage(long requestStartMillis, Page<T> page) {
		return DataResponse.create(requestStartMillis, new ListResult<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalPages(), page.getTotalElements()));
	}

	public static <T> DataResponse fromList(long requestStartMillis, List<T> list) {
		return DataResponse.create(requestStartMillis, new ListResult<>(list, 0, list.size(), 1, list.size()));
	}
}
