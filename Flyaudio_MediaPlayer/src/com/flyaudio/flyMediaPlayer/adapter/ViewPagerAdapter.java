package com.flyaudio.flyMediaPlayer.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class ViewPagerAdapter extends PagerAdapter {
	
	private List<View> paperViews;

	
	public ViewPagerAdapter(List<View> paperViews) {
		super();
		this.paperViews = paperViews;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return paperViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View view, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager)view).removeView(paperViews.get(position));
	}

	@Override
	public void finishUpdate(View container) {
		// TODO Auto-generated method stub
	}

	@Override
	public Object instantiateItem(View container, int position) {
		// TODO Auto-generated method stub
		((ViewPager)container).addView(paperViews.get(position));
		return paperViews.get(position);
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
		// TODO Auto-generated method stub
		super.restoreState(state, loader);
	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View container) {
		// TODO Auto-generated method stub
		
	}

}
