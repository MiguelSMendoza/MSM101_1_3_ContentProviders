package es.netrunners.providers;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Intents.Insert;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class ContentProvidersActivity extends ListActivity {

	String[] from = new String[] { "Name", "Phone" };
	int[] to = new int[] { R.id.name, R.id.phone };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		fillList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		fillList();
	}

	private void fillList() {
		Cursor cursor = getContentResolver().query(Contacts.CONTENT_URI, null,
				null, null, null);
		// Fill List with DATA
		ArrayList<HashMap<String, String>> Cons = new ArrayList<HashMap<String, String>>();
		while (cursor.moveToNext()) {

			HashMap<String, String> contactsData = new HashMap<String, String>();

			contactsData.put("ID",
					cursor.getString(cursor.getColumnIndex(BaseColumns._ID)));
			contactsData.put(from[0], cursor.getString(cursor
					.getColumnIndex(Contacts.DISPLAY_NAME)));
			if (Integer
					.parseInt(cursor.getString(cursor
							.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
				Cursor pCur = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = "
								+ cursor.getString(cursor
										.getColumnIndex(BaseColumns._ID)), null,
						null);

				while (pCur.moveToNext()) {
					if (pCur.getInt(pCur.getColumnIndex(Phone.TYPE)) == Phone.TYPE_MOBILE) {

						contactsData.put(from[1], pCur.getString(pCur
								.getColumnIndex(Phone.NUMBER)));
					}
				}
				pCur.close();
			}

			Cons.add(contactsData);
		}
		cursor.close();
		SimpleAdapter ListAdapter = new SimpleAdapter(this, Cons, R.layout.row,
				from, to);
		setListAdapter(ListAdapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		@SuppressWarnings("unchecked")
		HashMap<String, String> item = (HashMap<String, String>) this
				.getListAdapter().getItem(position);
		showEditDialog(item.get("ID"), item.get(from[0]), item.get(from[1]));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.addContact:
			newContact();
			return true;
		}
		return false;
	}

	private void newContact() {
		Intent i = new Intent(Intent.ACTION_INSERT_OR_EDIT);
		i.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
		startActivity(i);

	}

	/**
	 * Show a dialog asking the user for a confirmation about the new contact
	 */
	private void showEditDialog(final String ID, String name, String phone) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(R.layout.dialog, null);
		builder.setView(textEntryView);
		EditText Name = (EditText) textEntryView.findViewById(R.id.newname);
		EditText Phone = (EditText) textEntryView.findViewById(R.id.newphone);
		Name.setText(name);
		Phone.setText(phone);
		builder.setTitle("Editar Cliente")
				.setCancelable(false)
				.setPositiveButton("Guardar",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								EditText name = (EditText) textEntryView
										.findViewById(R.id.newname);
								EditText phone = (EditText) textEntryView
										.findViewById(R.id.newphone);
								editContact(Integer.parseInt(ID), name
										.getText().toString(), phone.getText()
										.toString());
							}

						})
				.setNegativeButton("Cancelar",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();

	}

	protected void editContact(int iD, String name, String phone) {
		Intent i = new Intent(Intent.ACTION_INSERT_OR_EDIT);
		i.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
		i.putExtra(Insert.NAME, name);
		i.putExtra(Insert.PHONE, phone);
		startActivity(i);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case 1:
				if (requestCode == 1) {

				}
				super.onActivityResult(requestCode, resultCode, data);
			}
		}
	}

}