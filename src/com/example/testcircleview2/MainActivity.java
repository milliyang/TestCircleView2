package com.example.testcircleview2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.testcircleview2.*;

public class MainActivity extends Activity {

	private static LinkedList<ApplicationInfo> mApplications;
	private static final int MAX_APP_NUMS = 9;
	private int mIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Log.d("yangth","0001");
		
		Log.i("yangth mIndex:", String.valueOf(mIndex++));
		
		CircleView2 cv = (CircleView2) findViewById(R.id.view1);
		
		loadApplications(true);
		cv.setmApplications(this.mApplications);
		
		//setContentView(cv);
		
		/*
		Button b = new Button(this);
		b.setText("yang1");
		cv.addSubView( b );
		
		b = new Button(this);
		b.setText("yang2");
		cv.addSubView( b );
		
		b = new Button(this);
		b.setText("yang3");
		cv.addSubView( b );
		
		b = new Button(this);
		b.setText("yang4");
		cv.addSubView( b );
		
		b = new Button(this);
		b.setText("yang5");
		cv.addSubView( b );
		
		b = new Button(this);
		b.setText("yang6");
		cv.addSubView( b );
		
		b = new Button(this);
		b.setText("yang7");
		cv.addSubView( b );
		
		b = new Button(this);
		b.setText("yang8");
		cv.addSubView( b );
		
		b = new Button(this);
		b.setText("yang9");
		cv.addSubView( b );
		 //*/
		
		Log.w("yangth","00333");
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    /**
     * Loads the list of installed applications in mApplications.
     */
    private void loadApplications(boolean isLaunching) {
        if (isLaunching && mApplications != null) {
            return;
        }

        PackageManager manager = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = manager.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(manager));

        if (apps != null) {
            final int count = apps.size();

            if (mApplications == null) {
                //mApplications = new ArrayList<ApplicationInfo>(count);
            	mApplications = new LinkedList<ApplicationInfo>();
            }
            mApplications.clear();

            for (int i = 0; i < count && i <= MAX_APP_NUMS ; i++) {
                ApplicationInfo application = new ApplicationInfo();
                ResolveInfo info = apps.get(i);

                application.title = info.loadLabel(manager);
                application.setActivity(new ComponentName(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name),
                        Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                application.icon = info.activityInfo.loadIcon(manager);

                mApplications.add(application);
            }
        }
    }
    



}
