package org.thoughts.on.java.model;

public class BookPublisherValue {

	private String publisher;
	private String title;

	public BookPublisherValue(String title, String publisher) {
		this.title = title;
		this.publisher = publisher;
	}
	
	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
