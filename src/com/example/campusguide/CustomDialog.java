package com.example.campusguide;

import com.google.android.gms.maps.model.Marker;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

	  public Activity c;
	  public Dialog d;
	  public Button ok, no;
	  TextView diag, info;
	  Marker marker;
	  String infotext;

	  public CustomDialog(Activity a, Marker m, String s) {
	    super(a);
	    // TODO Auto-generated constructor stub
	    this.c = a;
	    marker = m;
	    infotext = s;
	  }

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.marker_info);
	    //setContentView(R.layout.testlayout);
	    
	    ok = (Button) findViewById(R.id.dialogButtonOK);
	    ok.setOnClickListener(this);
	    
	    diag = (TextView) findViewById(R.id.txt_dia);
	    diag.setText( marker.getTitle());
	    
	    info = (TextView) findViewById(R.id.txt_info);
	    info.setText(infotext);
	       
	    info.setMovementMethod(new ScrollingMovementMethod());
	  }

	  @Override
	  public void onClick(View v) {
	    dismiss();
	  }
}
