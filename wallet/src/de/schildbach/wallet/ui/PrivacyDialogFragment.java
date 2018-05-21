/*
 * Copyright 2013-2015 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.schildbach.wallet.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.widget.TextView;

import rusapps.sibcoin.wallet.R;


public final class PrivacyDialogFragment extends DialogFragment
{
	private static final String FRAGMENT_TAG = PrivacyDialogFragment.class.getName();

	private static final String KEY_MESSAGE = "message";

	public static void page(final FragmentManager fm, final int messageResId)
	{
		final DialogFragment newFragment = PrivacyDialogFragment.instance(messageResId);
		newFragment.show(fm, FRAGMENT_TAG);
	}

	private static PrivacyDialogFragment instance(final int messageResId)
	{
		final PrivacyDialogFragment fragment = new PrivacyDialogFragment();

		final Bundle args = new Bundle();
		args.putInt(KEY_MESSAGE, messageResId);
		fragment.setArguments(args);

		return fragment;
	}

	private Activity activity;

	@Override
	public void onAttach(final Activity activity)
	{
		super.onAttach(activity);

		this.activity = activity;
	}

	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState)
	{
		final Bundle args = getArguments();
		final int messageResId = args.getInt(KEY_MESSAGE);

		// Linkify URL in text
		TextView textView = new TextView(activity);
		textView.setMovementMethod(LinkMovementMethod.getInstance());
		textView.setPadding(60, 60, 60, 60);
		textView.setText(messageResId);

		final DialogBuilder dialog = new DialogBuilder(activity);
		dialog.setView(textView);
		dialog.setPositiveButton(R.string.privacy_dialog_ok, null);
		AlertDialog alertDialog = dialog.create();

		// Disallow dismissing dialog on outside touch or back button
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return keyCode == KeyEvent.KEYCODE_BACK;
			}
		});

		return alertDialog;
	}
}
