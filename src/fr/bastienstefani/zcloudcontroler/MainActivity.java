package fr.bastienstefani.zcloudcontroler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	private final static int ACTIVITY_LOGIN = 0;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		File zwaveDirectory = new File(
				Environment.getExternalStorageDirectory(), "/Zwave/");
		if (!zwaveDirectory.exists()) {
			if (!zwaveDirectory.mkdirs()) {
				Log.e("TravellerLog :: ", "Problem creating folder");
			}
		}
		String loginPath = Environment.getExternalStorageDirectory()
				+ "/Zwave/login.xml";
		try {
			FileIO.readFromFile(loginPath);
		} catch (FileNotFoundException e) {
			Intent logIntent = new Intent(this, LoginActivity.class);
			this.startActivityForResult(logIntent, ACTIVITY_LOGIN);
		}
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_account:
			Intent logIntent = new Intent(this, LoginActivity.class);
			this.startActivityForResult(logIntent, ACTIVITY_LOGIN);
			return true;
		case R.id.menu_update:
			Intent updateIntent = new Intent(this, UpdateActivity.class);
			this.startActivity(updateIntent);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ACTIVITY_LOGIN) {
			Intent updateIntent = new Intent(this, UpdateActivity.class);
			this.startActivity(updateIntent);
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.

			Fragment fragment = null;
			Bundle args = null;
			switch (position) {
			case 0:
				fragment = new SceneFragment();
				args = new Bundle();
				args.putInt(SceneFragment.ARG_SECTION_NUMBER, position + 1);
				fragment.setArguments(args);
				return fragment;
			case 1:
				fragment = new InterrupteurFragment();
				args = new Bundle();
				args.putInt(InterrupteurFragment.ARG_SECTION_NUMBER,
						position + 1);
				fragment.setArguments(args);
				return fragment;
			default:
				fragment = new InterrupteurFragment();
				args = new Bundle();
				args.putInt(InterrupteurFragment.ARG_SECTION_NUMBER,
						position + 1);
				fragment.setArguments(args);
				return fragment;
			}
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			return rootView;
		}
	}

	public static class InterrupteurFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		ArrayList<Actioneur> actioneurs;

		public InterrupteurFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			final ListView lv = new ListView(getActivity());
			actioneurs = XMLparser.getActioneurs();
			ArrayList<String> names = new ArrayList<String>();
			for (int i = 0; i < actioneurs.size(); i++) {
				names.add(actioneurs.get(i).getName());
			}

			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
					getActivity(), R.layout.list_item, R.id.name, names);
			lv.setAdapter(arrayAdapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View view,
						int pos, long id) {
					final Dialog dialog = new Dialog(getActivity());
					dialog.setContentView(R.layout.dialog);
					dialog.setTitle("Actions");

					String item = lv.getItemAtPosition(pos).toString();
					final int idActioneur = getActioneurByName(item);
					Button openButton = (Button) dialog.findViewById(R.id.open);
					openButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							ExecActioneur(idActioneur, 255);
						}
					});
					Button closeButton = (Button) dialog
							.findViewById(R.id.close);
					closeButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							ExecActioneur(idActioneur, 0);
						}
					});
					Button cancelButton = (Button) dialog
							.findViewById(R.id.cancel);
					cancelButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

					dialog.show();
				}
			});
			return lv;
		}

		private int getActioneurByName(String name) {
			for (int i = 0; i < actioneurs.size(); i++) {
				if (actioneurs.get(i).getName().equals(name))
					return actioneurs.get(i).getId();
			}
			return -1;
		}

		private void ExecActioneur(int id, int value) {
			final PostCommand command = new PostCommand(
					"https://z-cloud.z-wave.me/ZWaveAPI/Run/devices%5B"
							+ id
							+ "%5D.instances%5B0%5D.commandClasses%5B37%5D.Set%28"
							+ value + "%29", getActivity());

			new Thread(new Runnable() {
				public void run() {
					try {
						command.postData();
					} catch (KeyManagementException e) {
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
					}
				}
			}).start();
		}
	}

	public static class SceneFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		private ArrayList<Scene> scenes;

		public SceneFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			final ListView lv = new ListView(getActivity());
			scenes = XMLparser.getScenes();
			ArrayList<String> names = new ArrayList<String>();
			for (int i = 0; i < scenes.size(); i++) {
				if (scenes.get(i).getName().length() > 25) {
					String name = replaceLast(scenes.get(i).getName(), " ", "\n");
					scenes.get(i).setName(name);
				} 
				names.add(scenes.get(i).getName());
				Log.v("Scenes", scenes.get(i).getName());
			}

			ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
					getActivity(), R.layout.list_item, R.id.name, names);
			lv.setAdapter(arrayAdapter);
			lv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View view,
						int pos, long id) {
					final Dialog dialog = new Dialog(getActivity());
					dialog.setContentView(R.layout.dialog_scene);
					dialog.setTitle("Actions");

					String item = lv.getItemAtPosition(pos).toString();
					final int idScene = getSceneByName(item);
					Log.v("Scenes", "idScene : " + idScene);
					Button openButton = (Button) dialog
							.findViewById(R.id.start);
					openButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							ExecScene(idScene);
							dialog.dismiss();
						}
					});
					Button cancelButton = (Button) dialog
							.findViewById(R.id.cancel);
					cancelButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				}
			});
			return lv;
		}

		private int getSceneByName(String name) {
			for (int i = 0; i < scenes.size(); i++) {
				if (scenes.get(i).getName().equals(name))
					return scenes.get(i).getId();
			}
			return -1;
		}

		private String replaceLast(String string, String from, String to) {
			int lastIndex = string.lastIndexOf(from);
			if (lastIndex < 0)
				return string;
			String tail = string.substring(lastIndex).replaceFirst(from, to);
			Log.v("Replace", string.substring(0, lastIndex) + tail);
			return string.substring(0, lastIndex) + tail;
		}

		private void ExecScene(int id) {
			final PostCommand command = new PostCommand(
					"https://z-cloud.z-wave.me/Scene/Activate/" + id,
					getActivity());

			new Thread(new Runnable() {
				public void run() {
					try {
						command.postData();
					} catch (KeyManagementException e) {
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
					}
				}
			}).start();
		}
	}
}
