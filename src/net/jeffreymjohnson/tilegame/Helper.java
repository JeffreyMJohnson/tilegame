package net.jeffreymjohnson.tilegame;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Helper {
    private static final String TAG = "net.jeffreymjohnson.Helper";
    
    public static int calculateInSampleSize(
	    BitmapFactory.Options options, int reqWidth, int reqHeight) {
	// Raw height and width of image
	final int height = options.outHeight;
	final int width = options.outWidth;
	int inSampleSize = 1;

	if (height > reqHeight || width > reqWidth) {

	    // Calculate ratios of height and width to requested height and width
	    final int heightRatio = Math.round((float) height / (float) reqHeight);
	    final int widthRatio = Math.round((float) width / (float) reqWidth);

	    // Choose the smallest ratio as inSampleSize value, this will guarantee
	    // a final image with both dimensions larger than or equal to the
	    // requested height and width.
	    inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	}

	return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	    int reqWidth, int reqHeight) {

	// First decode with inJustDecodeBounds=true to check dimensions
	final BitmapFactory.Options options = new BitmapFactory.Options();
	options.inJustDecodeBounds = true;
	BitmapFactory.decodeResource(res, resId, options);

	// Calculate inSampleSize
	options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	// Decode bitmap with inSampleSize set
	options.inJustDecodeBounds = false;
	return BitmapFactory.decodeResource(res, resId, options);
    }

    //returns method object with name equal to given String (case insensitive) from given Method[]
    //else returns null
    public static Method getMethod(Method[] methods, String methodName){
	if (methods != null && methodName != "" ){
	    for (int i = 0; i < methods.length; i++){
//		Log.i("main", "method name: " + methods[i].getName());
		if (methods[i].getName().equalsIgnoreCase(methodName)){
		    methods[i].setAccessible(true);
		    return methods[i];
		}

	    }
	}
	Log.e(TAG + ".getMethod", "No method found!");
	return null;
    }

    //returns Field object with name equal to given String (case insensitive) from given Field[]
    //else returns null
    public static Field getField (Field[] fields, String fieldName){
	if (fields != null && fieldName != ""){
	    for (int i = 0; i < fields.length; i++){
//		Log.i("main", "field name: " + fields[i].getName());
		if (fields[i].getName().equalsIgnoreCase(fieldName)){
		    fields[i].setAccessible(true); 
		    return fields[i];
		}
	    }
	}
	return null;
    }
}
