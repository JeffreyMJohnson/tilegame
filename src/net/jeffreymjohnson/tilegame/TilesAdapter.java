package net.jeffreymjohnson.tilegame;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class TilesAdapter extends ArrayAdapter<View> {

    private PlayGameActivity mContext;
    private static final String TAG = "TilesAdapter";

    public TilesAdapter(Context context, int textViewResourceId) {
	super(context, textViewResourceId);
	this.mContext = (PlayGameActivity)context;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent){
	ImageView view;
	if (convertView == null){
	    view = new ImageView(getContext());
	}
	else{
	    view = (ImageView) convertView;
	}
	
	TilePosition requestedPosition = PlayGameActivity.tileArrayPositionToTilePosition(position, mContext.getNumColumns());
	Tile tile = mContext.getTile(requestedPosition, true);
	try{
	view.setImageBitmap(tile.image);
	}
	catch(NullPointerException e){
		Log.d(TAG, "tile is null " + e.getMessage());
//		throw e;
	}
	return view;
    }

    @Override
    public int getCount(){
	return mContext.getDifficultyLevel();

    }
}
