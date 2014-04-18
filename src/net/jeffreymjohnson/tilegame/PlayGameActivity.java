package net.jeffreymjohnson.tilegame;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;

//TODO implement: tab view for small screen and side by side for large screens. solution image for comparison
//TODO implement: random shuffle, auto puzzle solving to verify if solvable
//TODO:implement: total moves counter in action bar
//TODO slide touch for tile switch

/**
 * @author Jeffrey M. Johnson
 * 
 */
public class PlayGameActivity extends Activity implements OnItemClickListener{
	private final int ACTIONBAR_HEIGHT = 160;
	private final int GRID_HORIZONTAL_SPACING = 2;
	private final int GRID_VERTICAL_SPACING = 2;

	protected static final int DIFFICULTY_LEVEL_EASY = 9;
	protected static final int DIFFICULTY_LEVEL_MEDIUM = 16;
	protected static final int DIFFICULTY_LEVEL_HARD = 25;

	private final long SECONDS_BEFORE_SHUFFLE = 3;
	private final int DEFAULT_DIFFICULTY_LEVEL = DIFFICULTY_LEVEL_MEDIUM;

	private static final String TAG = "net.jeffreymjohnson.tilegame.PlayGameActivity";

	protected static final String IMAGE_RESOURCE_KEY = "net.jeffreymjohnson.tilegame.chosenImageResource";
	protected static final String DIFFICULTY_LEVEL_KEY = "net.jeffreymjohnson.tilegame.mDifficultyLevel";
	protected static final String TOTAL_MOVES_KEY = "net.jeffreymjohnson.tilegame.totalMoves";
	protected static final String TILES_ARRAY_KEY = "net.jeffreymjohnson.tilesArray";

	private int mDifficultyLevel;
	private int mScreenWidth;
	private int mScreenHeight;
	private int mNumColumns;
	private Context mContext;
	private ArrayAdapter<View> mTilesAdapter;

	private int mImageResource;
	private GridView mGridView;
	private int mTotalMoves;
	private Tile[] mTiles;
	private boolean mIsOnClickAbled = false;
	private boolean mIsNewGame = false;
	
	
	/**
	 * Changes difficulty level to given level, removes any saved tile position state,
	 * then calls createTiles(), tileImage(), setupGridView() and timedShuffle() methods.
	 * @param newLevel New difficulty level to set mDifficultyLevel field to.
	 */
	protected void changeDifficultyLevel(int newLevel) {
		setDifficultyLevel(newLevel);
		getSharedPreferences(ChooseImageActivity.SHARED_PREFERENCES, MODE_PRIVATE).edit()
		.remove(TILES_ARRAY_KEY).commit();
		createTiles();
		tileImage();
		setupGridView();
		timedShuffle(SECONDS_BEFORE_SHUFFLE);
	}

	/**
	 * Sets mDifficultyLevel field as well as mNumColumns calculated from given difficultyLevel param. 
	 * This also sets the saved state of the difficultyLevel field to new value.
	 * @param difficultyLevel New difficultyLevel to set fields.
	 */
	private void setDifficultyLevel(int difficultyLevel) {
		mDifficultyLevel = difficultyLevel;
		mNumColumns = (int) Math.sqrt(mDifficultyLevel);
		getSharedPreferences(ChooseImageActivity.SHARED_PREFERENCES, MODE_PRIVATE).edit()
				.putInt(DIFFICULTY_LEVEL_KEY, mDifficultyLevel)
				.commit();
	}

	private void timedShuffle(long secondsToShuffle) {
		new CountDownTimer(secondsToShuffle * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
			}

			@Override
			public void onFinish() {
				shuffleTiles();
				mTilesAdapter.notifyDataSetChanged();
				mIsOnClickAbled = true;
			}
		}.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// clearState();
		Log.i(TAG, "onResume");
		setContentView(R.layout.play_game_action);
		/*
		 * isOnClickAbled = false; tileImage();
		 */
		mGridView = (GridView) findViewById(R.id.gridview);

