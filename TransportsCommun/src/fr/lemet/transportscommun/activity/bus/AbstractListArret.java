package fr.lemet.transportscommun.activity.bus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import fr.lemet.transportscommun.AbstractTransportsApplication;
import fr.lemet.transportscommun.R;
import fr.lemet.transportscommun.activity.commun.BaseActivity;
import fr.lemet.transportscommun.donnees.modele.Ligne;
import fr.lemet.transportscommun.fragments.AbstractListArretFragment;
import fr.lemet.transportscommun.AbstractTransportsApplication;
import fr.lemet.transportscommun.R;
import fr.lemet.transportscommun.activity.commun.BaseActivity.BaseTabFragmentActivity;
import fr.lemet.transportscommun.activity.commun.ChangeIconActionBar;
import fr.lemet.transportscommun.donnees.modele.Ligne;
import fr.lemet.transportscommun.fragments.AbstractListArretFragment;

public abstract class AbstractListArret extends BaseActivity.BaseTabFragmentActivity implements ChangeIconActionBar {

	protected abstract int getLayout();

	protected abstract void setupActionBar();

	protected abstract Class<? extends ListFragment> getListArretFragment();

	protected abstract Class<? extends AbstractArretOnMap> getArretOnMap();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());
		setupActionBar();
		configureTabs();
		Ligne myLigne = (Ligne) getIntent().getExtras().getSerializable("ligne");
		if (myLigne == null) {
			myLigne = new Ligne();
			myLigne.id = getIntent().getStringExtra("ligneId");
		}

		for (Ligne ligne : AbstractTransportsApplication.getDataBaseHelper().selectAll(Ligne.class)) {
			Bundle args = new Bundle();
			args.putSerializable("ligne", ligne);
			if (myLigne.id.equals(ligne.id)) {
				myLigne = ligne;
			}
			addTab(ligne.id, ligne.nomCourt, getListArretFragment(), args);
		}
		if (savedInstanceState != null) {
			setCurrentTab(savedInstanceState);
		} else {
			setCurrentTab(myLigne.id);
		}
	}

	public String getCurrrentTabTag() {
		return getCurrentTab();
	}

	private boolean orderDirection = true;

	public boolean isOrderDirection() {
		return orderDirection;
	}

	@Override
	public void changeIconActionBar(ImageButton imageButton) {
		if (imageButton.getId() == R.id.menu_order) {
			imageButton.setImageResource(orderDirection ? android.R.drawable.ic_menu_sort_alphabetically
					: android.R.drawable.ic_menu_sort_by_size);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		if (menu.findItem(R.id.menu_order) != null) {
			menu.findItem(R.id.menu_order).setTitle(
					orderDirection ? R.string.menu_orderByName : R.string.menu_orderBySequence);
			menu.findItem(R.id.menu_order).setIcon(
					orderDirection ? android.R.drawable.ic_menu_sort_alphabetically
							: android.R.drawable.ic_menu_sort_by_size);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		if (item.getItemId() == R.id.menu_order) {
			orderDirection = !orderDirection;
			AbstractListArretFragment fragment = (AbstractListArretFragment) getCurrentFragment();
			fragment.construireListe();
			getActivityHelper().invalidateOptionsMenu();
			return true;
		} else if (item.getItemId() == R.id.menu_google_map) {
			Intent intent = new Intent(this, getArretOnMap());
			AbstractListArretFragment fragment = (AbstractListArretFragment) getCurrentFragment();
			intent.putExtra("ligne", fragment.getMyLigne());
			if (fragment.getCurrentDirection() != null) {
				intent.putExtra("direction", fragment.getCurrentDirection());
			}
			if (fragment.getMyLigne() != null) {
				startActivity(intent);
			}
		}
		return false;
	}
}
