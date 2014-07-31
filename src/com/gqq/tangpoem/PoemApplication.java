package com.gqq.tangpoem;

import java.io.*;
import java.io.ObjectOutputStream.*;

import android.app.*;
import android.os.*;
import android.util.*;

public class PoemApplication extends Application {

	private static final String FileTag = "PoemApplicationFileSystem";
	public static final String POEMDB = "tangshi.db";

	@Override
	public void onCreate() {

		initDB();
	}

	private void initDB() {
		String pathdir = "/data" + Environment.getDataDirectory().getAbsolutePath()
				+ File.separator + "com.gqq.tangpoem" + File.separator + "databases"
				+ File.separator;
		// File dir = new File(pathdir);

		// if (!dir.exists())
		// dir.mkdir();

		String path = pathdir + POEMDB;
		Log.i(FileTag, path);
		File db = new File(path);

		if (!db.exists()) {
			// if (db.exists())
			// db.delete();
			Log.i(FileTag, "file is not exsits");
			try {
				db.getParentFile().mkdirs();
				db.createNewFile();
				InputStream is = getAssets().open(POEMDB);
				FileOutputStream fos = new FileOutputStream(db, false);
				int len = -1;
				byte[] buffer = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					fos.flush();
				}
				fos.close();
				is.close();
				Log.i(FileTag, "city.db已经被成功拷贝。地址为：" + path);
			} catch (Exception e) {
				Log.i(FileTag, e.getMessage());
				e.printStackTrace();
				System.exit(0);
			}

		} else {
			Log.i(FileTag, "file has already exsited");
		}
	}
}
