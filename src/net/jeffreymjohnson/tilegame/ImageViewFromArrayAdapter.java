package net.jeffreymjohnson.tilegame;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class ImageViewFromArrayAdapter extends ArrayAdapter<View>{

    private static final int imageWidth = 200;
    private static final int imageHeight = 200;
    int[] images;

    public ImageViewFromArrayAdapter(Context context, int textViewResourceId) {
	super(context, textViewResourceId);
	images = new int[13];
	images[0] = R.drawable.katie;
	images[1] = R.drawable.big_buddha;
	images[2] = R.drawable.christ_the_redeemer;
	images[3] = R.drawable.corvette;
	images[4] = R.drawable.face_mask;
	images[5] = R.drawable.fly_closeup;
	images[6] = R.drawable.kitten;
	images[7] = R.drawable.masaratti_engine;
	images[8] = R.drawable.pandas_kissing;
	images[9] = R.drawable.seattle_landmarks;
	images[10] = R.drawable.statue_garden;
	images[11] = R.drawable.stone_arch;
	images[12] = R.drawable.tree;
    }

    @Override
    public int getCount(){
	return images.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
	ImageView view;
	if (convertView == null){
	    view = new ImageView(getContext());
	}
	else{
	    view = (ImageView) convertView;
	}
	view.setId(images[position]);
	view.setImageBitmap(Helper.decodeSampledBitmapFromResource(getContext().getResources(),
		images[position], imageWidth, imageHeight));
	return view;

    }

    

}
