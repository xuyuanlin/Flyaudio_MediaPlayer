package com.flyaudio.flyMediaPlayer.until;

import java.io.File;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.images.Artwork;

import com.flyAudio.flyMediaPlayer.R;
import com.flyaudio.flyMediaPlayer.perferences.CoverList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;

/**
 * Open Source Project
 * 
 * <br>
 * <b>扫描音乐内嵌专辑图片处理类</b></br>
 * 
 * 
 */
public class AlbumUtil {
	private static String TAG = "AlbumUtil";
	private static Artwork artwork;
	private Context mContext;

	/**
	 * 扫描音乐内嵌专辑图片
	 * 
	 * @param path
	 *            音乐SD卡路径
	 * @return 有则返回图片，无则返回null
	 */
	public void scanAlbumImage(String path, Context context) {
		Bitmap bitmap = null;
		this.mContext = context;
		Flog.d(TAG,
				"AlbumUtil------------------------------scanAlbumImage-------"
						+ path);
		File file = new File(path);

		if (file.exists()) {// 下面的方法有问题
			try {
				MP3File mp3File = (MP3File) AudioFileIO.read(file);
				if (mp3File != null) {

					if (mp3File.hasID3v1Tag()) {
						Tag tag = mp3File.getTag();

						artwork = tag.getFirstArtwork();// 获得专辑图片

						if (artwork != null) {
							byte[] byteArray = artwork.getBinaryData();// 将读取到的专辑图片转成二进制

							CoverList.cover = new BitmapDrawable(
									mContext.getResources(),
									BitmapFactory.decodeByteArray(byteArray, 0,
											byteArray.length));
							bitmap = BitmapFactory.decodeByteArray(byteArray,
									0, byteArray.length);

						}
					} else if (mp3File.hasID3v2Tag()) {// 如果上面的条件不成立，才执行下面的方法
						AbstractID3v2Tag v2Tag = mp3File.getID3v2Tag();

						Artwork artwork = v2Tag.getFirstArtwork();// 获得专辑图片

						if (artwork != null) {
							byte[] byteArray = artwork.getBinaryData();// 将读取到的专辑图片转成二进制

							CoverList.cover = new BitmapDrawable(
									mContext.getResources(),
									BitmapFactory.decodeByteArray(byteArray, 0,
											byteArray.length));
							bitmap = BitmapFactory.decodeByteArray(byteArray,
									0, byteArray.length);

						}
					}
					Matrix matrix = new Matrix();
					matrix.postScale(0.5f, 0.5f);
					Flog.d(TAG, "scanAlbumImage---width=="+bitmap.getWidth()+"--height==="+bitmap.getHeight());
					CoverList.bitmap = Bitmap.createBitmap(bitmap, 0, 0,
							bitmap.getWidth(), bitmap.getHeight(), matrix, true);
					Flog.d(TAG, "scanAlbumImage-CoverList.bitmap--width=="+CoverList.bitmap.getWidth()+"--height==="+CoverList.bitmap.getHeight());
					
					
				} else {

					CoverList.cover = null;

				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}