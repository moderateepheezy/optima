package ru.max64.myappstime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		Thread timer = new Thread(){
			public void run(){
				try{
					sleep(3000);
				} catch(InterruptedException e){
					e.printStackTrace();
				}finally{
					Intent loginActivity = new Intent(Splash.this, Main_menu.class);
					startActivity(loginActivity);
				}
			}
		};
		timer.start();
	}
}
