package com.gqq.tangpoem;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class NewPoemActivity extends Activity implements OnClickListener {

	private Button btnSubmit;
	private Button btnCancel;
	private RadioButton rdoTangshi;
	private RadioButton rdoSongci;
	EditText edtContent;
	EditText edtCipai;
	EditText edtTitle;
	EditText edtAuthor;

	private int type;
	private String title, author, cipai, content;
	private boolean ismod;
	private int cid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//
		// http://blog.csdn.net/stonesharp/article/details/7648384
		// 解决盖住了输入法的问题
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.activity_new_poem);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		edtContent = (EditText) findViewById(R.id.edtContent);
		edtTitle = (EditText) findViewById(R.id.edtTitle);
		edtCipai = (EditText) findViewById(R.id.edtCipai);
		edtAuthor = (EditText) findViewById(R.id.edtAuthor);
		btnSubmit.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		rdoTangshi = (RadioButton) findViewById(R.id.rdoTangshi);
		rdoSongci = (RadioButton) findViewById(R.id.rdoSongci);
		initPoem();
	}

	private void initPoem() {
		Intent intent = getIntent();
		ismod = intent.getBooleanExtra("ismodify", false);
		Log.d("Intent ismodify", ismod + "");
		if (!ismod)
			return;
		cid = intent.getIntExtra("currentId", 0);
		if (0 == cid)
			return;
		DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
		Poem poem = poemdb.getPoem(cid);

		edtContent.setText(poem.getContent());
		edtTitle.setText(poem.getTitle());
		edtCipai.setText(poem.getCipai());
		edtAuthor.setText(poem.getAuthor());
		if (poem.getType() == PoemType.Shi) {
			rdoTangshi.setChecked(true);
		} else {
			rdoSongci.setChecked(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_poem, menu);
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

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnSubmit) {

			if (!doCheck())
				return;

			DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
			if (!ismod) {
				// 如果是添加诗词
				if (poemdb.insertPoem(type, author, title, cipai, content)) {
					String msg = 0 == type ? "插入新诗成功" : "插入新词成功";
					T.showLong(this, msg);

					Intent intent = new Intent();
					// 通过Intent对象返回结果，调用setResult方法
					setResult(2, intent);

					NewPoemActivity.this.finish();
				}
			} else {
				// 如果是修改诗词
				if (poemdb.updatePoem(cid, type, author, title, cipai, content)) {
					String msg = 0 == type ? "更新新诗成功" : "更新新词成功";
					T.showLong(this, msg);

					Intent intent = new Intent();
					// 通过Intent对象返回结果，调用setResult方法
					setResult(MainActivity.POEM_MODIFY, intent);

					NewPoemActivity.this.finish();
				}
			}
		} else if (v.getId() == R.id.btnCancel) {
			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage("确定不要插入吗？")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									NewPoemActivity.this.finish();
								}

							}).setNegativeButton("No", null).show();
		}
	}

	private boolean doCheck() {
		type = rdoTangshi.isChecked() ? 0 : 1;
		content = (String) edtContent.getText().toString();
		if ("".equals(content)) {
			T.showShort(this, "内容不能为空");
			return false;
		}

		title = (String) edtTitle.getText().toString();
		if ("".equals(title)) {
			T.showShort(this, "标题不能为空");
			return false;
		}

		author = (String) edtAuthor.getText().toString();
		if ("".equals(author)) {
			T.showShort(this, "作者不能为空");
			return false;
		}

		cipai = (String) edtCipai.getText().toString();
		if (type == 1 && "".equals(cipai)) {
			T.showShort(this, "词牌不能为空");
			return false;
		}
		return true;
	}
}
