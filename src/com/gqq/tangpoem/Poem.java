package com.gqq.tangpoem;

import android.R.string;

public class Poem {
	private int id;
	private String author;
	private String cipai;
	private String title;
	private String comment;

	public Poem(int id, PoemType type, String author, String cipai,
			String titile, String content, String comment) {
		this.id = id;
		this.type = type;
		this.cipai = cipai;
		this.title = titile;
		this.author = author;
		this.content = content;
		this.setComment(comment);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getCipai() {
		return cipai;
	}

	public void setCipai(String cipai) {
		this.cipai = cipai;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public PoemType getType() {
		return type;
	}

	public void setType(PoemType type) {
		this.type = type;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	private String content;
	private PoemType type;
}
