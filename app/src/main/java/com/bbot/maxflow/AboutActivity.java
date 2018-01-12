package com.bbot.maxflow;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {

    // clear icon: Icon made by Google from www.flaticon.com
    // tick-inside-circle:  Icon made by Google from www.flaticon.com
    // check-symbol: Icon made by Google from www.flaticon.com
    // keyboard-right-arrow-button: Icon made by Google from www.flaticon.com
    // play-arrow: Icon made by Google from www.flaticon.com
    // go-back-left-arrow: Icon made by Google from www.flaticon.com
    // garbage icon: Icon made by Madebyoliver from www.flaticon.com
    // network icon: Icon made by Madebyoliver from www.flaticon.com
    // delete icon: Icon made by Freepik from www.flaticon.com
    // Vector diagonal line with box edges: Icon made by Freepik from www.flaticon.com
    // business-affiliate-network: Icon made by Freepik from www.flaticon.com
    // convergence: Icon made by Freepik from www.flaticon.com
    // heart-meter: Icon made by Freepik from www.flaticon.com

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView abouttxt = (TextView) findViewById(R.id.txt_credits);
        abouttxt.setText("MaxFlow\n\n Developer: BanishedBot https://github.com/sam46\n\n\n"+getResources().getString(R.string.copyrights));
    }
}
