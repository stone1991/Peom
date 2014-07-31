package com.gqq.tangpoem;

import java.util.*;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.util.*;

public class DataDb {
	public static final String DATADB_NAME = "tangshi.db";
	private static final String DATA_TABLE_NAME = "tangsong";
	private SQLiteDatabase db;
	private String path;

	public DataDb(Context context, String path) {
		this.path = path;
		db = context.openOrCreateDatabase(path, Context.MODE_PRIVATE, null);
	}

	public void closeDB() {
		if (null != db)
			db.close();
	}

	public List<Poem> getAllPoems() {
		List<Poem> list = new ArrayList<Poem>();
		Cursor c = db.rawQuery("SELECT * from " + DATA_TABLE_NAME, null);
		while (c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex("id"));
			int type = c.getInt(c.getColumnIndex("type"));
			PoemType pType = 0 == type ? PoemType.Shi : PoemType.Ci;
			String author = c.getString(c.getColumnIndex("author"));
			String cipai = c.getString(c.getColumnIndex("cipai"));
			String title = c.getString(c.getColumnIndex("title"));
			String content = c.getString(c.getColumnIndex("content"));
			Poem shiCi = new Poem(id, pType, author, cipai, title, content);
			list.add(shiCi);
		}
		return list;
	}

	/**
	 * 插入一首新诗
	 * 
	 * @param type
	 * @param author
	 * @param title
	 * @param cipai
	 * @param content
	 * @return
	 */
	public boolean insertPoem(int type, String author, String title, String cipai, String content) {
		String sql = "insert into " + DATA_TABLE_NAME
				+ " (type, author,cipai,title, content) values (" + type + ", '" + author + "',";
		sql += "'" + cipai + "',";
		sql += "'" + title + "',";
		sql += "'" + content + "');";
		Log.d("Sql", sql);
		try {
			db.execSQL(sql);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			e.printStackTrace();
			return false;
		}

	}

	public boolean delPoem(int id) {
		String sql = "delete from " + DATA_TABLE_NAME + " where id = " + id;
		Log.d("Sql", sql);
		try {
			db.execSQL(sql);
			db.close();
			return true;
		} catch (Exception e) {
			db.close();
			e.printStackTrace();
			return false;
		}

	}

}
