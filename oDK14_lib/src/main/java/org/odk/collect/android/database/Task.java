package org.odk.collect.android.database;

import java.util.Date;

public class Task {
	public int id;
	public String type;
	public String title;
	public String url;
	public String form_id;
	public int form_version;
	public String initial_data;
	public String assignment_mode;
	public boolean gps;
	public boolean barcode;
	public Date scheduled_at;
	public String address;			// Key value pairs representing an unstructured address
	public String status;
}
