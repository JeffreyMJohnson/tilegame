package net.jeffreymjohnson.tilegame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChooseImageActivity extends Activity implements OnItemClickListener {
//	private static final String TAG = "net.jeffreymjohnson.tilegame.ChooseImageActivity";
	protected static final String SHARED_PREFERENCES = "tileGame";
	protected final static String CHOSEN_IMAGE_KEY = "net.jeffreymjohnson.tilegame.chosen_image_ID";
	protected final static String IS_NEW_GAME_KEY = "net.jeffreymjohnson.tilegame.isNewGame";
	protected final static String END_APP_KEY = "net.jeffreymjohnson.tilegame.endApp";
	private boolean endApp;

	@Override
	protected void onResume() {
		super.onResume();
		setContentView(R.layout.activity_choose_image);
		ArrayAdapter<View> adapter = new ImageViewFromArrayAdapter(getApplicationContext(), 0);
		ListView listView = (ListView) findViewById(R.id.choose_image_listview);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.choose_image, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(this, PlayGameActivity.class);
		// set imageResource field in sharedPreferences and remove saved
		// tilePosition array if there.
		SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
				.edit();
		editor.putInt(PlayGameActivity.IMAGE_RESOURCE_KEY, view.getId())
				.remove(PlayGameActivity.TILES_ARRAY_KEY)
				.commit();
		
		// get result bundle with endApp boolean set
		startActivityForResult(intent, 0);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			endApp = data.getBooleanExtra(END_APP_KEY, false);
			//if returned boolean is true then kill this activity and therefore the app
			//else go to onResume.
			if (endApp)
				finish();
		}
	}

}
