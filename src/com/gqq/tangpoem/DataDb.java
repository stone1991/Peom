package com.gqq.tangpoem;

import java.lang.annotation.Retention;
import java.security.PublicKey;
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
			SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.CREATE_IF_NECESSARY);
	}

	public int[] getMaxMinId() {
		String sql = "select max(id) as max, min(id) as min from tangsong where `status`=1;";
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
			String sql = "SELECT * from " + DATA_TABLE_NAME
					+ " where `status`=1 and id=" + id;
			Log.d(MainActivity.DATABASE_TAG, sql);
			Cursor c = db.rawQuery(sql, null);
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
			String sql = "SELECT * from " + DATA_TABLE_NAME
					+ " where `status`=1 and id>" + id
					+ " order by id limit 1;";
			Log.d(MainActivity.DATABASE_TAG, sql);
			Cursor c = db.rawQuery(sql, null);
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

			String sql = "SELECT * from " + DATA_TABLE_NAME
					+ " where `status`=1 and id<" + id
					+ " order by id desc limit 1;";
			Log.d(MainActivity.DATABASE_TAG, sql);

			Cursor c = db.rawQuery(sql, null);
			return genPoem(c);

		} catch (Exception e) {
			e.printStackTrace();
			db.close();
			return null;
		}

	}

	/**
	 * 根据传入的游标生成一个Poem对象
	 * 
	 * @param c
	 *            传入的游标
	 * @return Poem对象
	 */
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
			String comment = c.getString(c.getColumnIndex("comment"));
			shiCi = new Poem(newId, pType, author, cipai, title, content,
					comment);
		}
		db.close();
		return shiCi;
	}

	/**
	 * 取得所有的诗词
	 * 
	 * @return 诗词列表
	 */
	public List<Poem> getAllPoems() {

		String sql = "SELECT * from " + DATA_TABLE_NAME
				+ " where `status` = 1;";
		return getPoemFromSql(sql);

	}

	/**
	 * 取得所有的已经删除的诗词
	 * 
	 * @return 诗词列表
	 */
	public List<Poem> getAllDeletedPoems() {

		String sql = "SELECT * from " + DATA_TABLE_NAME
				+ " where `status` = 99;";
		return getPoemFromSql(sql);
	}

	/**
	 * 取得所有的熟悉的的诗词
	 * 
	 * @return 诗词列表
	 */
	public List<Poem> getAllFamiliarPoems() {

		String sql = "SELECT * from " + DATA_TABLE_NAME
				+ " where `status` = 0;";
		return getPoemFromSql(sql);
	}

	private List<Poem> getPoemFromSql(String sql) {
		List<Poem> list = new ArrayList<Poem>();
		Cursor c = db.rawQuery(sql, null);
		while (c.moveToNext()) {
			int id = c.getInt(c.getColumnIndex("id"));
			int type = c.getInt(c.getColumnIndex("type"));
			PoemType pType = 0 == type ? PoemType.Shi : PoemType.Ci;
			String author = c.getString(c.getColumnIndex("author"));
			String cipai = c.getString(c.getColumnIndex("cipai"));
			String title = c.getString(c.getColumnIndex("title"));
			String content = c.getString(c.getColumnIndex("content"));
			String comment = c.getString(c.getColumnIndex("comment"));
			Poem shiCi = new Poem(id, pType, author, cipai, title, content,
					comment);
			list.add(shiCi);
		}
		db.close();
		return list;
	}

	/**
	 * 插入一首新诗
	 * 
	 * @param type
	 *            类型：0：诗，1：词
	 * @param author
	 *            作者
	 * @param title
	 *            题目
	 * @param cipai
	 *            词牌名
	 * @param content
	 *            诗词内容
	 * @return 是否插入成功
	 */
	public int insertPoem(int type, String author, String title,
			String cipai, String content) {
		String sql = "insert into " + DATA_TABLE_NAME
				+ " (`status`, type, author,cipai,title, comment, content) values (1, "
				+ type + ", '" + author + "',";
		sql += "'" + cipai + "',";
		sql += "'" + title + "',";
		sql += "'" + "暂时无注释！" + "',";
		sql += "'" + content + "');";
		Log.d("Sql", sql);
		try {
			db.execSQL(sql);
		} catch (Exception e) {
			db.close();
			e.printStackTrace();
			return 0;
		}

		int[] ids = getMaxMinId();

		db.close();
		return ids[0];
	}

	/**
	 * 修改一首诗词
	 * 
	 * @param type
	 *            类型：0：诗，1：词
	 * @param author
	 *            作者
	 * @param title
	 *            题目
	 * @param cipai
	 *            词牌名
	 * @param content
	 *            诗词内容
	 * @return 是否插入成功
	 */
	public boolean updatePoem(int id, int type, String author, String title,
			String cipai, String content) {
		String sql = "update " + DATA_TABLE_NAME + " set `type`=" + type
				+ ", author='" + author;
		sql += "', title='" + title;
		sql += "', cipai='" + cipai;
		sql += "', content='" + content;
		sql += "' where id=" + id;

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

	/**
	 * 更新诗词的评论
	 * 
	 * @param id
	 *            id
	 * @param cm
	 *            评论
	 * @return
	 */
	public boolean updatePoemComment(int id ,String cm) {
		String sql = "update " + DATA_TABLE_NAME + " set `comment`='" + cm;
		sql += "' where id=" + id;

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

	/**
	 * 删除一首诗词
	 * 
	 * @param id
	 *            诗词id
	 * @return
	 */
	public boolean delPoem(int id) {
		String sql = "update " + DATA_TABLE_NAME
				+ " set `status` = 99 where id = " + id;
		Log.d(MainActivity.DATABASE_TAG, sql);
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
