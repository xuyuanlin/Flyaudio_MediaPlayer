package com.flyaudio.flyMediaPlayer.sdl;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.reflect.Method;

import com.flyaudio.flyMediaPlayer.until.FFT;
import com.flyaudio.flyMediaPlayer.until.Flog;

import android.app.*;
import android.content.*;
import android.view.*;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsoluteLayout;
import android.os.*;
import android.util.Log;
import android.graphics.*;
import android.media.*;
import android.hardware.*;

/**
 * SDL NativePlayer 必须有一下的方法： 1 getNativeSurface 2 filpBuffer 3 audioInit 4
 * audioWriteShortBuffer 5 audioWriteByteBuffer 6 pollInputDevices
 * 
 * 
 */

public class NativePlayer {
	private static final String TAG = "NativePlayer";
	private static ArrayList<short[]> fftList = null;
	public static final float pi = (float) 3.1415926;
	private static boolean isGetFFT = false;
	public static boolean mIsPaused, mIsSurfaceReady;
	public static boolean mExitCalledFromJava;
	protected static NativePlayer mSingleton;
	protected static SDLSurface mSurface;
	protected static SDLJoystickHandler mJoystickHandler;
	protected static AudioTrack mAudioTrack;

	public static native PlayerResult nativePlayerInit(String filename);

	public static native void nativePlayerStart();

	public static native void nativeLowMemory();

	public static native void nativePlayerPause();

	public static native void nativePlayerResume();

	public static native int nativePlayerSeekTo(double seek_pos);// 定位到某一秒播放（seek_pos单位：s）

	public static native void nativePlayerStop();

	public static native void nativeFlipBuffers();

	public static native String nativeGetHint(String name);

	// Load the .so
	static {// TODO
		Flog.d(TAG, "-----------------------------------------------.so");
		System.loadLibrary("SDL2");

		System.loadLibrary("ffmpeg");
		System.loadLibrary("ffmpegJNI");
		Flog.d(TAG,
				"-----------------------------------------------.so------------------end");
	}

	/**
	 * This method is called by SDL using JNI.
	 */
	public static void flipBuffers() {
		NativePlayer.nativeFlipBuffers();
	}

	/**
	 * This method is called by SDL using JNI.
	 */
	public static boolean setActivityTitle(String title) {
		// Called from SDLMain() thread and can't directly affect the view
		return false;// mSingleton.sendCommand(0, title);
	}

	/**
	 * This method is called by SDL using JNI.
	 */
	public static Surface getNativeSurface() {
		return NativePlayer.mSurface.getNativeSurface();
	}

	// Audio

	/**
	 * This method is called by SDL using JNI.
	 */
	public static int audioInit(int sampleRate, boolean is16Bit,
			boolean isStereo, int desiredFrames) {
		int channelConfig = isStereo ? AudioFormat.CHANNEL_CONFIGURATION_STEREO
				: AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int audioFormat = is16Bit ? AudioFormat.ENCODING_PCM_16BIT
				: AudioFormat.ENCODING_PCM_8BIT;
		int frameSize = (isStereo ? 2 : 1) * (is16Bit ? 2 : 1);

		Flog.d(TAG, "SDL audio: wanted " + (isStereo ? "stereo" : "mono") + " "
				+ (is16Bit ? "16-bit" : "8-bit") + " " + (sampleRate / 1000f)
				+ "kHz, " + desiredFrames + " frames buffer");

		// Let the user pick a larger buffer if they really want -- but ye
		// gods they probably shouldn't, the minimums are horrifyingly high
		// latency already
		desiredFrames = Math.max(
				desiredFrames,
				(AudioTrack.getMinBufferSize(sampleRate, channelConfig,
						audioFormat) + frameSize - 1)
						/ frameSize);

		if (mAudioTrack == null) {
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
					channelConfig, audioFormat, desiredFrames * frameSize,
					AudioTrack.MODE_STREAM, 111);
			if (mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
				Flog.d(TAG, "Failed during initialization of Audio Track");
				mAudioTrack = null;
				return -1;
			}
			if (fftList != null) {
				fftList.clear();
			} else {
				fftList = new ArrayList<short[]>(10);// lyl+
			}

			mAudioTrack.play();

		}

		Flog.d(TAG,
				"SDL audio: got "
						+ ((mAudioTrack.getChannelCount() >= 2) ? "stereo"
								: "mono")
						+ " "
						+ ((mAudioTrack.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) ? "16-bit"
								: "8-bit") + " "
						+ (mAudioTrack.getSampleRate() / 1000f) + "kHz, "
						+ desiredFrames + " frames buffer");

