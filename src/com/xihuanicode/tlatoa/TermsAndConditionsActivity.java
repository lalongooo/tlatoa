package com.xihuanicode.tlatoa;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TermsAndConditionsActivity extends Activity implements View.OnClickListener,
		DialogInterface.OnClickListener {

	private Button b;
	private TextView t;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.terms_and_conditions);
		b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(this);

		t = (TextView) findViewById(R.id.textView1);
	}

	@Override
	public void onClick(View v) {
		AlertDialog ad = new AlertDialog.Builder(this)
				.setMessage("Blah blah blah.\n Fine pring.\n Do you accept all our terms and conditions?")
				.setIcon(R.drawable.tlatoa_icon).setTitle("Terms of Service")
				.setPositiveButton("Yes", this).setNegativeButton("No", this)
				.setNeutralButton("Cancel", this).setCancelable(false).create();

		ad.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE: // yes
			t.setText("You have accepted the TOS. Welcom to the site");
			break;
		case DialogInterface.BUTTON_NEGATIVE: // no
			t.setText("You have denied the TOS. You may not access the site");
			break;
		case DialogInterface.BUTTON_NEUTRAL: // neutral
			t.setText("Please select yes or no");
			break;
		default:
			// nothing
			break;
		}
	}
}