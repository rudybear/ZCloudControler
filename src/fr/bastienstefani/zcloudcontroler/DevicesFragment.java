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
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DevicesFragment extends Fragment {
	
	ArrayList<Actioneur> actioneurs;

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
				dialog.setTitle(R.string.dialogName);

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