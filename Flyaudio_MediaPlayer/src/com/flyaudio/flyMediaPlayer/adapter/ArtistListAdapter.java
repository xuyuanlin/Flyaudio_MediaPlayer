package com.flyaudio.flyMediaPlayer.adapter;

import java.util.List;

import com.flyaudio.flyMediaPlayer.adapter.AlbumListAdapter.ViewHolder;
import com.flyaudio.flyMediaPlayer.objectInfo.AristInfo;
import com.flyaudio.flyMediaPlayer.perferences.AlbumList;
import com.flyaudio.flyMediaPlayer.perferences.ArtistList;
import com.flyaudio.flyMediaPlayer.perferences.CoverList;
import com.flyaudio.flyMediaPlayer.perferences.MusicList;
import com.flyaudio.flyMediaPlayer.serviceImpl.MediaService;
import com.flyaudio.flyMediaPlayer.until.AlbumUtil;
import com.flyaudio.flyMediaPlayer.until.Constant;
import com.flyaudio.flyMediaPlayer.until.Flog;
import com.flyAudio.flyMediaPlayer.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArtistListAdapter extends BaseAdapter implements OnClickListener {
	private static final String TAG = "ArtistListAdapter";
	private Context mContext;
	private LayoutInflater layoutInflater;
	private List<AristInfo> mAristInfos;
	private int page;
	private int nArtistPosition;
	private AlbumUtil mAlbumUtil;

	public ArtistListAdapter(Context context, List<AristInfo> mAristInfos,
			int page,AlbumUtil mAlbumUtil) {
		this.mAlbumUtil=mAlbumUtil;
		mContext = context;
		this.mAristInfos = mAristInfos;
		this.page = page;
		layoutInflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getCount--start");

		int count = 0;
		switch (page) {
		case Constant.VIEWPAHER_MENU_ARTIST:
			if (mAristInfos != null) {
				count = mAristInfos.size();
			} else {
				count = 0;
			}

			// count = ArtistList.list.size();
			break;
		case Constant.VIEWPAHER_MENU_ARTIST_LIST:
			// count = MusicList.aristList.size();
			if (ArtistList.list.size() > nArtistPosition) {
				count = ArtistList.list.get(nArtistPosition).getMusicList()
						.size();
			} else {
				count = 0;
			}

			break;
		}
		Flog.d(TAG, "getCount--count--" + count);
		Flog.d(TAG, "getCount--end");
		return count;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getItem");
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "getItemId");
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Flog.d(TAG, "getView()------start");
		ViewHolder viewHolder;
//		AlbumUtil mAlbumUtil=new AlbumUtil();
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.music_artist_item,
					null);
			viewHolder = new ViewHolder();
			viewHolder.mArtistView = (LinearLayout) convertView
					.findViewById(R.id.artist_View);
			viewHolder.mArtist = (TextView) convertView
					.findViewById(R.id.artist_list);
			viewHolder.mAlbum = (ImageView) convertView
					.findViewById(R.id.album_list);
			viewHolder.mAristSize = (TextView) convertView
					.findViewById(R.id.artist_size_list);
			viewHolder.mArtistItemView = (LinearLayout) convertView
					.findViewById(R.id.artist_itemView);
			viewHolder.mSongItem = (TextView) convertView
					.findViewById(R.id.song_item);
			viewHolder.mAlbumItem = (ImageView) convertView
					.findViewById(R.id.album_item);
			viewHolder.mArtistItem = (TextView) convertView
					.findViewById(R.id.artist_item);
			viewHolder.mFavoritesItem = (ImageButton) convertView
					.findViewById(R.id.favorites_item);
			viewHolder.mDetailItem = (ImageButton) convertView
					.findViewById(R.id.detail_item);
			viewHolder.mClearItem = (ImageButton) convertView
					.findViewById(R.id.clear_item);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		switch (page) {
		case Constant.VIEWPAHER_MENU_ARTIST:

			if (mAristInfos != null) {
				if (viewHolder.mArtistItemView.getVisibility() == View.VISIBLE) {
					viewHolder.mArtistItemView.setVisibility(View.GONE);
				}
				if (viewHolder.mArtistView.getVisibility() != View.VISIBLE) {
					viewHolder.mArtistView.setVisibility(View.VISIBLE);
				}
				viewHolder.mArtist
						.setText(mAristInfos.get(position).getArist());

				viewHolder.mAristSize.setText(mAristInfos.get(position)
						.getAristCount() + Constant.MUSIC_SIZE);
				CoverList.cover=null;
				CoverList.bitmap=null;
				mAlbumUtil.scanAlbumImage(mAristInfos.get(position).getMusicPath(), mContext);
				if (CoverList.cover==null) {
					viewHolder.mAlbum.setImageResource(R.drawable.main_album_item);
				}else {
					viewHolder.mAlbum.setImageDrawable(CoverList.cover);
				}

//				viewHolder.mAlbum.setImageResource(R.drawable.main_album_item);

			}

			break;
		case Constant.VIEWPAHER_MENU_ARTIST_LIST:
			if (ArtistList.list.size() > 0) {
				if (viewHolder.mArtistView.getVisibility() == View.VISIBLE) {
					viewHolder.mArtistView.setVisibility(View.GONE);
				}
				if (viewHolder.mArtistItemView.getVisibility() != View.VISIBLE) {
					viewHolder.mArtistItemView.setVisibility(View.VISIBLE);
				}
				// viewHolder.mSongItem.setText(MusicList.aristList.get(position).getName());
				//
				// viewHolder.mArtistItem.setText(MusicList.aristList.get(position).getArtist());
				//
				// Bitmap mAlbumItemBitmap =
				// AlbumUtil.scanAlbumImage(MusicList.aristList.get(position).getPath());
				//
				viewHolder.mSongItem.setText(ArtistList.list
						.get(nArtistPosition).getMusicList().get(position)
						.getName());

				viewHolder.mArtistItem.setText(ArtistList.list
						.get(nArtistPosition).getMusicList().get(position)
						.getArtist());

				Flog.d(TAG,
						"ArtistList.list.get(nArtistPosition).getMusicList().get(position).getPath()--"
								+ ArtistList.list.get(nArtistPosition)
										.getMusicList().get(position).getPath());

				viewHolder.mAlbumItem
						.setImageResource(R.drawable.main_album_item);

				if (CoverList.cover==null) {
					viewHolder.mAlbumItem.setImageResource(R.drawable.main_album_item);
				}else {
					viewHolder.mAlbumItem.setImageDrawable(CoverList.cover);
				}

				viewHolder.mFavoritesItem.setImageResource(ArtistList.list
						.get(nArtistPosition).getMusicList().get(position)
						.isFavorite() ? R.drawable.main_favorites
						: R.drawable.main_favorites_u);
				if (MediaService.sMusicPath != null) {
					for (int i = 0; i < ArtistList.list.get(nArtistPosition)
							.getMusicList().size(); i++) {
						if (MediaService.sMusicPath.equals(ArtistList.list
								.get(nArtistPosition).getMusicList()
								.get(position).getPath())) {
							if (i == position) {
								viewHolder.mSongItem.setTextColor(Color.BLUE);
								viewHolder.mArtistItem.setTextColor(Color.BLUE);
								break;
							}

						} else {
							viewHolder.mSongItem.setTextColor(Color.WHITE);
							viewHolder.mArtistItem.setTextColor(Color.WHITE);
						}

					}
				}

				viewHolder.mClearItem.setTag(position);
				viewHolder.mClearItem.setOnClickListener(this);
				viewHolder.mDetailItem.setTag(position);
				viewHolder.mDetailItem.setOnClickListener(this);
				viewHolder.mFavoritesItem.setTag(position);
				viewHolder.mFavoritesItem.setOnClickListener(this);
			}

			break;
		}

		Flog.d(TAG, "getView()-----end");
		return convertView;
	}

	public void setArtistPosition(int position) {
		this.nArtistPosition = position;
	}

	public void setArtistIfo(List<AristInfo> mAristInfos) {
		this.mAristInfos = mAristInfos;
	}

	public void update(int page) {
		this.page = page;
		notifyDataSetChanged();
	}

	// 返回页面的状态

	public int getPage() {
		return page;
	}

	class ViewHolder {
		public TextView mArtist;
		public TextView mAristSize;
		public ImageView mAlbum;
		private LinearLayout mArtistView;
		private LinearLayout mArtistItemView;
		public TextView mSongItem;
		public TextView mArtistItem;
		public ImageView mAlbumItem;
		public ImageButton mFavoritesItem;
		public ImageButton mDetailItem;
		public ImageButton mClearItem;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Flog.d(TAG, "---onClick()----start");
		Intent intent = null;
		Flog.d(TAG, "---onClick()--tag--" + v.getTag());
		switch (v.getId()) {
		case R.id.favorites_item:
			intent = new Intent(Constant.BROADCAST_ACTION_FAVORITE);
			break;
		case R.id.clear_item:
			intent = new Intent(Constant.BROADCAST_ACTION_CLEAR);
			break;
		case R.id.detail_item:
			intent = new Intent(Constant.BROADCAST_ACTION_DETAIL);
			break;
		}
		if (intent != null) {
			intent.putExtra(Constant.BROADCAST_INTENT_PAGE, page);
			intent.putExtra(Constant.BROADCAST_INTENT_POSITION,
					(Integer) v.getTag());
			mContext.sendBroadcast(intent);
		}
		Flog.d(TAG, "---onClick()----end");

	}
}