		return 0;
	}

	/**
	 * This method is called by SDL using JNI.
	 */
	public static void audioWriteShortBuffer(short[] buffer) {
		for (int i = 0; i < buffer.length;) {
			int result = mAudioTrack.write(buffer, i, buffer.length - i);

			if (isGetFFT) {
//				Flog.e(TAG, "audioWriteShortBuffer-----------isGetFFT=="+ isGetFFT);
				if (fftList != null) {
					try {
						int size = fftList.size();
//						Flog.i(TAG, "audioWriteShortBuffer-----------size=="+ size);
						if (size >= 8) {
							// fftList.clear();
							fftList.remove(size - 1);
						}
						fftList.add(buffer);
					} catch (IndexOutOfBoundsException e) {
						e.printStackTrace();
						Flog.d(TAG, "IndexOutOfBoundsException:fftList.size()= " + fftList.size());
						// fftList.clear();
					}
				}
			}
			if (result > 0) {
				i += result;
			} else if (result == 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// Nom nom
				}
			} else {
				Flog.e(TAG, "SDL audio: error return from write(short)");
				return;
			}
		}
	}

	/**
	 * This method is called by SDL using JNI.
	 */
	public static void audioWriteByteBuffer(byte[] buffer) {
		for (int i = 0; i < buffer.length;) {
			int result = mAudioTrack.write(buffer, i, buffer.length - i);
			Flog.d(TAG, "audioWriteByteBuffer-----------result==" + result);
			if (result > 0) {
				i += result;
			} else if (result == 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// Nom nom
				}
			} else {
				Flog.d(TAG, "SDL audio: error return from write(byte)");
				return;
			}
		}
	}

	/**
	 * This method is called by SDL using JNI.
	 */
	public static void audioQuit() {
		if (mAudioTrack != null) {
			mAudioTrack.stop();
			mAudioTrack.release();// +lyl
			if (fftList != null) {
				fftList.clear();
				fftList = null;
			}
			isGetFFT = false;
			mAudioTrack = null;
		}
	}

	public static float[] getFFTBuffer() {

		try {
			isGetFFT = true;
			if (fftList != null) {
				int size = fftList.size();
				if (size <= 0) {
					Flog.d(TAG, "---------------size==0");
					float[] outBuf = new float[2];
					for (int i = 0; i < 2; i++) {
						outBuf[i] = 0;
					}
					return outBuf;
				} else if (size > 0) {

					short[] fftBuffer = null;
					try {
						fftBuffer = fftList.get(size - 1);
						fftList.remove(size - 1);
					} catch (IndexOutOfBoundsException e) {
						e.printStackTrace();
						Flog.d(TAG, "IndexOutOfBoundsException:size= " + size);
						// fftList.clear();
						fftBuffer = null;
						return null;
					}
					if (fftBuffer != null) {
						int i, j;
						int out_len;
						int fft_len = fftBuffer.length;
						if (fft_len <= 0) {
							Flog.d(TAG, "fft_len= " + fft_len);
							fftBuffer = null;
							return null;
						}

						int length = up2int(fft_len);
						int len = length / 1024;
						if (len <= 1) {
							out_len = length;
						} else {
							out_len = 1024;
						}

						double[] buf = new double[out_len];
						for (i = 0; i < out_len; i++) {
							Short short1 = fftBuffer[i];
							buf[i] = short1.doubleValue();
							// Log.i("getFFTBuffer", "buf["+i+"]= "+buf[i]);
						}
						try {

							new FFT(buf);

							float[] outBuf = new float[out_len / 2];
							for (i = 0; i < out_len / 2; i++) {
								outBuf[i] = (float) buf[i];
								if (outBuf[i] <= 0) {
									// Log.e("getFFTBuffer",
									// "outBuf["+i+"]= "+outBuf[i]);
									return null;
								}
							}
							fftBuffer = null;
							return outBuf;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return null;
						}
					}
				}
			} else if (fftList == null) {
				// Log.i("getFFTBuffer", "fftList= " + fftList);
				float[] outBuf = new float[2];
				for (int i = 0; i < 2; i++) {
					outBuf[i] = 0;
				}
				return outBuf;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/**
	 * 向上取最接近iint的2的幂次数.比如iint=320时,返回256
	 * 
	 * @param iint
	 * @return
	 */
	private static int up2int(int iint) {
		int ret = 1;
		while (ret <= iint) {
			ret = ret << 1;
		}
		return ret >> 1;
	}

	// Input

	/**
	 * This method is called by SDL using JNI.
	 * 
	 * @return an array which may be empty but is never null.
	 */
	public static int[] inputGetInputDeviceIds(int sources) {
		int[] ids = InputDevice.getDeviceIds();
		int[] filtered = new int[ids.length];
		int used = 0;
		for (int i = 0; i < ids.length; ++i) {
			InputDevice device = InputDevice.getDevice(ids[i]);
			if ((device != null) && ((device.getSources() & sources) != 0)) {
				filtered[used++] = device.getId();
			}
		}
		return Arrays.copyOf(filtered, used);
	}

	// Joystick glue code, just a series of stubs that redirect to the
	// SDLJoystickHandler instance
	public static boolean handleJoystickMotionEvent(MotionEvent event) {
		return mJoystickHandler.handleMotionEvent(event);
	}

	/**
	 * This method is called by SDL using JNI.
	 */
	public static void pollInputDevices() {

	}

	// APK extension files support

	/** com.android.vending.expansion.zipfile.ZipResourceFile object or null. */
	private Object expansionFile;

	/**
	 * com.android.vending.expansion.zipfile.ZipResourceFile's getInputStream()
	 * or null.
	 */
	private Method expansionFileMethod;

	/**
	 * This method is called by SDL using JNI.
	 */
	public InputStream openAPKExtensionInputStream(String fileName)
			throws IOException {
		// Get a ZipResourceFile representing a merger of both the main and
		// patch files
		if (expansionFile == null) {
			Integer mainVersion = Integer
					.valueOf(nativeGetHint("SDL_ANDROID_APK_EXPANSION_MAIN_FILE_VERSION"));
			Integer patchVersion = Integer
					.valueOf(nativeGetHint("SDL_ANDROID_APK_EXPANSION_PATCH_FILE_VERSION"));

			try {
				// To avoid direct dependency on Google APK extension library
				// that is
				// not a part of Android SDK we access it using reflection
				expansionFile = Class
						.forName(
								"com.android.vending.expansion.zipfile.APKExpansionSupport")
						.getMethod("getAPKExpansionZipFile", Context.class,
								int.class, int.class)
						.invoke(null, this, mainVersion, patchVersion);

				expansionFileMethod = expansionFile.getClass().getMethod(
						"getInputStream", String.class);
			} catch (Exception ex) {
				ex.printStackTrace();
				expansionFile = null;
				expansionFileMethod = null;
			}
		}

		// Get an input stream for a known file inside the expansion file ZIPs
		InputStream fileStream;
		try {
			fileStream = (InputStream) expansionFileMethod.invoke(
					expansionFile, fileName);
		} catch (Exception ex) {
			ex.printStackTrace();
			fileStream = null;
		}

		if (fileStream == null) {
			throw new IOException();
		}

		return fileStream;
	}
}

/**
 * SDLSurface. This is what we draw on, so we need to know when it's created in
 * order to do anything useful.
 * 
 * Because of this, that's where we set up the SDL thread
 */
class SDLSurface extends SurfaceView implements SurfaceHolder.Callback {

	// Sensors
	protected static SensorManager mSensorManager;
	protected static Display mDisplay;

	// Keep track of the surface size to normalize touch events
	protected static float mWidth, mHeight;

	// Startup
	public SDLSurface(Context context) {
		super(context);
		getHolder().addCallback(this);
	}

	public void handleResume() {
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
	}

	public Surface getNativeSurface() {
		return getHolder().getSurface();
	}

	// Called when we have a valid drawing surface
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v("SDL", "surfaceCreated()");

	}

	// Called when we lose the surface
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v("SDL", "surfaceDestroyed()");

	}

	// Called when the surface is resized
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	// unused
	@Override
	public void onDraw(Canvas canvas) {
	}
}

/*
 * A null joystick handler for API level < 12 devices (the accelerometer is
 * handled separately)
 */
class SDLJoystickHandler {

	/**
	 * Handles given MotionEvent.
	 * 
	 * @param event
	 *            the event to be handled.
	 * @return if given event was processed.
	 */
	public boolean handleMotionEvent(MotionEvent event) {
		return false;
	}

	/**
	 * Handles adding and removing of input devices.
	 */
	public void pollInputDevices() {
	}
}
