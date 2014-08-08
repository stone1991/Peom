package com.gqq.tangpoem;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnGestureListener,
		OnTouchListener {

	class DoubuleTapClass implements OnDoubleTapListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.d(TAG_PRESS, "onSingleTapConfirmed");

			int left = w_screen / 4;
			int right = w_screen * 3 / 4;

			int top = h_screen / 5;
			int foot = h_screen * 4 / 5;
			float x = e.getX();
			boolean isValid = (e.getY() > top && e.getY() < foot) ? true
					: false;
			if (left > x && isValid) {
				// changeText(FlingDirection.Left);
				dispPrePoem();
			}

			if (right < x && isValid) {
				// changeText(FlingDirection.Right);
				dispNextPoem();
			}

			if (x > left && x < right && isValid) {
				showmenu();
			}

			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.d(TAG_PRESS, "onDoubleTap");
			modifyPoem();
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

	}

	private TextView tvContent;
	private TextView tvTitle;

	public static final String TAG_PRESS = "TAG_PRESS";
	public static final String DATABASE_TAG = "DataBase";
	public static final String FILE_TAG = "File";
	public static final String RETURN_TAG = "RETURN";
	private static final String SYSTEM = "system";
	private static final int FLING_MIN_DISTANCE = 200;
	private static final int FLING_MAX_DISTANCE = 300;
	private static final int FLING_MIN_VELOCITY = 200;
	public static final int LIST_POEM_ACTIVITY = 3;
	public static final int LIST_POEM_RESULT = 13;
	public static final int INSERT_POEM_SUCCESS = 2;
	public static final int POEM_MODIFY = 4;
	public static final int POEM_ADD_MSG = 5;

	private int w_screen;
	private int h_screen;

	private GestureDetector detector;

	private int cId;
	private Poem cPoem;

	private AlertDialog menuDialog;// menu菜单Dialog
	private GridView menuGrid;
	private View menuView;

	private final int ITEM_ADD = 0;// 添加
	private final int ITEM_DELETE = 1;// 删除
	private final int ITEM_CONTENT = 2;// 目录
	private final int ITEM_MODIFY = 3;// 修改

	/** 菜单图片 **/
	int[] menu_image_array = { android.R.drawable.ic_menu_add,
			android.R.drawable.ic_menu_delete,
			android.R.drawable.ic_menu_info_details,
			android.R.drawable.ic_menu_edit };
	/** 菜单文字 **/
	String[] menu_name_array = { "添加", "删除", "目录", "修改" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(SYSTEM, "oncreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);//
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		System.out.println("init");
		init();

		initmenu();
	}

	/**
	 * 初始化Menu菜单
	 */
	private void initmenu() {
		menuView = View.inflate(this, R.layout.gridview_menu, null);
		// 创建AlertDialog
		menuDialog = new AlertDialog.Builder(this).create();
		menuDialog.setView(menuView);
		menuDialog.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU)// 监听按键
					dialog.dismiss();
				return false;
			}
		});
		menuDialog.setCanceledOnTouchOutside(true);

		menuGrid = (GridView) menuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(getMenuAdapter(menu_name_array, menu_image_array));
		// menuGrid.setBackgroundColor(255);
		/** 监听menu选项 **/
		menuGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case ITEM_ADD:// 添加
					T.showLong(MainActivity.this, "add");
					Intent i = new Intent(MainActivity.this,
							NewPoemActivity.class);
					// startActivity(i);
					startActivityForResult(i, 1);
					break;
				case ITEM_DELETE:// 文件管理
					new AlertDialog.Builder(MainActivity.this)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setTitle("删除诗词")
							.setMessage("确定要删除这首诗吗？")
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											DataDb poemdb = new DataDb(
													getBaseContext(),
													PoemApplication.POEMDB);
											if (poemdb.delPoem(cId)) {
												// poems.remove(pointer);
												// changeText(FlingDirection.None);
												dispNextPoem();
											}
										}

									}).setNegativeButton("No", null).show();
					break;
				case ITEM_CONTENT:// 目录
					Intent i2 = new Intent(MainActivity.this,
							ListPoemActivity.class);
					// startActivity(i);
					startActivityForResult(i2, LIST_POEM_ACTIVITY);
					break;
				case ITEM_MODIFY:// 修改
					// POEM_MODIFY
					modifyPoem();
					break;
				}
				menuDialog.dismiss();
			}
		});
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {

		showmenu();
		return false;
		// 返回为true 则显示系统menu
	}

	private void showmenu() {
		if (menuDialog == null) {
			menuDialog = new AlertDialog.Builder(this).setView(menuView).show();
		} else {
			menuDialog.show();
			Window dialogWindow = menuDialog.getWindow();
			dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER);
			WindowManager.LayoutParams lp = dialogWindow.getAttributes();
			lp.alpha = 0.7f;
			// lp.height = 500;
			lp.width = w_screen;
			lp.x -= 180;
			dialogWindow.setAttributes(lp);
		}
		// return false;
	}

	private SimpleAdapter getMenuAdapter(String[] menuNameArray,
			int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < menuNameArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			map.put("itemText", menuNameArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.item_menu, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		return simperAdapter;
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
		detector.setOnDoubleTapListener(new DoubuleTapClass());
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

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
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
		setIconEnable(menu, true); // 调用这句实现显示ICON
		menu.add("menu");// 必须创建一项
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效
	private void setIconEnable(Menu menu, boolean enable) {
		try {
			Class<?> clazz = Class
					.forName("com.android.internal.view.menu.MenuBuilder");
			Method m = clazz.getDeclaredMethod("setOptionalIconsVisible",
					boolean.class);
			m.setAccessible(true);

			// MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
			m.invoke(menu, enable);

		} catch (Exception e) {
			e.printStackTrace();
		}
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
		} else if (R.id.action_mod == id) {
			// POEM_MODIFY
			modifyPoem();
			return false;

		} else if (R.id.action_add == id) {
			Intent i = new Intent(this, NewPoemActivity.class);
			// startActivity(i);
			startActivityForResult(i, 1);
			return false;
		} else if (R.id.action_del == id) {

			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("删除诗词")
					.setMessage("确定要删除这首诗吗？")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									DataDb poemdb = new DataDb(
											getBaseContext(),
											PoemApplication.POEMDB);
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

	private void modifyPoem() {
		Intent i = new Intent(this, NewPoemActivity.class);
		i.putExtra("ismodify", true);
		i.putExtra("currentId", cId);
		// startActivity(i);
		startActivityForResult(i, POEM_MODIFY);
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
		// Log.d(TAG_PRESS, "onDown");
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

		return false;
	}

	/**
	 * 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// Log.d(TAG_PRESS, "onScroll");
		return false;
	}

	/**
	 * 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
	 */
	@Override
	public void onLongPress(MotionEvent e) {
		Log.d(TAG_PRESS, "onLongPress");
		// POEM_MODIFY
		Intent i = new Intent(this, MsgActivity.class);
		i.putExtra("currentId", cId);
		// startActivity(i);
		startActivityForResult(i, POEM_ADD_MSG);
	}

	/**
	 * 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// Log.d(TAG_PRESS, "onFling");

		// e1：第1个ACTION_DOWN MotionEvent
		// e2：最后一个ACTION_MOVE MotionEvent
		// velocityX：X轴上的移动速度，像素/秒
		// velocityY：Y轴上的移动速度，像素/秒
		if (Math.abs(velocityX) < FLING_MIN_VELOCITY)
			return false;
		if (Math.abs(e1.getX() - e2.getX()) < FLING_MIN_DISTANCE)
			return false;
		if (Math.abs(e1.getY() - e2.getY()) > FLING_MAX_DISTANCE)
			return false;

		if (velocityX < 0) {
			// 左滑动
			dispNextPoem();
		} else if (velocityX > 0) {
			// 右滑动
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
		// Log.d(TAG_PRESS, "on touch");
		return detector.onTouchEvent(event);
	}

	private void dispCurrPoem(int id) {
		DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
		Log.d(DATABASE_TAG, "dispCurrPoem current poem id is:" + cId);
		cPoem = poemdb.getPoem(id);
		poemdb.closeDB();
		displayPoem();
	}

	private void dispCurrPoem() {
		DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
		Log.d(DATABASE_TAG, "dispCurrPoem current poem id is:" + cId);
		cPoem = poemdb.getPoem(cId);
		poemdb.closeDB();
		displayPoem();
	}

	private void dispNextPoem() {
		DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
		Log.d(DATABASE_TAG, "dispNextPoem current poem id is:" + cId);

		cPoem = poemdb.getNextPoem(cId);
		poemdb.closeDB();
		displayPoem();
	}

	private void dispPrePoem() {

		DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
		Log.d(DATABASE_TAG, "dispPrePoem current poem id is:" + cId);
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
		String title = p.getType().equals(PoemType.Shi) ? p.getTitle() : p
				.getCipai();
		cId = p.getId();
		Log.d(DATABASE_TAG, "the displayed current poem id is:" + cId);
		tvTitle.setText(title + "·" + p.getAuthor());
		tvContent.setText(p.getContent());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d(RETURN_TAG, requestCode + "");
		if (INSERT_POEM_SUCCESS == resultCode) {
			// 重新读取数据
			dispCurrPoem(data.getIntExtra("maxId", cId));
			T.showShort(this, "重新加载完成！");
		} else if (LIST_POEM_ACTIVITY == requestCode
				&& resultCode == LIST_POEM_RESULT) {
			dispCurrPoem(data.getIntExtra("selectedPoemId", 1));
		} else if (POEM_MODIFY == requestCode) {
			dispCurrPoem();
		}
	}

}
