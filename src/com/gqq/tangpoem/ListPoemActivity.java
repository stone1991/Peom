package com.gqq.tangpoem;

import java.util.*;

import android.app.*;
import android.content.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class ListPoemActivity extends ListActivity {

	static final String[] POEMS = new String[] { "Apple", "Avocado", "Banana", "Blueberry",
			"Coconut", "Durian", "Guava", "Kiwifruit", "Jackfruit", "Mango", "Olive", "Pear",
			"Sugar-apple" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//

		// no more this
		// setContentView(R.layout.list_fruit);

		DataDb poemdb = new DataDb(getBaseContext(), PoemApplication.POEMDB);
		List<Poem> poems = poemdb.getAllPoems();

		setListAdapter(new PoemArrayAdapter(this, poems));

		// ListView listView = getListView();
		// listView.setTextFilterEnabled(true);
		//
		// listView.setOnItemClickListener(new OnItemClickListener() {
		// public void onItemClick(AdapterView<?> parent, View view, int
		// position, long id) {
		// // When clicked, show a toast with the TextView text
		// Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
		// Toast.LENGTH_SHORT).show();
		// }
		// });

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// super.onListItemClick(l, v, position, id);
		Log.d("Fanshe", "l" + l.getClass().getName());
		Log.d("Fanshe", "v" + v.getClass().getName());
		Poem poem = (Poem) getListAdapter().getItem(position);
		// Toast.makeText(this, poem.getTitle() + poem.getId(),
		// Toast.LENGTH_SHORT).show();
		Intent intent = new Intent();
		// 通过Intent对象返回结果，调用setResult方法
		intent.putExtra("selectedPoemId", poem.getId());
		setResult(MainActivity.LIST_POEM_ACTIVITY, intent);
		ListPoemActivity.this.finish();
	}
}
