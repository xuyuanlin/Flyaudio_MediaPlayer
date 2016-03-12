package com.flyaudio.flyMediaPlayer.until;

import java.util.LinkedList;
import java.util.List;
import com.flyaudio.flyMediaPlayer.until.Flog;
import android.app.Activity;
import android.app.Application;

public class AllListActivity extends Application {
	private static String TAG = "AllListActivity";
	private List<Activity> activityList = new LinkedList();
	private static AllListActivity instance;
	private String path;
	private int position;

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	// 单例模式中获取唯一的Mapplication实例
	public static AllListActivity getInstance() {
		if (null == instance) {
			instance = new AllListActivity();
		}
		return instance;

	}

	// 添加Activity到容器中
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	// 遍历所有Activity并finish

	public void exit() {
		Flog.d(TAG,"alllistactivity---exit()-------------------------exit");

		for (Activity activity : activityList) {
			if (activity != null) {
				Flog.d(TAG,"alllistactivity---exit()---" + activity);
				activity.finish();
			}
		}
	
//		System.exit(0);

		

	}
}