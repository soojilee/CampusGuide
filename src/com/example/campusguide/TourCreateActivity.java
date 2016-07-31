package com.example.campusguide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import android.text.Editable;
import android.text.TextWatcher;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class TourCreateActivity extends Activity {
	ArrayList<String> placesList;
	ArrayList<Integer> selectedTours = new ArrayList<Integer>();
	ArrayAdapter<String> adapter;
	ListView places;
	EditText search;
	
	Map<String, String> placesMap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_create);
        

        LocationXmlParser locations = new LocationXmlParser(getApplicationContext());
        
        EditText search = (EditText) findViewById(R.id.editText1);
        search.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				TourCreateActivity.this.adapter.getFilter().filter(s);
				
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
        });
        
        places = (ListView) findViewById(R.id.list);
        places.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        places.setOnItemClickListener(new OnItemClickListener() {
        	
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
					if(selectedTours.contains(position)) {
						selectedTours.remove(selectedTours.indexOf(position));
					} else {
						selectedTours.add(position);
					}
			}
        });
        
      //find button
        Button tourButton = (Button)findViewById(R.id.tourButton);
        tourButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), MapView.class);
				i.putIntegerArrayListExtra("tour_list", selectedTours);
				startActivity(i);
			}
        	
        });
        

        //change values to waterloo map areas
        //Information we can show is opening and closing times

        placesList = new ArrayList<String>();
        for (int i = 0; i < locations.getSize(); ++i) {
          placesList.add(locations.getNameOf(i));
        }
        
        adapter = new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_multiple_choice, placesList);
        places.setAdapter(adapter);
        
    }
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.map_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
