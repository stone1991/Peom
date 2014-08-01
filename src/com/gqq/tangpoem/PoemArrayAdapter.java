package com.gqq.tangpoem;

import java.util.*;

import android.content.*;
import android.view.*;
import android.widget.*;

public class PoemArrayAdapter extends ArrayAdapter<Poem> {

	private final Context context;
	private final List<Poem> poems;
	private LayoutInflater mInflater;

	public PoemArrayAdapter(Context context, List<Poem> poems) {
		super(context, R.layout.list_poem, poems);
		this.context = context;
		this.poems = poems;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// return super.getView(position, convertView, parent);
		View rowView;
		if (convertView == null) {
			rowView = mInflater.inflate(R.layout.list_poem, parent, false);
		} else {
			rowView = convertView;
		}

		TextView txtAuthor = (TextView) rowView.findViewById(R.id.txtAuthor);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txtTitle);
		// TextView hidId = (TextView) rowView.findViewById(R.id.hidId);

		ImageView imgLogo = (ImageView) rowView.findViewById(R.id.imgLogo);

		Poem poem = poems.get(position);
		txtAuthor.setText(poem.getAuthor());
		txtTitle.setText(poem.getTitle());
		// hidId.setText(poem.getId());

		PoemType pType = poem.getType();

		if (pType == PoemType.Shi)
			imgLogo.setImageResource(R.drawable.shi);
		else {
			imgLogo.setImageResource(R.drawable.ci);
		}

		return rowView;
	}
}
