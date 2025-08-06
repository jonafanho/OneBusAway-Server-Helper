package org.transport.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record ListResult<T>(List<T> list, int pageNumber, int pageSize, int totalPages, long totalElements) {

	public static <T> DataResponse fromPage(Page<T> page) {
		return DataResponse.create(new ListResult<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalPages(), page.getTotalElements()));
	}
}
