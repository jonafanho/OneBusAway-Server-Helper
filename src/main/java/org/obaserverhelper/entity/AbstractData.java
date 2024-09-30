package org.obaserverhelper.entity;

public abstract class AbstractData {

	public final References references = new References();

	protected abstract void merge(AbstractData abstractData);
}
