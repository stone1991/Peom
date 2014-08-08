package com.gqq.tangpoem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MsgActivity extends Activity {

	class MsgViewDetector extends SimpleOnGestureListener {

		String name;

		public MsgViewDetector() {
			this("");
		}

		public MsgViewDetector(String name) {
			this.name = name;
		}

		/**
		 * 这里要注意，如果使用了onSingleTapUp，在执行onDoubleTap的时候，也会执行这个函数。
		 * 所以应该使用onSingleTapConfirmed
		 */
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// T.showLong(MsgActivity.this, "single tap");
			// Log.d(MainActivity.TAG_PRESS, "onSingleTapConfirmed tap up");
			if (name.equals("parent") && mod == Mod.MODIFY) {
				edtMsg.clearFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(edtMsg.getWindowToken(), 0);
			}
			if (name.equals("")
					|| (name.equals("parent") && mod == Mod.DISPLAY))
				MsgActivity.this.finish();
			return false;
		}

		/**
		 * 双击，修改文档，单击，关闭Activity
		 */
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// T.showLong(MsgActivity.this, "double tap");
			// detector.setIsLongpressEnabled(false);
			edtMsg.setText(comment);
			setModdifyMode();
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return super.onScroll(e1, e2, distanceX, distanceY);
			// return true;
		}
	}

	enum Mod {
		DISPLAY, MODIFY
	}

	private int cid;
	private Poem poem;
	private TextView txtMsg;
	private EditText edtMsg;
	private String comment;
	private Button btnSubmit;
	private Button btnCancel;
	private LinearLayout llyFooter;
	private GestureDetector detector;
	private GestureDetector detector2;
	// private ScrollView scrollView;
	private RelativeLayout lly;

	private Mod mod;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_msg);
		Intent intent = getIntent();
		cid = intent.getIntExtra("currentId", 0);
		txtMsg = (TextView) findViewById(R.id.txtMsg);
		// scrollView = (ScrollView) findViewById(R.id.scrollView);
		lly = (RelativeLayout) findViewById(R.id.lly);
		edtMsg = (EditText) findViewById(R.id.edtMsg);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		llyFooter = (LinearLayout) findViewById(R.id.footer);
		llyFooter.setVisibility(View.GONE);
		comment = "";
		detector = new GestureDetector(this, new MsgViewDetector());
		detector2 = new GestureDetector(this, new MsgViewDetector("parent"));
		mod = Mod.DISPLAY;

		init();

	}

	private void init() {
		if (cid > 0) {
			DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
			poem = poemdb.getPoem(cid);
			comment = poem.getComment();
			txtMsg.setText(comment);
		}

		DisplayMetrics dm = getResources().getDisplayMetrics();
		int h_screen = dm.heightPixels;
		lly.getLayoutParams().height = h_screen - 50;
		lly.requestLayout();

		txtMsg.setOnTouchListener(new View.OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Log.d(MainActivity.TAG_PRESS, "onTouch");
				detector.onTouchEvent(event);
				detector.setIsLongpressEnabled(true);
				return true;
			}
		});

		lly.setOnTouchListener(new View.OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Log.d(MainActivity.TAG_PRESS, "onTouch");
				detector2.onTouchEvent(event);
				return true;
			}
		});

		// txtMsg.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// MsgActivity.this.finish();
		// }
		// });

		// 取消按钮，回到视图界面
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// if (!imm.isActive())
				// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

				imm.hideSoftInputFromWindow(edtMsg.getWindowToken(), 0);
				setDisplayMode();
			}
		});

		// 提交按钮，保存注释，回到视图界面
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				// if (imm.isActive()) {
				// T.showLong(MsgActivity.this, "active");
				// imm.toggleSoftInput(0,
				// InputMethodManager.RESULT_UNCHANGED_HIDDEN);
				// }

				imm.hideSoftInputFromWindow(edtMsg.getWindowToken(), 0);
				DataDb db = new DataDb(getBaseContext(), PoemApplication.POEMDB);
				comment = edtMsg.getText().toString();
				db.updatePoemComment(cid, comment);
				txtMsg.setText(comment);
				setDisplayMode();
			}

		});
	}

	/**
	 * 修改界面的展示方式
	 */
	private void setModdifyMode() {
		txtMsg.setVisibility(View.GONE);
		edtMsg.setVisibility(View.VISIBLE);
		llyFooter.setVisibility(View.VISIBLE);
		mod = Mod.MODIFY;
	}

	/**
	 * 视图界面的展示方式
	 */
	private void setDisplayMode() {
		// edtMsg.clearFocus();
		txtMsg.setVisibility(View.VISIBLE);
		edtMsg.setVisibility(View.GONE);
		llyFooter.setVisibility(View.GONE);
		mod = Mod.DISPLAY;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.msg, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
