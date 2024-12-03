package org.transport.type;

import jakarta.annotation.Nullable;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public abstract class DateTimeBase<T extends DateTimeBase<T>> implements UserType<T> {

	protected final int a;
	protected final int b;
	protected final int c;

	protected DateTimeBase(int a, int b, int c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public final int getSqlType() {
		return Types.VARCHAR;
	}

	@Override
	public final boolean equals(DateTimeBase data1, DateTimeBase data2) {
		return data1.a == data2.a && data1.b == data2.b && data1.c == data2.c;
	}

	@Override
	public final int hashCode(DateTimeBase data) {
		return Objects.hash(data.a, data.b, data.c);
	}

	@Nullable
	@Override
	public final T nullSafeGet(ResultSet resultSet, int i, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws SQLException {
		final String result = resultSet.getString(i);
		return result == null ? null : decode(result);
	}

	@Override
	public final void nullSafeSet(PreparedStatement preparedStatement, @Nullable T data, int i, SharedSessionContractImplementor sharedSessionContractImplementor) throws SQLException {
		if (data == null) {
			preparedStatement.setNull(i, Types.VARCHAR);
		} else {
			preparedStatement.setString(i, data.toString());
		}
	}

	@Override
	public final boolean isMutable() {
		return false;
	}

	@Override
	public final Serializable disassemble(DateTimeBase data) {
		return data.toString();
	}

	@Override
	public final T assemble(Serializable serializable, Object o) {
		return decode(serializable.toString());
	}

	@Override
	public abstract String toString();

	protected abstract T decode(String data);
}
