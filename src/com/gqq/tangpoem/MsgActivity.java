package com.gqq.tangpoem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MsgActivity extends Activity {

	class MsgViewDetector extends SimpleOnGestureListener {

		// @Override
		// public void onLongPress(MotionEvent e) {
		// // TODO Auto-generated method stub
		// // T.showLong(MsgActivity.this, "long press");
		// Log.d(MainActivity.TAG_PRESS, "long press");
		// edtMsg.setText(comment);
		// displayModMode();
		// return;
		// }

		/**
		 * 这里要注意，如果使用了onSingleTapUp，在执行onDoubleTap的时候，也会执行这个函数。
		 * 所以应该使用onSingleTapConfirmed
		 */
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// T.showLong(MsgActivity.this, "single tap");
			Log.d(MainActivity.TAG_PRESS, "onSingleTapConfirmed tap up");
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
			displayModMode();
			return true;
		}
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_msg);
		Intent intent = getIntent();
		cid = intent.getIntExtra("currentId", 0);
		txtMsg = (TextView) findViewById(R.id.txtMsg);
		edtMsg = (EditText) findViewById(R.id.edtMsg);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		llyFooter = (LinearLayout) findViewById(R.id.footer);
		llyFooter.setVisibility(View.GONE);
		comment = "";
		detector = new GestureDetector(this, new MsgViewDetector());

		init();

	}

	private void init() {
		if (cid > 0) {
			DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
			poem = poemdb.getPoem(cid);
			comment = poem.getComment();
			txtMsg.setText(comment);
			// txtMsg.setLongClickable(longClickable);
			// 长摁视图界面，切换到修改界面
			// txtMsg.setOnLongClickListener(new View.OnLongClickListener() {
			//
			// @Override
			// public boolean onLongClick(View v) {
			// // T.showLong(MsgActivity.this, "just a test");
			// edtMsg.setText(comment);
			// displayModMode();
			// return false;
			// }
			// });
		}

		txtMsg.setOnTouchListener(new View.OnTouchListener() {

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Log.d(MainActivity.TAG_PRESS, "onTouch");
				detector.onTouchEvent(event);
				detector.setIsLongpressEnabled(true);
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
				displayViewMode();
			}
		});

		// 提交按钮，保存注释，回到视图界面
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DataDb db = new DataDb(getBaseContext(), PoemApplication.POEMDB);
				comment = edtMsg.getText().toString();
				db.updatePoemComment(cid, comment);
				txtMsg.setText(comment);
				displayViewMode();
			}

		});
	}

	/**
	 * 修改界面的展示方式
	 */
	private void displayModMode() {
		txtMsg.setVisibility(View.GONE);
		edtMsg.setVisibility(View.VISIBLE);
		llyFooter.setVisibility(View.VISIBLE);
	}

	/**
	 * 视图界面的展示方式
	 */
	private void displayViewMode() {
		txtMsg.setVisibility(View.VISIBLE);
		edtMsg.setVisibility(View.GONE);
		llyFooter.setVisibility(View.GONE);
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
