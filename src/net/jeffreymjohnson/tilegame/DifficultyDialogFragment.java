/**
 * 
 */
package net.jeffreymjohnson.tilegame;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * @author Jeff
 * 
 */
public class DifficultyDialogFragment extends DialogFragment {
	int mDifficultyLevel;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		super.onCreateDialog(savedInstanceState);

		int currentDifficulty = getActivity().getSharedPreferences(
				ChooseImageActivity.SHARED_PREFERENCES, Context.MODE_PRIVATE).getInt(
				PlayGameActivity.DIFFICULTY_LEVEL_KEY, PlayGameActivity.DIFFICULTY_LEVEL_MEDIUM);
		int selectedEntry;
		switch (currentDifficulty) {
		case PlayGameActivity.DIFFICULTY_LEVEL_EASY:
			selectedEntry = 0;
			break;
		case PlayGameActivity.DIFFICULTY_LEVEL_MEDIUM:
			selectedEntry = 1;
			break;
		case PlayGameActivity.DIFFICULTY_LEVEL_HARD:
			selectedEntry = 2;
			break;
		default:
			selectedEntry = 1;
			break;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.difficulty_dialog_title).setSingleChoiceItems(
				R.array.pref_difficulty_list, selectedEntry, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							mDifficultyLevel = PlayGameActivity.DIFFICULTY_LEVEL_EASY;
							break;
						case 1:
							mDifficultyLevel = PlayGameActivity.DIFFICULTY_LEVEL_MEDIUM;
							break;
						case 2:
							mDifficultyLevel = PlayGameActivity.DIFFICULTY_LEVEL_HARD;
							break;
						default:
							assert false : "difficulty level not chosen";
							mDifficultyLevel = PlayGameActivity.DIFFICULTY_LEVEL_MEDIUM;
							break;
						}
						((PlayGameActivity) getActivity()).changeDifficultyLevel(mDifficultyLevel);
						dismiss();

					}
				});
		return builder.create();
	}
}
