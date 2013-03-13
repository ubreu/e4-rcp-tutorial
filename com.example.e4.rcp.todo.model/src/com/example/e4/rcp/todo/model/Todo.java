package com.example.e4.rcp.todo.model;

import java.util.Date;

public class Todo {

	public static final String FIELD_ID = "id";
	public static final String FIELD_SUMMARY = "summary";
	public static final String FIELD_DESCRIPTION = "description";
	public static final String FIELD_DONE = "done";
	public static final String FIELD_DUEDATE = "duedate";

	private String description = "";
	private boolean done;
	private Date dueDate;
	private long id;
	private String summary = "";

	public Todo() {
	}

	public Todo(long id, String summary, String description, boolean done,
			Date dueDate) {
		this();
		this.id = id;
		this.summary = summary;
		this.description = description;
		this.done = done;
		this.dueDate = dueDate;
	}

	public Todo copy() {
		return new Todo(this.id, this.summary, this.description, this.done,
				this.dueDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Todo other = (Todo) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getDescription() {
		return description;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public long getId() {
		return id;
	}

	public String getSummary() {
		return summary;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	public boolean isDone() {
		return done;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	@Override
	public String toString() {
		return "Todo [id=" + id + ", summary=" + summary + ", description="
				+ description + ", done=" + done + ", dueDate=" + dueDate + "]";
	}
}
