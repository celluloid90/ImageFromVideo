package bappivi.baji.fivek;

import java.io.File;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import bappivi.baji.fivek.R;

public class MainActivity extends Activity implements OnTaskCompleted {

	private final static String TAG = "MainActivity";
	private Button mButton = null;

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
		File videoFile = new File(Util.VIDEO_CONTENT);
		if (false == videoFile.exists()) {
			Util.copyAssets(MainActivity.this);
		}
		new CollectBitmapAsyncTask(this, MainActivity.this).execute();

		// if (null != mFrameGrabber)
		// mFrameGrabber.release();
	}

	@Override
	public void onFrameExtractCompleted(ArrayList<ImageItem> allExtractedImages) {
		GridView gridView = (GridView) findViewById(R.id.gridView);
		GridViewAdapter gridAdapter = new GridViewAdapter(this,
				R.layout.grid_item_layout, allExtractedImages);
		gridView.setAdapter(gridAdapter);
	}

}
