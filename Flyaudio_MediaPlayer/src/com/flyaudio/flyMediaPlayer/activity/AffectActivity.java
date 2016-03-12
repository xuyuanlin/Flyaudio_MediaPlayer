package com.flyaudio.flyMediaPlayer.activity;

import java.util.Formatter;
import java.util.Locale;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.AudioEffect.Descriptor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.flyaudio.flyMediaPlayer.adapter.EffectAdapter;
import com.flyaudio.flyMediaPlayer.seekbar.SeekBar;
import com.flyaudio.flyMediaPlayer.seekbar.SeekBar.OnSeekBarChangeListener;
import com.flyaudio.flyMediaPlayer.until.AllListActivity;
import com.flyaudio.flyMediaPlayer.until.Constant;
import com.flyaudio.flyMediaPlayer.until.ControlPanelEffect;
import com.flyaudio.flyMediaPlayer.until.Flog;
import com.flyAudio.flyMediaPlayer.R;

/*
 * 
 * 音效的实现类，主要是参考android4.4的源码的/home/xyz/msm8226/packages/apps/MusicFX实现的。
 * 其中为了在切歌的时候保存上一次的音效，在实现的过程中让 CompoundButton开关关闭再打开。
 * 
 * 
 */
public class AffectActivity extends Activity implements OnSeekBarChangeListener {
	private static final String TAG = "AffectActivity";

	/**
	 * Indicates if Virtualizer effect is supported.
	 */
	private boolean mVirtualizerSupported;
	private boolean mVirtualizerIsHeadphoneOnly;
	/**
	 * Indicates if BassBoost effect is supported.
	 */
	private boolean mBassBoostSupported;
	/**
	 * Indicates if Equalizer effect is supported.
	 */
	private boolean mEqualizerSupported;
	/**
	 * Indicates if Preset Reverb effect is supported.
	 */
	private boolean mPresetReverbSupported;
	private SharedPreferences preferences;
	// Equalizer fields
	private final SeekBar[] mEqualizerSeekBar = new SeekBar[Constant.EQUALIZER_MAX_BANDS];
	private int mNumberEqualizerBands;
	private int mEqualizerMinBandLevel;
	private int mEQPresetUserPos = 1;
	private int mEQPreset;
	private int mEQPresetPrevious;
	private int[] mEQPresetUserBandLevelsPrev;
	private String[] mEQPresetNames;
	private int skinId;// 背影ID
	private int mPRPreset;
	private int mPRPresetPrevious;
	private RelativeLayout mlayout;
	private boolean mIsHeadsetOn = false;
	public static CompoundButton mToggleSwitch;
	private StringBuilder mFormatBuilder = new StringBuilder();
	private Formatter mFormatter = new Formatter(mFormatBuilder,
			Locale.getDefault());
	public static boolean isAffectActivity;

	/**
	 * Mapping for the EQ widget ids per band
	 */

	// Preset Reverb fields
	/**
	 * Array containing the PR preset names.
	 */

	/**
	 * Context field
	 */
	private Context mContext;

	/**
	 * Calling package name field
	 */
	private String mCallingPackageName = "empty";

	/**
	 * Audio session field
	 */

