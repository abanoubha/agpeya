package com.softwarepharaoh.agpeya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Thread() {
            public void run() {
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.getStackTrace();
                } finally {
                    SplashActivity.this.startActivity(
                            new Intent(
                                    SplashActivity.this,
                                    AgpeyaListActivity.class
                            )
                    );
                }
            }
        }.start();
    }
    public void onPause() {
        super.onPause();
        finish();
    }
}
