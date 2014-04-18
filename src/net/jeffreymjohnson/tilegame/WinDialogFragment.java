package net.jeffreymjohnson.tilegame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class WinDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState){
	super.onCreateDialog(savedInstanceState);
	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	String message = getString(R.string.total_moves_pre);
	int totalMoves = getArguments().getInt(PlayGameActivity.TOTAL_MOVES_KEY);
	message += " " + totalMoves + " ";
	message += getString(R.string.total_moves_post);
	builder.setTitle(R.string.win_dialog_title)
	.setMessage(message)
	.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		getActivity().finish();
	    }
	})
	.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {

	    @Override
	    public void onClick(DialogInterface dialog, int which) {
		
		((PlayGameActivity)getActivity()).endActivity();
	    }
	});
	return builder.create();
    }

}