	private int mAudioSession = AudioEffect.ERROR_BAD_VALUE;
	private ListView mlistView;
	// Broadcast receiver to handle wired and Bluetooth A2dp headset events
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			Flog.d(TAG, "BroadcastReceiver------------begin");
			final String action = intent.getAction();
			final boolean isHeadsetOnPrev = mIsHeadsetOn;
			final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			if (action.equals(Intent.ACTION_HEADSET_PLUG)) {// 插拔耳麦广播
				Flog.d(TAG,
						"AffectActivity----BroadcastReceiver------ACTION_HEADSET_PLUG");
				mIsHeadsetOn = (intent.getIntExtra("state", 0) == 1)
						|| audioManager.isBluetoothA2dpOn();
			} else if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {// 广播活动：指明一个与远程设备建立的低级别
				Flog.d(TAG,
						"AffectActivity----BroadcastReceiver------ACTION_ACL_CONNECTED");
				final int deviceClass = ((BluetoothDevice) intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE))
						.getBluetoothClass().getDeviceClass();
				if ((deviceClass == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES)
						|| (deviceClass == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET)) {
					Flog.d(TAG,
							"AffectActivity----BroadcastReceiver------AUDIO_VIDEO_WEARABLE_HEADSET");
					mIsHeadsetOn = true;
				}
			} else if (action.equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
				Flog.d(TAG,
						"AffectActivity----BroadcastReceiver------ACTION_AUDIO_BECOMING_NOISY");
				mIsHeadsetOn = audioManager.isBluetoothA2dpOn()
						|| audioManager.isWiredHeadsetOn();
			} else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
				final int deviceClass = ((BluetoothDevice) intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE))
						.getBluetoothClass().getDeviceClass();
				if ((deviceClass == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES)
						|| (deviceClass == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET)) {
					Flog.d(TAG,
							"AffectActivity----BroadcastReceiver------AUDIO_VIDEO_HEADPHONES");
					mIsHeadsetOn = audioManager.isWiredHeadsetOn();
				}
			}
			if (isHeadsetOnPrev != mIsHeadsetOn) {
				Flog.d(TAG,
						"AffectActivity----BroadcastReceiver------mIsHeadsetOn");
				// updateUIHeadset();
			}
			Flog.d(TAG, "BroadcastReceiver------------end");
		}
	};

	private AllListActivity mAllListActivity;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		Flog.d(TAG, "onCreate()-------------begin");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.music_affect);
		mAllListActivity = (AllListActivity) getApplication();
		AllListActivity.getInstance().addActivity(this);
		preferences = getSharedPreferences(Constant.PREFERENCES_NAME,
				Context.MODE_PRIVATE);

		final ViewGroup viewGroup = (ViewGroup) findViewById(R.id.contentSoundEffects);
		// Check if wired or Bluetooth headset is connected/on
		final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mIsHeadsetOn = (audioManager.isWiredHeadsetOn() || audioManager
				.isBluetoothA2dpOn());
		Flog.d(TAG, "onResume: mIsHeadsetOn : " + mIsHeadsetOn);
		Flog.d(TAG, "AffectActivity----onCreate()");
		Flog.d(TAG, "AffectActivity----onCreate()--------"
				+ AudioEffect.ERROR_BAD_VALUE);

		// Init context to be used in listeners
		Flog.d(TAG, "AffectActivity----onCreate()--------1");
		mContext = this;
		final Intent intent = getIntent();
		Flog.d(TAG, "AffectActivity----onCreate()-----------2");
		mAudioSession = 111;
		Flog.d(TAG, "AffectActivity----onCreate()----session----"
				+ mAudioSession);
		mCallingPackageName = "com.flyaudio.flyMediaPlayer";
		Flog.d(TAG, "AffectActivity----onCreate()----------3");
		setResult(RESULT_OK);
		ControlPanelEffect.initEffectsPreferences(mContext,
				mCallingPackageName, mAudioSession);
		Flog.d(TAG, "AffectActivity----onCreate()------------4");
		// query available effects
		final Descriptor[] effects = AudioEffect.queryEffects();
		Flog.d(TAG, "AffectActivity----onCreate()----------------5");
		// Determine available/supported effects
		Flog.d(TAG, "Available effects:");
		for (final Descriptor effect : effects) {
			Flog.d(TAG,
					effect.name.toString() + ", type: "
							+ effect.type.toString());
			Flog.d(TAG,
					"AffectActivity----------111111111111111111111111111111111111111111111111");
			if (effect.type.equals(AudioEffect.EFFECT_TYPE_VIRTUALIZER)) {
				Flog.d(TAG, "AffectActivity----------222");
				mVirtualizerSupported = true;

				if (effect.uuid.equals(UUID
						.fromString("1d4033c0-8557-11df-9f2d-0002a5d5c51b"))
						|| effect.uuid
								.equals(UUID
										.fromString("e6c98a16-22a3-11e2-b87b-f23c91aec05e"))
						|| effect.uuid
								.equals(UUID
										.fromString("d3467faa-acc7-4d34-acaf-0002a5d5c51b"))) {
					Flog.d(TAG, "AffectActivity----------333");
					mVirtualizerIsHeadphoneOnly = true;
				}
			} else if (effect.type.equals(AudioEffect.EFFECT_TYPE_BASS_BOOST)) {
				Flog.d(TAG, "AffectActivity----------444-----------"
						+ AudioEffect.EFFECT_TYPE_BASS_BOOST);
				mBassBoostSupported = true;
			} else if (effect.type.equals(AudioEffect.EFFECT_TYPE_EQUALIZER)) {
				Flog.d(TAG, "AffectActivity----------555---------------"
						+ AudioEffect.EFFECT_TYPE_EQUALIZER);
				mEqualizerSupported = true;
			} else if (effect.type
					.equals(AudioEffect.EFFECT_TYPE_PRESET_REVERB)) {
				Flog.d(TAG, "AffectActivity----------666----------"
						+ AudioEffect.EFFECT_TYPE_PRESET_REVERB);
				mPresetReverbSupported = true;
			}
		}
		Flog.d(TAG, "AffectActivity----------" + mVirtualizerSupported);
		Flog.d(TAG, "AffectActivity----------" + mBassBoostSupported);
		Flog.d(TAG, "AffectActivity----------" + mEqualizerSupported);
		Flog.d(TAG, "AffectActivity----------" + mPresetReverbSupported);
		Flog.d(TAG, "AffectActivity----------" + mVirtualizerIsHeadphoneOnly);

		// Fill array with presets from AudioEffects call.
		// allocate a space for 2 extra strings (CI Extreme & User)
		final int numPresets = ControlPanelEffect.getParameterInt(mContext,
				mCallingPackageName, mAudioSession,
				ControlPanelEffect.Key.eq_num_presets);
