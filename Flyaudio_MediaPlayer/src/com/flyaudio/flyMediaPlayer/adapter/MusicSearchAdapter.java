package com.flyaudio.flyMediaPlayer.adapter;

import java.util.ArrayList;
import java.util.List;
import com.flyaudio.flyMediaPlayer.data.DBDao;
import com.flyaudio.flyMediaPlayer.objectInfo.SearchInfo;
import com.flyaudio.flyMediaPlayer.until.Flog;
import com.flyAudio.flyMediaPlayer.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

public class MusicSearchAdapter extends BaseAdapter implements Filterable {
	private static final String TAG = "MusicSearchAdapter";
	private Context mContext;
	private LayoutInflater layoutInflater;
//	private int nPage;
	private List<SearchInfo>mList;
	private DBFilter mFilter;
	private DBDao mDao;

	public MusicSearchAdapter(Context context) {
		Flog.d(TAG, "MusicNameListAdapter");
		mContext = context;
//		this.nPage = page;
//		this.mList=list;
		layoutInflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getCount()--start");
		Flog.d(TAG,
				"getCount()--mList.size()--" + mList.size());
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getItem");
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getItemId");
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getView()------start");
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = layoutInflater
					.inflate(R.layout.music_auto_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mAutoItem = (TextView) convertView
					.findViewById(R.id.music_auto_item);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mAutoItem.setText(mList.get(position).getmName());
		
		Flog.d(TAG, "getView-----end");

		return convertView;
	}


	class ViewHolder {
		public TextView mAutoItem;
	}
	public List<SearchInfo> getNameList(){
		return mList;
	}

	

	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		if (mFilter == null) {
			mFilter = new DBFilter();
		}
		return mFilter;
	}
	/**
	 * 数据库查询过滤器
	 * 
	 * @author Administrator
	 * 
	 */
	private class DBFilter extends Filter {
		/**
		 * 查询数据库
		 */
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			// TODO Auto-generated method stub
			//查询结果保存到FilterResults对象里
			Flog.d(TAG, "performFiltering()--start");
			FilterResults results = new FilterResults();
			mDao=new DBDao(mContext);
			List<SearchInfo> mNameList=new ArrayList<SearchInfo>();
			mNameList=mDao.getNameList();
			List<SearchInfo>mAddList=new ArrayList<SearchInfo>();
			
			Flog.d(TAG, "performFiltering()--mNameList--"+mNameList);
			Flog.d(TAG, "performFiltering()--prefix--"+prefix.length());
			if (prefix.length()>=2) {
				for (int i = 0; i < mNameList.size(); i++) {
					if (mNameList.get(i).getmName().contains(prefix)) {
						mAddList.add(mNameList.get(i));
					}
				}
				results.values = mAddList;
				results.count = mAddList.size();
			}
			Flog.d(TAG, "performFiltering()--mAddList--"+mAddList);
			Flog.d(TAG, "performFiltering()--results--"+results.values);
			Flog.d(TAG, "performFiltering()--end");
			return results;
		}
 
		/**
		 * 更新UI
		 */
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			// TODO Auto-generated method stub
			mList = (List<SearchInfo>) results.values;
			Flog.d(TAG, "publishResult()--constraint===="+constraint);
			 if (results.count > 0) {
                 notifyDataSetChanged();
             } else {
                 notifyDataSetInvalidated();
                 if (constraint!=null) {
                	  Toast.makeText(mContext, "没有匹配到相应信息，请重新输入...", Toast.LENGTH_SHORT).show();
				}
               
             }
		}
 
	}
}
