package bappivi.baji.fivek;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import com.tam.media.FrameGrabber;

public class Util {
	@SuppressLint("SdCardPath")
	public final static String VIDEO_CONTENT = "/sdcard/nasa.mp4";

	public static long getVideoDuration(String path) {
		long videoDuration;
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(path);
		// 1000 micro er jonno gon kora hoyeche
		videoDuration = Long.parseLong(mmr
				.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000;
		mmr.release();
		return videoDuration;
	}

	public static Bitmap getFrameAtTimeByMMDR(String path, long time) {
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(path);
		Bitmap bmp = mmr.getFrameAtTime(time,
				MediaMetadataRetriever.OPTION_CLOSEST);
		mmr.release();
		return bmp;
	}

	public static Bitmap getFrameAtTimeByFrameGrabber(String path, long time) {
		FrameGrabber mFrameGrabber = null;
		mFrameGrabber = new FrameGrabber();
		mFrameGrabber.setDataSource(path);
		mFrameGrabber.setTargetSize(1280, 720);
		mFrameGrabber.init();
		return mFrameGrabber.getFrameAtTime(time);
	}

	public static void copyAssets(Context context) {
		AssetManager assetManager = context.getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
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
				e.printStackTrace();
			}
		}
	}

	public static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
}
