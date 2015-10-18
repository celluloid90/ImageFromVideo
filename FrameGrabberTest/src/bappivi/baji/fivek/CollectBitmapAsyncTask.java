package bappivi.baji.fivek;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class CollectBitmapAsyncTask extends
		AsyncTask<String, String, ArrayList<ImageItem>> {

	private ProgressDialog mRingProgressDialog;
	private Context mContext;
	private OnTaskCompleted mTaskCompletedListener;

	public CollectBitmapAsyncTask(Context applicationContext,
			OnTaskCompleted taskCompletedListener) {
		this.mContext = applicationContext;
		this.mTaskCompletedListener = taskCompletedListener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mRingProgressDialog = ProgressDialog.show(mContext, "Please wait ...",
				"Loading Image ...", false);
		mRingProgressDialog.setCancelable(false);
	}

	@Override
	protected ArrayList<ImageItem> doInBackground(String... params) {
		boolean useMMDR = false;
		final ArrayList<ImageItem> imageItems = new ArrayList<ImageItem>();
		String video = Util.VIDEO_CONTENT;
		long currentFrameTime = 1000;
		int micro = 1000;
		long framePeriod = 1000 * micro;

		while (currentFrameTime < Util.getVideoDuration(video) - framePeriod) {
			currentFrameTime = currentFrameTime + framePeriod;
			Bitmap bmp = useMMDR ? Util.getFrameAtTimeByMMDR(video,
					currentFrameTime) : Util.getFrameAtTimeByFrameGrabber(
					video, currentFrameTime);
			imageItems.add(new ImageItem(bmp, currentFrameTime / 1000000
					+ " sec"));
		}
		return imageItems;
	}

	@Override
	protected void onPostExecute(ArrayList<ImageItem> result) {
		super.onPostExecute(result);
		if (mRingProgressDialog.isShowing())
			mRingProgressDialog.dismiss();
		mTaskCompletedListener.onFrameExtractCompleted(result);
	}

}
