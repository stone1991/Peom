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

	public void openDB() {
		if (null != db)
			SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
	}

	public int[] getMaxMinId() {
		String sql = "select max(id) as max, min(id) as min from tangsong";
		int[] ids = { 0, 0 };
		Cursor c = db.rawQuery(sql, null);
		if (c.moveToNext()) {
			ids[0] = c.getInt(c.getColumnIndex("max"));
			ids[1] = c.getInt(c.getColumnIndex("min"));
		}
		// db.close();
		return ids;
	}

	public Poem getPoem(int id) {
		int[] ids = getMaxMinId();
		// 物极必反，如果到了最大的id，则回到最小的
		if (id >= ids[0])
			id = ids[0];
		else if (id <= ids[1]) {
			id = ids[1];
		}

		try {
			Cursor c = db.rawQuery("SELECT * from " + DATA_TABLE_NAME + " where id=" + id, null);
			return genPoem(c);
		} catch (Exception e) {
			e.printStackTrace();
			db.close();
			return null;
		}
	}

	public Poem getNextPoem(int id) {
		int[] ids = getMaxMinId();
		// 物极必反，如果到了最大的id，则回到最小的
		if (id >= ids[0])
			return getPoem(ids[1]);

		try {
			Cursor c = db.rawQuery("SELECT * from " + DATA_TABLE_NAME + " where id>" + id
					+ " order by id limit 1;", null);
			return genPoem(c);
		} catch (Exception e) {
			e.printStackTrace();
			db.close();
			return null;
		}
	}

	public Poem getPrePoem(int id) {
		int[] ids = getMaxMinId();
		if (id <= ids[1])
			return getPoem(ids[0]);

		try {
			Cursor c = db.rawQuery("SELECT * from " + DATA_TABLE_NAME + " where id<" + id
					+ " order by id desc limit 1;", null);
			return genPoem(c);

		} catch (Exception e) {
			e.printStackTrace();
			db.close();
			return null;
		}
	}

	private Poem genPoem(Cursor c) {
		Poem shiCi = null;
		if (c.moveToNext()) {
			int newId = c.getInt(c.getColumnIndex("id"));
			int type = c.getInt(c.getColumnIndex("type"));
			PoemType pType = 0 == type ? PoemType.Shi : PoemType.Ci;
			String author = c.getString(c.getColumnIndex("author"));
			String cipai = c.getString(c.getColumnIndex("cipai"));
			String title = c.getString(c.getColumnIndex("title"));
			String content = c.getString(c.getColumnIndex("content"));
			shiCi = new Poem(newId, pType, author, cipai, title, content);
		}
		db.close();
		return shiCi;
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
		db.close();
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
