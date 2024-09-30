package org.obaserverhelper.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Getter
@NoArgsConstructor(force = true)
public final class Response<T extends AbstractData> {

	private int version;
	private int code;
	private String text;
	private long currentTime;
	@NonNull
	private final T data;

	public Response(@NonNull T data) {
		this.data = data;
	}

	public void clearReferences() {
		this.data.references.clear();
	}

	public void merge(Response<T> response) {
		data.merge(response.data);
	}
}
