package fr.bastienstefani.zcloudcontroler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class SceneFragment extends Fragment {

	private ArrayList<Scene> scenes;

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
			public void onItemClick(AdapterView<?> adapter, View view, int pos,
					long id) {
				final Dialog dialog = new Dialog(getActivity());
				dialog.setContentView(R.layout.dialog_scene);
				dialog.setTitle(R.string.dialogName);

				String item = lv.getItemAtPosition(pos).toString();
				final int idScene = getSceneByName(item);
				Button openButton = (Button) dialog.findViewById(R.id.start);
				openButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ExecScene(idScene);
						dialog.dismiss();
					}
				});
				Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
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
		return string.substring(0, lastIndex) + tail;
	}

	private void ExecScene(int id) {
		final PostCommand command = new PostCommand(
				"https://z-cloud.z-wave.me/Scene/Activate/" + id, getActivity());

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
