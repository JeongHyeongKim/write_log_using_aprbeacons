package com.aprbrother.aprilbeacondemos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aprbrother.aprilbeacondemo.R;
import com.aprilbrother.aprilbrothersdk.Beacon;

import java.util.ArrayList;
import java.util.Collection;

public class BeaconAdapter extends BaseAdapter {

	private ArrayList<Beacon> beacons;
	private LayoutInflater inflater;

	public BeaconAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
		this.beacons = new ArrayList<Beacon>();
	}

	public void replaceWith(Collection<Beacon> newBeacons) {
		this.beacons.clear();
		this.beacons.addAll(newBeacons);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return beacons.size();
	}

	@Override
	public Beacon getItem(int position) {
		return beacons.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		view = inflateIfRequired(view, position, parent);
		bind(getItem(position), view);
		return view;
	}

	private void bind(Beacon beacon, View view) { // 여기서 해당 비콘의 트랙 정보를 보여준다. 바꿔보자
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.macTextView.setText("Approximate Distance : "+
				+ beacon.getDistance() + "m");
		holder.majorTextView.setText("Major: " + beacon.getMajor());


		switch (beacon.getProximity()) {
		case 0:
			holder.proximityView.setText("nickname: " + "Chicken");
			break;
		case 1:
			holder.proximityView.setText("nickname: " + "Chicken");
			break;
		case 2:
			holder.proximityView.setText("nickname: " + "Chicken");
			break;
		case 3:
			holder.proximityView.setText("nickname: " + "Chicken");
			break;
		default:
			break;
		}
	}

	private View inflateIfRequired(View view, int position, ViewGroup parent) {
		if (view == null) {
			view = inflater.inflate(R.layout.device_item, null);
			view.setTag(new ViewHolder(view));
		}
		return view;
	}

	static class ViewHolder {
		final TextView macTextView;
		final TextView uuidTextView;
		final TextView majorTextView;
		final TextView minorTextView;
		final TextView measuredPowerTextView;
		final TextView rssiTextView;
		final TextView proximityView;

		ViewHolder(View view) {
			macTextView = (TextView) view.findViewWithTag("mac");
			uuidTextView = (TextView) view.findViewWithTag("uuid");
			majorTextView = (TextView) view.findViewWithTag("major");
			minorTextView = (TextView) view.findViewWithTag("minor");
			measuredPowerTextView = (TextView) view.findViewWithTag("mpower");
			rssiTextView = (TextView) view.findViewWithTag("rssi");
			proximityView = (TextView) view.findViewWithTag("proximity");
		}
	}
}
