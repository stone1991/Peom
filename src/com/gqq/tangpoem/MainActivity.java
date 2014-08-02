package com.gqq.tangpoem;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.os.*;
import android.text.method.*;
import android.util.*;
import android.view.*;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.widget.*;

public class MainActivity extends Activity implements OnGestureListener, OnTouchListener {

	private TextView tvContent;
	private TextView tvTitle;

	private static final String TAG_PRESS = "TAG_PRESS";
	public static final String DATABASE_TAG = "DataBase";
	public static final String FILE_TAG = "File";
	public static final String RETURN_TAG = "RETURN";
	private static final String SYSTEM = "system";
	private static final int FLING_MIN_DISTANCE = 100;
	private static final int FLING_MIN_VELOCITY = 200;
	public static final int LIST_POEM_ACTIVITY = 3;

	// private enum FlingDirection {
	// Left, None, Right
	// };

	String str1;
	String str2;

	int w_screen;
	int h_screen;

	float down_y = 0;
	float up_y = 0;
	private GestureDetector detector;

	// private ArrayList<Poem> poems;
	// private int pointer;
	// private int maxId;
	// private int minId;
	private int cId;
	private Poem cPoem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(SYSTEM, "oncreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);//
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println("init");
		init();

	}

	private void init() {
		// poems = new ArrayList<Poem>();

		tvContent = (TextView) findViewById(R.id.tvContent);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
		tvContent.setClickable(true);
		tvContent.setFocusable(true);

		// 注册一个GestureDetector
		detector = new GestureDetector(this, this);
		// 获得屏幕的宽度和高度
		DisplayMetrics dm = getResources().getDisplayMetrics();
		w_screen = dm.widthPixels;
		h_screen = dm.heightPixels;

		Log.d(TAG_PRESS, "width:" + w_screen + "");
		Log.d(TAG_PRESS, "height:" + h_screen + "");

		tvContent.setOnTouchListener(this);

		// getPoem();
		//
		// displayPoem();
		cId = getCurrentId();

		dispCurrPoem();
	}

