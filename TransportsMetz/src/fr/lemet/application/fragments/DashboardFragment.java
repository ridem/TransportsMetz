/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.lemet.application.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.lemet.application.R;
import fr.lemet.application.activity.alerts.TabAlertes;
import fr.lemet.application.activity.bus.BusMetz;
import fr.lemet.application.activity.bus.ListArretByPosition;
import fr.lemet.application.activity.itineraires.ItineraireRequete;
import fr.lemet.application.activity.parkrelais.ListParkRelais;
import fr.lemet.application.activity.plans.PlanReseau;
import fr.lemet.application.activity.velos.ListStationsByPosition;

import fr.lemet.application.application.TransportsMetzApplication;
import fr.lemet.transportscommun.util.Theme;

public class DashboardFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root;
		if (TransportsMetzApplication.getTheme(getActivity()) == Theme.NOIR) {
			root = inflater.inflate(R.layout.fragment_dashboard_noir, container);
		} else {
			root = inflater.inflate(R.layout.fragment_dashboard, container);
		}
		root.findViewById(R.id.home_btn_bus).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), BusMetz.class));
			}
		});
		root.findViewById(R.id.home_btn_bus_gps).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), ListArretByPosition.class));
			}
		});
		/*root.findViewById(R.id.home_btn_alert).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), TabAlertes.class));
			}
		}); */
		root.findViewById(R.id.home_btn_carte).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), PlanReseau.class));
			}
		});
		/*root.findViewById(R.id.home_btn_parking).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), ListParkRelais.class));
			}
		}); */
		root.findViewById(R.id.home_btn_itineraires).setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				//startActivity(new Intent(getActivity(), ItineraireRequete.class));

			}
		});
		return root;
	}
}