//		String namePresetString=ControlPanelEffect.getParameterString(mContext,
//				mCallingPackageName, mAudioSession,
//				ControlPanelEffect.Key.eq_preset_name);
//		Flog.d(TAG, "AffectActivity-----namePresetString.length()==" + namePresetString.length());
		Flog.d(TAG, "AffectActivity-----numPresets==" + numPresets);
		mEQPresetNames = new String[numPresets + 2];
		for (short i = 0; i < numPresets; i++) {
			mEQPresetNames[i] = ControlPanelEffect.getParameterString(mContext,
					mCallingPackageName, mAudioSession,
					ControlPanelEffect.Key.eq_preset_name, i);
		}
		mEQPresetNames[numPresets] = getString(R.string.ci_extreme);
		mEQPresetNames[numPresets + 1] = getString(R.string.user);
		mEQPresetUserPos = numPresets + 1;

		// Watch for button clicks and initialization.
		if ((mVirtualizerSupported) || (mBassBoostSupported)
				|| (mEqualizerSupported) || (mPresetReverbSupported)) {
			// Set the listener for the main enhancements toggle button.
			// Depending on the state enable the supported effects if they were
			// checked in the setup tab.
			mToggleSwitch = new Switch(this);
			mToggleSwitch
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(
								final CompoundButton buttonView,
								final boolean isChecked) {
							Flog.d(TAG, "AffectActivity-----mToggleSwitch-----"
									+ isChecked);
							Flog.d(TAG,
									"AffectActivity-----mToggleSwitch-----mAudioSession---"
											+ mAudioSession);
							// set parameter and state
							ControlPanelEffect.setParameterBoolean(mContext,
									mCallingPackageName, mAudioSession,
									ControlPanelEffect.Key.global_enabled,
									isChecked);
							// Enable Linear layout (in scroll layout) view with
							// all
							// effect contents depending on checked state
							setEnabledAllChildren(viewGroup, isChecked);
							// update UI according to headset state
							// updateUIHeadset();

							Flog.d(TAG,
									"AffectActivity-----mToggleSwitch-----end");
						}
					});

			// Initialize the Equalizer elements.
			if (mEqualizerSupported) {
				mEQPreset = ControlPanelEffect.getParameterInt(mContext,
						mCallingPackageName, mAudioSession,
						ControlPanelEffect.Key.eq_current_preset);
				if (mEQPreset >= mEQPresetNames.length) {
					mEQPreset = 0;
				}
				mEQPresetPrevious = mEQPreset;
				mlistView = (ListView) findViewById(R.id.affectFormat);
				EffectAdapter mAdapter = new EffectAdapter(this, mEQPresetNames);
				mlistView.setAdapter(mAdapter);
				mlistView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View v,
							int position, long id) {
						// TODO Auto-generated method stub
						if (position != mEQPresetPrevious) {
							equalizerSetPreset(position);
						}
						mEQPresetPrevious = position;

					}
				});
				// equalizerSpinnerInit((Spinner) findViewById(R.id.eqSpinner));
				equalizerBandsInit(findViewById(R.id.eqcontainer));
			}

			// Initialize the Preset Reverb elements.
			// Set Spinner listeners.
			if (mPresetReverbSupported) {
				mPRPreset = ControlPanelEffect.getParameterInt(mContext,
						mCallingPackageName, mAudioSession,
						ControlPanelEffect.Key.pr_current_preset);
				mPRPresetPrevious = mPRPreset;
				// reverbSpinnerInit((Spinner) findViewById(R.id.prSpinner));
			}

		} else {
			viewGroup.setVisibility(View.GONE);
			((TextView) findViewById(R.id.noEffectsTextView))
					.setVisibility(View.VISIBLE);
		}
		ActionBar ab = getActionBar();
		final int padding = getResources().getDimensionPixelSize(
				R.dimen.action_bar_switch_padding);
		mToggleSwitch.setPadding(0, 0, padding, 0);
		ab.setCustomView(mToggleSwitch, new ActionBar.LayoutParams(
				ActionBar.LayoutParams.WRAP_CONTENT,
				ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL
						| Gravity.RIGHT));
		ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
				| ActionBar.DISPLAY_SHOW_CUSTOM);
		TextView finishText = (TextView) findViewById(R.id.AffectActivity_finsh);
		finishText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Flog.d(TAG, "onCreate()------------finishText");
				finish();
			}
		});
		Flog.d(TAG, "onCreate()-------------end");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {

		super.onResume();
		isAffectActivity = true;
		Flog.d(TAG, "AffectActivity-------onResume()----begin");

		Flog.d(TAG, "AffectActivity-------1");
		if ((mVirtualizerSupported) || (mBassBoostSupported)
				|| (mEqualizerSupported) || (mPresetReverbSupported)) {
			// Listen for broadcast intents that might affect the onscreen UI
			// for headset.
			Flog.d(TAG, "AffectActivity-------2");
			final IntentFilter intentFilter = new IntentFilter(
					Intent.ACTION_HEADSET_PLUG);
			intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
			intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
			intentFilter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
			registerReceiver(mReceiver, intentFilter);
			// Update UI
			updateUI();
		}
		Flog.d(TAG, "AffectActivity-------onResume()----end");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Flog.d(TAG, "AffectActivity----------onPause()---begin");
		// Unregister for broadcast intents. (These affect the visible UI,
		// so we only care about them while we're in the foreground.)
		unregisterReceiver(mReceiver);
		Flog.d(TAG, "AffectActivity----------onPause()---end");
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		Flog.d(TAG, "AffectActivity----------finish()----start");
		if (mToggleSwitch != null) {
			preferences.edit()
					.putBoolean("mToggleSwitch", mToggleSwitch.isChecked())
					.commit();
		}

		Flog.d(TAG, "AffectActivity----------finish()----end");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Flog.d(TAG, "AffectActivity------------onDestroy()---end");
	}

	private void reverbSpinnerInit(Spinner spinner) {
		Flog.d(TAG, "reverbSpinnerInit()-----begin");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				Constant.PRESETREVERBPRESETSTRINGS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position != mPRPresetPrevious) {
					presetReverbSetPreset(position);
				}
				mPRPresetPrevious = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spinner.setSelection(mPRPreset);
		Flog.d(TAG, "reverbSpinnerInit()-----end");
	}

	private void equalizerSpinnerInit(Spinner spinner) {
		Flog.d(TAG, "equalizerSpinnerInit()-----begin");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mEQPresetNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position != mEQPresetPrevious) {
					equalizerSetPreset(position);
				}
				mEQPresetPrevious = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		spinner.setSelection(mEQPreset);
		Flog.d(TAG, "equalizerSpinnerInit()-----end");
	}

	/**
	 * En/disables all children for a given view. For linear and relative layout
	 * children do this recursively
	 * 
	 * @param viewGroup
	 * @param enabled
	 */
	private void setEnabledAllChildren(final ViewGroup viewGroup,
			final boolean enabled) {
		Flog.d(TAG, "AffectActivity---setEnabledAllChildren()---start");
		Flog.d(TAG, "AffectActivity---setEnabledAllChildren()---" + enabled);
		final int count = viewGroup.getChildCount();
		for (int i = 0; i < count; i++) {
			Flog.d(TAG, "AffectActivity---setEnabledAllChildren()---" + count);
			final View view = viewGroup.getChildAt(i);
			if ((view instanceof LinearLayout)
					|| (view instanceof RelativeLayout)) {
				final ViewGroup vg = (ViewGroup) view;
				setEnabledAllChildren(vg, enabled);
			}
			view.setEnabled(enabled);
		}
		Flog.d(TAG, "AffectActivity---setEnabledAllChildren()---end");
	}

	/**
	 * Updates UI (checkbox, seekbars, enabled states) according to the current
	 * stored preferences.
	 */
	private void updateUI() {
		Flog.d(TAG, "AffectActivity---updateUI()---start");
		final boolean isEnabled = ControlPanelEffect.getParameterBoolean(
				mContext, mCallingPackageName, mAudioSession,
				ControlPanelEffect.Key.global_enabled);
		Flog.d(TAG, "AffectActivity---updateUI()---" + isEnabled);
		Flog.d(TAG, "AffectActivity---updateUI()---mAudioSession----"
				+ mAudioSession);
		mToggleSwitch.setChecked(isEnabled);
		setEnabledAllChildren(
				(ViewGroup) findViewById(R.id.contentSoundEffects), isEnabled);
		if (mEqualizerSupported) {
			Flog.d(TAG, "AffectActivity---updateUI()---if4");
			equalizerUpdateDisplay();
		}
		if (mPresetReverbSupported) {
			Flog.d(TAG, "AffectActivity---updateUI()---if5");
			int reverb = ControlPanelEffect.getParameterInt(mContext,
					mCallingPackageName, mAudioSession,
					ControlPanelEffect.Key.pr_current_preset);
			// ((Spinner) findViewById(R.id.prSpinner)).setSelection(reverb);
		}
		Flog.d(TAG, "AffectActivity---updateUI()---end");
	}

	/**
	 * Initializes the equalizer elements. Set the SeekBars and Spinner
	 * listeners.
	 */
	private void equalizerBandsInit(View eqcontainer) {
		Flog.d(TAG, "equalizerBandsInit()-----begin");
		// Initialize the N-Band Equalizer elements.
		mNumberEqualizerBands = ControlPanelEffect.getParameterInt(mContext,
				mCallingPackageName, mAudioSession,
				ControlPanelEffect.Key.eq_num_bands);
		mEQPresetUserBandLevelsPrev = ControlPanelEffect.getParameterIntArray(
				mContext, mCallingPackageName, mAudioSession,
				ControlPanelEffect.Key.eq_preset_user_band_level);
		final int[] centerFreqs = ControlPanelEffect.getParameterIntArray(
				mContext, mCallingPackageName, mAudioSession,
				ControlPanelEffect.Key.eq_center_freq);
		final int[] bandLevelRange = ControlPanelEffect.getParameterIntArray(
				mContext, mCallingPackageName, mAudioSession,
				ControlPanelEffect.Key.eq_level_range);
		mEqualizerMinBandLevel = bandLevelRange[0];
		final int mEqualizerMaxBandLevel = bandLevelRange[1];

		for (int band = 0; band < mNumberEqualizerBands; band++) {
			// Unit conversion from mHz to Hz and use k prefix if necessary to
			// display
			final int centerFreq = centerFreqs[band] / 1000;
			float centerFreqHz = centerFreq;
			String unitPrefix = "";
			if (centerFreqHz >= 1000) {
				centerFreqHz = centerFreqHz / 1000;
				unitPrefix = "k";
			}
			((TextView) eqcontainer
					.findViewById(Constant.EQViewElementIds[band][0]))
					.setText(format("%.0f ", centerFreqHz) + unitPrefix + "Hz");
			mEqualizerSeekBar[band] = (SeekBar) eqcontainer
					.findViewById(Constant.EQViewElementIds[band][1]);
			mEqualizerSeekBar[band].setMax(mEqualizerMaxBandLevel
					- mEqualizerMinBandLevel);
			mEqualizerSeekBar[band].setOnSeekBarChangeListener(this);
		}

		// Hide the inactive Equalizer bands.
//		for (int band = mNumberEqualizerBands; band < Constant.EQUALIZER_MAX_BANDS; band++) {
//			// CenterFreq text
//			eqcontainer.findViewById(Constant.EQViewElementIds[band][0])
//					.setVisibility(View.GONE);
//			// SeekBar
//			eqcontainer.findViewById(Constant.EQViewElementIds[band][1])
//					.setVisibility(View.GONE);
//		}

		// TODO: get the actual values from somewhere
		TextView tv = (TextView) findViewById(R.id.maxLevelText);
		tv.setText("+15 dB");
		tv = (TextView) findViewById(R.id.centerLevelText);
		tv.setText("0 dB");
		tv = (TextView) findViewById(R.id.minLevelText);
		tv.setText("-15 dB");
		equalizerUpdateDisplay();
		Flog.d(TAG, "equalizerBandsInit()-----end");
	}

	private String format(String format, Object... args) {
		Flog.d(TAG, "format()-----begin");
		mFormatBuilder.setLength(0);
		mFormatter.format(format, args);
		Flog.d(TAG, "format()-----end");
		return mFormatBuilder.toString();
	}

	@Override
	public void onProgressChanged(final SeekBar seekbar, final int progress,
			final boolean fromUser) {
		Flog.d(TAG, "onProgressChanged()-----begin");
		final int id = seekbar.getId();

		for (short band = 0; band < mNumberEqualizerBands; band++) {
			if (id == Constant.EQViewElementIds[band][1]) {
				final short level = (short) (progress + mEqualizerMinBandLevel);
				if (fromUser) {
					equalizerBandUpdate(band, level);
				}
				break;
			}
		}
		Flog.d(TAG, "onProgressChanged()-----end");
	}

	@Override
	public void onStartTrackingTouch(final SeekBar seekbar) {
		Flog.d(TAG, "onStartTrackingTouch()-----begin");
		// get current levels
		final int[] bandLevels = ControlPanelEffect.getParameterIntArray(
				mContext, mCallingPackageName, mAudioSession,
				ControlPanelEffect.Key.eq_band_level);
		// copy current levels to user preset
		for (short band = 0; band < mNumberEqualizerBands; band++) {
			equalizerBandUpdate(band, bandLevels[band]);
		}
		equalizerSetPreset(mEQPresetUserPos);
		mlistView = (ListView) findViewById(R.id.affectFormat);
		// ((Spinner)
		// findViewById(R.id.eqSpinner)).setSelection(mEQPresetUserPos);
		Flog.d(TAG, "onStartTrackingTouch()-----end");
	}

	@Override
	public void onStopTrackingTouch(final SeekBar seekbar) {
		Flog.d(TAG, "AffectActivity---onStopTrackingTouch()---start");
		equalizerUpdateDisplay();
		Flog.d(TAG, "AffectActivity---onStopTrackingTouch()---end");
	}

	/**
	 * Updates the EQ by getting the parameters.
	 */
	private void equalizerUpdateDisplay() {
		// Update and show the active N-Band Equalizer bands.
		Flog.d(TAG, "AffectActivity---equalizerUpdateDisplay()---start");
		final int[] bandLevels = ControlPanelEffect.getParameterIntArray(
				mContext, mCallingPackageName, mAudioSession,
				ControlPanelEffect.Key.eq_band_level);
		for (short band = 0; band < mNumberEqualizerBands; band++) {
			final int level = bandLevels[band];
			final int progress = level - mEqualizerMinBandLevel;
			mEqualizerSeekBar[band].setProgress(progress);
		}
		Flog.d(TAG, "AffectActivity---equalizerUpdateDisplay()---end");
	}

	/**
	 * Updates/sets a given EQ band level.
	 * 
	 * @param band
	 *            Band id
	 * @param level
	 *            EQ band level
	 */
	private void equalizerBandUpdate(final int band, final int level) {
		Flog.d(TAG, "AffectActivity---equalizerBandUpdate()---start");
		ControlPanelEffect.setParameterInt(mContext, mCallingPackageName,
				mAudioSession, ControlPanelEffect.Key.eq_band_level, level,
				band);
		Flog.d(TAG, "AffectActivity---equalizerBandUpdate()---end");
	}

	/**
	 * Sets the given EQ preset.
	 * 
	 * @param preset
	 *            EQ preset id.
	 */
	private void equalizerSetPreset(final int preset) {
		Flog.d(TAG, "AffectActivity---equalizerSetPreset()---start");
		ControlPanelEffect
				.setParameterInt(mContext, mCallingPackageName, mAudioSession,
						ControlPanelEffect.Key.eq_current_preset, preset);
		equalizerUpdateDisplay();
		Flog.d(TAG, "AffectActivity---equalizerSetPreset()---end");
	}

	/**
	 * Sets the given PR preset.
	 * 
	 * @param preset
	 *            PR preset id.
	 */
	private void presetReverbSetPreset(final int preset) {
		Flog.d(TAG, "AffectActivity---presetReverbSetPreset()---start");
		ControlPanelEffect
				.setParameterInt(mContext, mCallingPackageName, mAudioSession,
						ControlPanelEffect.Key.pr_current_preset, preset);
		Flog.d(TAG, "AffectActivity---presetReverbSetPreset()---end");
	}

	/**
	 * Show msg that headset needs to be plugged.
	 */
	private void showHeadsetMsg() {
		Flog.d(TAG, "AffectActivity---showHeadsetMsg()---start");
		final Context context = getApplicationContext();
		final int duration = Toast.LENGTH_SHORT;

		final Toast toast = Toast.makeText(context,
				getString(R.string.headset_plug), duration);
		toast.setGravity(Gravity.CENTER, toast.getXOffset() / 2,
				toast.getYOffset() / 2);
		toast.show();
		Flog.d(TAG, "AffectActivity---showHeadsetMsg()---end");
	}

}