		mContext = getApplicationContext();
		mScreenWidth = mContext.getResources().getDisplayMetrics().widthPixels - 10;
		mScreenHeight = mContext.getResources().getDisplayMetrics().heightPixels - ACTIONBAR_HEIGHT;
		loadState();
		setupGridView();
		createTiles();
		tileImage();
		//new game so shuffle tiles after pause
		//else just leave as they were listed in saved state
		if (mIsNewGame) {
			timedShuffle(SECONDS_BEFORE_SHUFFLE);
			mTilesAdapter.notifyDataSetChanged();
		}
		
	}

	/**
	 * Sets module variables mImageResource, mDifficultyLevel, mNumColumns, and
	 * mTotalMoves
	 */
	private void loadState() {
		SharedPreferences prefs = getSharedPreferences(ChooseImageActivity.SHARED_PREFERENCES,
				MODE_PRIVATE);
		mImageResource = prefs.getInt(IMAGE_RESOURCE_KEY, 0);
		// use setDifficulty method to set mNumColumns as well as
		// mDifficultyLevel
		setDifficultyLevel(prefs.getInt(DIFFICULTY_LEVEL_KEY, DEFAULT_DIFFICULTY_LEVEL));
		mTotalMoves = prefs.getInt(TOTAL_MOVES_KEY, 0);
		
	}

	/**
	 * Sets the properties of the GridView object
	 */
	private void setupGridView() {
		mGridView.setNumColumns(mNumColumns);
		mGridView.setColumnWidth(mScreenWidth / mNumColumns);
		mGridView.setHorizontalSpacing(GRID_HORIZONTAL_SPACING);
		mGridView.setVerticalSpacing(GRID_VERTICAL_SPACING);
		mTilesAdapter = new TilesAdapter(this, 0);
		mGridView.setAdapter(mTilesAdapter);
		
		mGridView.setOnItemClickListener(this);
		
		mGridView.setBackgroundColor(Color.BLACK);
	}
	
	

	/**
	 * fills the mTiles array with either tiles in solved positions or in
	 * positions designated by shared preferences and sets mIsNewGame field
	 * accordingly. In both cases the Tile object has a null for its image field.
	 */
	private void createTiles() {
		mTiles = new Tile[mDifficultyLevel];
		SharedPreferences prefs = getSharedPreferences(ChooseImageActivity.SHARED_PREFERENCES,
				MODE_PRIVATE);
		String savedTilesString = prefs.getString(TILES_ARRAY_KEY, "");

		// no saved state so create tiles from scratch and set isNewGame = true;
		if (savedTilesString.equals("")) {
			mIsNewGame = true;
			mTotalMoves = 0;
			int arrayIndex = 0;
			for (int row = 0; row < mNumColumns; row++) {
				for (int col = 0; col < mNumColumns; col++) {
					boolean isBlankTile;
					if (arrayIndex == mDifficultyLevel - 1) {
						isBlankTile = true;
					} else
						isBlankTile = false;
					TilePosition correctPosition = new TilePosition(col, row);
					mTiles[arrayIndex] = new Tile(null, correctPosition, correctPosition,
							isBlankTile);
					arrayIndex++;
				}
			}
		} else {// tile positions saved in state, create from savedTileString
			mIsNewGame = false;
			Gson gson = new Gson();
			Tile[] savedTiles = new Tile[mDifficultyLevel];
			savedTiles = gson.fromJson(savedTilesString, Tile[].class);
			for (int i = 0; i < savedTiles.length; i++) {
				Tile newTile = new Tile(null, savedTiles[i].currentPosition,
						savedTiles[i].correctPosition, savedTiles[i].isBlankTile);
				mTiles[i] = newTile;
			}
			//clear tilePositons from prefs.
			prefs.edit().remove(TILES_ARRAY_KEY).commit();
		}
	}


	/**
	 * create each image field for Tile objects in mTiles. If mIsNewGame=true
	 * set mIsOnClickAbled=false and vice versa
	 */
	private void tileImage() {
		Bitmap fullImage = BitmapFactory.decodeResource(getResources(), mImageResource);

		fullImage = Bitmap.createScaledBitmap(fullImage, mScreenWidth, mScreenHeight, false);
		int tileWidth = mScreenWidth / mNumColumns;
		int tileHeight = mScreenHeight / mNumColumns;

		for (int row = 0; row < mNumColumns; row++) {
			for (int col = 0; col < mNumColumns; col++) {
				Bitmap tileImage = Bitmap.createBitmap(fullImage, col * tileWidth,
						row * tileHeight, tileWidth, tileHeight);
				Tile tile = getTile(new TilePosition(col, row), false);
				if (tile.isBlankTile) {
					tileImage.eraseColor(Color.BLACK);
				}
				tile.image = tileImage;

			}
		}
		mIsOnClickAbled = !mIsNewGame;

	}

	
	/**
	 * Changes the currentPosition field of each Tile in mTiles using set shuffle algorithm.
	 */
	private void shuffleTiles() {
		boolean isEven = mNumColumns % 2 == 0;
		int newIndex = 0;

		// loop through tiles lrg->small index skipping last index because it's
		// blank tile
		for (int i = mTiles.length - 2; i >= 0; i--) {
			TilePosition newCurrentPosition = mTiles[i].currentPosition;

			// if tilecount is even then need to swap tiles in positions 0 and 1
			if (isEven && i == 0) {
				// newCurrentPosition =
				// PlayGameActivity.tileArrayPositionToTilePosition((mTiles.length
				// - 1) - 2);
				newCurrentPosition = PlayGameActivity.tileArrayPositionToTilePosition(newIndex - 1,
						mNumColumns);
			} else if (isEven && i == 1) {
				// newCurrentPosition =
				// PlayGameActivity.tileArrayPositionToTilePosition((mTiles.length
				// - 1) - 1);
				newCurrentPosition = PlayGameActivity.tileArrayPositionToTilePosition(newIndex + 1,
						mNumColumns);
			}
			// otherwise set new position to new index
			else {
				newCurrentPosition = PlayGameActivity.tileArrayPositionToTilePosition(newIndex,
						mNumColumns);
			}
			// change currentPosition field and iterate index var
			mTiles[i].currentPosition = newCurrentPosition;
			newIndex++;
		}
	}

	/**
	 * returns true if clicked tile position has blank tile directly N, S, E, or W
	 * of location, else returns false
	 * 
	 * @param clickedTilePosition
	 *            TilePosition object of clicked tile to switch
	 * @return true if valid move else false
	 */
	private boolean isValidMove(TilePosition clickedTilePosition) {
		TilePosition blankTilePosition = getBlankTile().currentPosition;
		TilePosition north = new TilePosition(blankTilePosition.column - 1, blankTilePosition.row);
		TilePosition south = new TilePosition(blankTilePosition.column + 1, blankTilePosition.row);
		TilePosition west = new TilePosition(blankTilePosition.column, blankTilePosition.row - 1);
		TilePosition east = new TilePosition(blankTilePosition.column, blankTilePosition.row + 1);

		if (clickedTilePosition.equals(north) && !isOutOfBounds(north))
			return true;
		else if (clickedTilePosition.equals(south) && !isOutOfBounds(south))
			return true;
		else if (clickedTilePosition.equals(west) && !isOutOfBounds(west))
			return true;
		else if (clickedTilePosition.equals(east) && !isOutOfBounds(east))
			return true;
		else
			return false;
	}

	/**
	 * returns true if given TilePosition row and column is less than zero or
	 * greater than or equal to number of columns in grid else returns false.
	 * i.e testMove < 0 OR >= mNumColumns -> true else -> false. if testMove is
	 * null will throw exception.
	 * 
	 * @param testMove
	 *            TilePosition to test if out of bounds
	 * @return true if position is within grid, else returns false
	 */
	private boolean isOutOfBounds(TilePosition testMove) {
		if (testMove.row < 0 || testMove.column < 0) {
			return true;
		} else if (testMove.row >= mNumColumns || testMove.column >= mNumColumns)
			return true;
		else
			return false;
	}

	/**
	 * Switches blank tile with clickedTile and set lastBlankPosition field to
	 * current position before move if valid move. Returns true if switched else
	 * returns false
	 * 
	 * @param clickedTilePosition
	 *            index position of clicked tile in GridView
	 * @return true if valid move and tiles switched, else false
	 */
	private boolean switchTiles(int clickedTilePosition) {
		TilePosition convertedClickedTilePosition = tileArrayPositionToTilePosition(
				clickedTilePosition, mNumColumns);
		Tile clickedTile = getTile(convertedClickedTilePosition, true);

		Tile blankTile = getBlankTile();
		// if clicked tile isn't a valid move just return
		if (!isValidMove(clickedTile.currentPosition))
			return false;
		// create temp position for switch
		TilePosition newBlankPosition = clickedTile.currentPosition;
		clickedTile.currentPosition = blankTile.currentPosition;
		blankTile.currentPosition = newBlankPosition;
		mTotalMoves++;
		return true;
	}

	/**
	 * Returns tile with isBlankTile equal to true in mTiles array, else returns
	 * null
	 */
	private Tile getBlankTile() {
		for (int i = 0; i < mTiles.length; i++) {
			if (mTiles[i].isBlankTile)
				return mTiles[i];
		}
		return null;
	}

	/**
	 * Saves mDifficultyLevel, mImageResource, mTotalMoves fields as well as an array of Tile objects
	 * copying mTiles array with null image fields converted to JSON.
	 */
	private void saveState() {
		SharedPreferences prefs = getSharedPreferences(ChooseImageActivity.SHARED_PREFERENCES,
				MODE_PRIVATE);
		Gson gson = new Gson();
		Tile[] savedTiles = new Tile[mDifficultyLevel];
		for (int i = 0; i < savedTiles.length; i++) {
			savedTiles[i] = new Tile(null, mTiles[i].currentPosition, mTiles[i].correctPosition,
					mTiles[i].isBlankTile);
		}
		prefs.edit().putString(TILES_ARRAY_KEY, gson.toJson(savedTiles))
				.putInt(IMAGE_RESOURCE_KEY, mImageResource)
				.putInt(DIFFICULTY_LEVEL_KEY, mDifficultyLevel)
				.putInt(TOTAL_MOVES_KEY, mTotalMoves).commit();

		/*
		 * outState.putString(TILES_ARRAY_KEY, gson.toJson(savedTiles));
		 * outState.putInt(IMAGE_RESOURCE_KEY, mImageResource);
		 * outState.putInt(DIFFICULTY_LEVEL_KEY, mDifficultyLevel);
		 * outState.putInt(TOTAL_MOVES_KEY, mTotalMoves);
		 */
	}

	protected void endActivity() {
		Intent data = getIntent();
		data.putExtra(ChooseImageActivity.END_APP_KEY, true);
		setResult(Activity.RESULT_OK, data);
		finish();

	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.difficulty:
			//User wants to change difficulty setting
			// openDifficultyChoose();
			DialogFragment dialog = new DifficultyDialogFragment();
//			Bundle bundle = new Bundle();
//			bundle.putInt(DIFFICULTY_LEVEL_KEY, mDifficultyLevel);
//			dialog.setArguments(bundle);
			dialog.show(getFragmentManager(), "foo");
			return true;
		case R.id.newGame:
			finish();
			return true;
		case R.id.shuffle:
			getSharedPreferences(ChooseImageActivity.SHARED_PREFERENCES, MODE_PRIVATE).edit()
			.remove(TILES_ARRAY_KEY)
			.commit();
			createTiles();
			tileImage();
			shuffleTiles();
			mTilesAdapter.notifyDataSetChanged();
			mIsOnClickAbled = true;
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private boolean isSolved() {
		for (int i = 0; i < mTiles.length; i++) {
			if (!mTiles[i].currentPosition.equals(mTiles[i].correctPosition))
				return false;
		}
		return true;
	}
	
	/**
	 * Load WinDialogFragment object.
	 */
	private void loadWinDialog(){
		DialogFragment winDialog = new WinDialogFragment();
		Bundle winBundle = new Bundle();
		winBundle.putInt(TOTAL_MOVES_KEY, mTotalMoves);
		winDialog.setArguments(winBundle);
		winDialog.show(getFragmentManager(), "foo");
	}

	//original method works for singleTap only. sliding gesture implemented via onTouchEvent method
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		//if mIsOnClickAbled = false just ignore
		if (mIsOnClickAbled) {
			if (switchTiles(position)) {
//				Toast.makeText(mContext, "Total moves: " + mTotalMoves, Toast.LENGTH_SHORT).show();
				if (isSolved()) {
					loadWinDialog();
				}
			} else {
				Toast.makeText(mContext, "Not a valid move.", Toast.LENGTH_SHORT).show();
			}
			mTilesAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Returns a Tile object with currentPosition equal to given TilePosition if
	 * returnCurrent is true, else returns Tile with correctPosition equal to
	 * given TilePosition. If no match is found returns null. requires
	 * TilePosition row and column value be greater than or equal to zero and
	 * less than number of columns in grid.
	 * 
	 * @param aTilePosition
	 *            TilePosition of Tile to return.
	 * @param returnCurrent
	 *            boolean for comparing currentPosition or correctPosition
	 * @return Tile object with current or correct position equal to given
	 *         position, else null.
	 */
	protected Tile getTile(TilePosition aTilePosition, boolean returnCurrent) {
		for (int i = 0; i < mTiles.length; i++) {
			if (returnCurrent) {
				if (mTiles[i].currentPosition.equals(aTilePosition))
					return mTiles[i];
			} else {
				if (mTiles[i].correctPosition.equals(aTilePosition))
					return mTiles[i];
			}
		}
		return null;
	}

	/**
	 * Converts an index position of array to a column, row position in a grid represented by
	 * TilePosition object.
	 * e.g. tileArrayPositionToTilePositon(0, 3) will return a TilePositon object with col and row
	 * fields of 0. tileArrayPositionToTilePositon(1, 3) will return TilePositon object with col and row
	 * fields of 1 and 0 respectively. tileArrayPositionToTilePositon(3, 3) will return TilePositon
	 * object with col and row fields of 0 and 1 respectively.
	 * @param tileArrayIndexPosition Index position in array to convert.
	 * @param numColumns Number of columns in grid.
	 * @return
	 */
	protected static TilePosition tileArrayPositionToTilePosition(int tileArrayIndexPosition,
			int numColumns) {
		int index = 0;
		for (int row = 0; row < numColumns; row++) {
			for (int col = 0; col < numColumns; col++) {
				if (index == tileArrayIndexPosition)
					return new TilePosition(col, row);
				index++;
			}
		}

		return null;
	}

	protected int getNumColumns() {
		return mNumColumns;
	}

	protected int getDifficultyLevel() {
		return mDifficultyLevel;
	}

	protected static int tilePositionToArrayPosition(TilePosition aTilePosition, int numColumns) {
		if (aTilePosition != null) {
			int index = 0;
			for (int row = 0; row < numColumns; row++) {
				for (int col = 0; col < numColumns; col++) {
					if (aTilePosition.equals(new TilePosition(col, row)))
						return index;
					index++;
				}
			}
		}

		return -1;
	}


}
