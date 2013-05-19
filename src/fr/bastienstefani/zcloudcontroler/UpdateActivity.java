package fr.bastienstefani.zcloudcontroler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;

public class UpdateActivity extends Activity {

	private ProgressDialog pd;
	private PostCommand command;

	private static final int CONFIG_DONE = 0;
	private static final int ERROR_PASSWORD = 1;
	private static final int RULES_DONE = 2;
	private static final int ERROR_KEY = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		getConfigs();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update, menu);
		return true;
	}

	private void getConfigs() {
		command = new PostCommand(
				"https://z-cloud.z-wave.me/Config/Configuration/Get", this);

		pd = ProgressDialog.show(this, "Please wait...",
				"Downloading datas...", true, false);
		new Thread(new Runnable() {
			public void run() {
				Message msg = null;
				try {
					command.postData();
					String path = Environment.getExternalStorageDirectory()
							+ "/Zwave/Configuration.xml";
					FileIO.writeToFile(path, command.getAnswer());
					msg = handler.obtainMessage(CONFIG_DONE);
				} catch (KeyManagementException e) {
					msg = handler.obtainMessage(ERROR_KEY);
				} catch (UnrecoverableKeyException e) {
					e.printStackTrace();
				} catch (KeyStoreException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (CertificateException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	private void getRules() {
		command = new PostCommand("https://z-cloud.z-wave.me/Config/Rules/Get",
				this);

		new Thread(new Runnable() {
			public void run() {
				Message msg = null;
				try {
					command.postData();
					String path = Environment.getExternalStorageDirectory()
							+ "/Zwave/Rules.xml";
					FileIO.writeToFile(path, command.getAnswer());
					msg = handler.obtainMessage(RULES_DONE);
				} catch (KeyManagementException e) {
					msg = handler.obtainMessage(ERROR_KEY);
				} catch (UnrecoverableKeyException e) {
					e.printStackTrace();
				} catch (KeyStoreException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (CertificateException e) {
					e.printStackTrace();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					handler.sendMessage(msg);
				}
			}
		}).start();
	}

	private final Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CONFIG_DONE:
				getRules();
				break;
			case RULES_DONE:
				pd.dismiss();
				finish();
				break;
			case ERROR_KEY:
				Log.v("Handler", "ERROR KEYMANAGEMENT EXCEPTION");
				break;
			}
		}
	};
}