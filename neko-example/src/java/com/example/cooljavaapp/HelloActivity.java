package com.example.cooljavaapp;

import android.app.Activity;
import android.os.Bundle;

/**
 * A minimal "Hello, World!" application demonstrating use of string resources.
 */
public class HelloActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the layout.
        setContentView(R.layout.main);
    }
}