	private int getCurrentId() {
		SharedPreferences appPrefs = getSharedPreferences("infos", MODE_PRIVATE);
		String strId = appPrefs.getString("currentId", "");

		Log.d(FILE_TAG, "currentId:" + strId);

		if ("".equals(strId))
			return 1;
		return Integer.parseInt(strId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		Log.d(SYSTEM, "onCreateOptionsMenu");

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		Log.d(SYSTEM, "onOptionsItemSelected");

		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		} else if (R.id.action_add == id) {
			Intent i = new Intent(this, NewPoemActivity.class);
			// startActivity(i);
			startActivityForResult(i, 1);
			return false;
		} else if (R.id.action_del == id) {

			new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("删除诗词")
					.setMessage("确定要删除这首诗吗？")
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
							if (poemdb.delPoem(cId)) {
								// poems.remove(pointer);
								// changeText(FlingDirection.None);
								dispNextPoem();
							}
						}

					}).setNegativeButton("No", null).show();
		} else if (R.id.action_list == id) {
			Intent i = new Intent(this, ListPoemActivity.class);
			// startActivity(i);
			startActivityForResult(i, LIST_POEM_ACTIVITY);
			return false;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 将触屏事件交给手势识别类处理
		return this.detector.onTouchEvent(event);
	}

	/**
	 * 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发Java代码
	 */
	@Override
	public boolean onDown(MotionEvent e) {
		Log.d(TAG_PRESS, "onDown");
		return false;
	}

	/**
	 * 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
	 * 注意和onDown()的区别，强调的是没有松开或者拖动的状态
	 */
	@Override
	public void onShowPress(MotionEvent e) {

	}

	/**
	 * 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.d(TAG_PRESS, "onSingleTapUp");

		int left = w_screen / 4;
		int right = w_screen * 3 / 4;

		int top = h_screen / 5;
		int foot = h_screen * 4 / 5;
		float x = e.getX();
		boolean isValid = (e.getY() > top && e.getY() < foot) ? true : false;
		if (left > x && isValid) {
			// changeText(FlingDirection.Left);
			dispPrePoem();
		}

		if (right < x && isValid) {
			// changeText(FlingDirection.Right);
			dispNextPoem();
		}

		return false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences appPrefs = getSharedPreferences("infos", MODE_PRIVATE);
		SharedPreferences.Editor prefsEditor = appPrefs.edit();
		prefsEditor.clear();
		prefsEditor.putString("currentId", cId + "");
		prefsEditor.commit();
		Log.d(FILE_TAG, "currentId:" + cId + "");
	}

	/**
	 * 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		// Log.d(TAG_PRESS, "onScroll");
		return false;
	}

	/**
	 * 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
	 */
	@Override
	public void onLongPress(MotionEvent e) {
		Log.d(TAG_PRESS, "onLongPress");

	}

	/**
	 * 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		Log.d(TAG_PRESS, "onFling");

		// e1：第1个ACTION_DOWN MotionEvent
		// e2：最后一个ACTION_MOVE MotionEvent
		// velocityX：X轴上的移动速度，像素/秒
		// velocityY：Y轴上的移动速度，像素/秒
		if (Math.abs(velocityX) < FLING_MIN_VELOCITY)
			return false;
		if (Math.abs(e1.getX() - e2.getX()) < FLING_MIN_DISTANCE)
			return false;

		if (velocityX < 0) {
			// 左滑动
			// changeText(FlingDirection.Left);
			dispNextPoem();
		} else if (velocityX > 0) {
			// 右滑动
			// changeText(FlingDirection.Right);
			dispPrePoem();
		}
		return false;
	}

	/**
	 * onTouch方法则是实现了OnTouchListener中的抽象方法，我们只要在这里添加逻辑代码即
	 * 可在用户触摸屏幕时做出响应，就像我们这里所做的——打出一个提示信息
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// return false;
		// OnGestureListener will analyzes the given motion event
		// 这里的mGestureDetector是该Activity的一个属性.在构造方法中实例化或在oncreate()方法中实例化.
//		Log.d(TAG_PRESS, "on touch");
		return detector.onTouchEvent(event);
	}

	private void dispCurrPoem(int id) {
		DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
		Log.d(DATABASE_TAG, "dispCurrPoem cid is:" + cId);
		cPoem = poemdb.getPoem(id);
		poemdb.closeDB();
		displayPoem();
	}

	private void dispCurrPoem() {
		DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
		Log.d(DATABASE_TAG, "dispCurrPoem cid is:" + cId);
		cPoem = poemdb.getPoem(cId);
		poemdb.closeDB();
		displayPoem();
	}

	private void dispNextPoem() {
		DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
		Log.d(DATABASE_TAG, "dispNextPoem cid is:" + cId);

		cPoem = poemdb.getNextPoem(cId);
		poemdb.closeDB();
		displayPoem();
	}

	private void dispPrePoem() {

		DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
		Log.d(DATABASE_TAG, "dispPrePoem cid is:" + cId);
		cPoem = poemdb.getPrePoem(cId);
		poemdb.closeDB();
		displayPoem();
	}

	/**
	 * 显示所有诗词
	 */
	private void displayPoem() {
		Poem p = cPoem;
		if (null == p) {
			Log.d(DATABASE_TAG, "取得的诗词为null，请检查代码");
			return;
		}
		tvContent.setScrollY(0);
		String title = p.getType().equals(PoemType.Shi) ? p.getTitle() : p.getCipai();
		cId = p.getId();
		Log.d(DATABASE_TAG, "cid is:" + cId);
		tvTitle.setText(title + "·" + p.getAuthor());
		tvContent.setText(p.getContent());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Log.d(RETURN_TAG, requestCode + "");
		if (2 == resultCode) {
			// 重新读取数据
			dispCurrPoem();
			T.showShort(this, "重新加载完成！");
		} else if (LIST_POEM_ACTIVITY == resultCode) {
			dispCurrPoem(data.getIntExtra("selectedPoemId", 1));
		}
	}

}
