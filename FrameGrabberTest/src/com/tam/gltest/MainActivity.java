package com.tam.gltest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.tam.media.FrameGrabber;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private final static String TAG = "MainActivity";
	@SuppressLint("SdCardPath")
	private final static String VIDEO_CONTENT = "/sdcard/nasa.mp4";

	private FrameGrabber mFrameGrabber = null;
	private Button mButton = null;
	private ImageView mImageView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// UI Setup
		initUIControls();
	}

	private void initUIControls() {
		mButton = (Button) findViewById(R.id.button);
		mButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getFrames();
			}
		});

	}

	@SuppressLint("SdCardPath")
	private void getFrames() {
		File videoFile = new File(VIDEO_CONTENT);
		if (false == videoFile.exists()) {
			copyAssets();
		}

		boolean useMMDR = false;
		final ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
		String video = VIDEO_CONTENT;
		long currentFrameTime = 1000;
		int micro = 1000;
		long framePeriod = 1000 * micro;
		getFrameNumber(video);
		ProgressDialog ringProgressDialog = ProgressDialog.show(MainActivity.this, "Please wait ...",
				"Loading Image ...", false);
		ringProgressDialog.setCancelable(false);

		while (currentFrameTime < videoDuration - framePeriod) {
			currentFrameTime = currentFrameTime + framePeriod;
			Bitmap bmp = useMMDR ? getFrameAtTimeByMMDR(video, currentFrameTime)
					: getFrameAtTimeByFrameGrabber(video, currentFrameTime);
			imageItems.add(new ImageItem(bmp, currentFrameTime / 1000000 + " sec"));
		}

		if (ringProgressDialog.isShowing())
			ringProgressDialog.dismiss();

		GridView gridView = (GridView) findViewById(R.id.gridView);
		GridViewAdapter gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, imageItems);
		gridView.setAdapter(gridAdapter);

		if (null != mFrameGrabber)
			mFrameGrabber.release();
	}

	long videoDuration;

	private void getFrameNumber(String path) {
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(path);
		// 1000 micro er jonno gon kora hoyeche
		videoDuration = Long.parseLong(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;
		mmr.release();
	}

	private Bitmap getFrameAtTimeByMMDR(String path, long time) {
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(path);
		Bitmap bmp = mmr.getFrameAtTime(time, MediaMetadataRetriever.OPTION_CLOSEST);
		mmr.release();
		return bmp;
	}

	private Bitmap getFrameAtTimeByFrameGrabber(String path, long time) {
		mFrameGrabber = new FrameGrabber();
		mFrameGrabber.setDataSource(path);
		mFrameGrabber.setTargetSize(1280, 720);
		mFrameGrabber.init();
		return mFrameGrabber.getFrameAtTime(time);
	}

	private void copyAssets() {
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e(TAG, "Failed to get asset file list.", e);
		}
		for (String filename : files) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open(filename);
				File outFile = new File(VIDEO_CONTENT);
				out = new FileOutputStream(outFile);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				Log.e(TAG, "Failed to copy asset file: " + filename, e);
			}
		}
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
}
