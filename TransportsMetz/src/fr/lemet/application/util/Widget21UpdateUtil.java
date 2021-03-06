/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.lemet.application.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.List;

import fr.lemet.application.R;
import fr.lemet.application.activity.widgets.TransportsWidget;
import fr.lemet.transportscommun.donnees.modele.ArretFavori;
import fr.lemet.transportscommun.donnees.modele.DetailArretConteneur;
import fr.lemet.transportscommun.donnees.modele.Horaire;
import fr.lemet.transportscommun.util.IconeLigne;
import fr.lemet.transportscommun.util.LogYbo;

public class Widget21UpdateUtil {

    private static final LogYbo LOG_YBO = new LogYbo(Widget11UpdateUtil.class);

    private Widget21UpdateUtil() {
    }

    public static void updateAppWidget(Context context, RemoteViews views, ArretFavori favori, Calendar calendar) {
        views.setTextViewText(R.id.nomArret, favori.nomArret);
        views.setTextViewText(R.id.directionArret, "-> " + favori.direction);
        views.setImageViewResource(R.id.iconeLigne, IconeLigne.getIconeResource(favori.nomCourt));
        Intent intent = new Intent(context, TransportsWidget.class);
        intent.setAction("YboClick_" + favori.arretId + '_' + favori.ligneId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widgetlayout, pendingIntent);

        int now = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE);
        calendar.add(Calendar.MINUTE, -3);
        try {
            List<DetailArretConteneur> prochainsDeparts = Horaire.getProchainHorairesAsList(favori.ligneId, favori.arretId,
                    3, calendar, favori.macroDirection);
            LOG_YBO.debug("Prochains departs : " + prochainsDeparts);

            Integer prochainDepart = null;
            if (prochainsDeparts.size() > 0) {
                int heureProchain = prochainsDeparts.get(0).getHoraire();
                if (heureProchain >= 24 * 60) {
                    heureProchain -= 24 * 60;
                }

                LOG_YBO.debug("heureProchain : " + heureProchain);
                LOG_YBO.debug("now : " + now);
                if ((now - heureProchain) >= 0 && (now - heureProchain) < 30) {
                    views.setTextColor(R.id.tempsRestantPasse, context.getResources().getColor(R.color.red));
                } else {
                    prochainDepart = prochainsDeparts.get(0).getHoraire();
                    views.setTextColor(R.id.tempsRestantPasse, context.getResources().getColor(R.color.blanc));
                }
                views.setTextViewText(R.id.tempsRestantPasse, formatterCalendar(prochainsDeparts.get(0).getHoraire()));

            } else {
                views.setTextViewText(R.id.tempsRestantPasse, "");
            }
            if (prochainsDeparts.size() > 1) {
                views.setTextViewText(R.id.tempsRestant, formatterCalendar(prochainsDeparts.get(1).getHoraire()));
                if (prochainDepart == null) {
                    prochainDepart = prochainsDeparts.get(1).getHoraire();
                }
            } else {
                views.setTextViewText(R.id.tempsRestant, "");
            }

            views.setTextViewText(R.id.tempsRestant,
                    prochainsDeparts.size() < 2 ? "" : formatterCalendar(prochainsDeparts.get(1).getHoraire()));
            views.setTextViewText(R.id.tempsRestantFutur,
                    prochainsDeparts.size() < 3 ? "" : formatterCalendar(prochainsDeparts.get(2).getHoraire()));

            views.setTextViewText(R.id.prochainBus, prochainDepart == null ? "" : (context.getString(R.string.prochain)
                    + " " + formatterTempsRestant(context, prochainDepart, now)));
        } catch (SQLiteException ignore) {

        }
    }

    private static String formatterCalendar(int prochainDepart) {
        StringBuilder stringBuilder = new StringBuilder();

        int heures = prochainDepart / 60;
        int minutes = prochainDepart - heures * 60;
        if (heures >= 24) {
            heures -= 24;
        }
        if (heures < 10) {
            stringBuilder.append('0');
        }
        stringBuilder.append(heures);
        stringBuilder.append(':');
        if (minutes < 10) {
            stringBuilder.append('0');
        }
        stringBuilder.append(minutes);

        return stringBuilder.toString();
    }

    private static String formatterTempsRestant(Context context, int prochainDepart, int now) {
        StringBuilder stringBuilder = new StringBuilder();
        int tempsEnMinutes = prochainDepart - now;
        if (tempsEnMinutes < 0) {
            stringBuilder.append(context.getString(R.string.tropTard));
        } else {
            int heures = tempsEnMinutes / 60;
            int minutes = tempsEnMinutes - heures * 60;
            boolean tempsAjoute = false;
            if (heures > 0) {
                stringBuilder.append(heures);
                stringBuilder.append(' ');
                stringBuilder.append(context.getString(R.string.miniHeures));
                stringBuilder.append(' ');
                tempsAjoute = true;
            }
            if (minutes > 0) {
                if (heures <= 0) {
                    stringBuilder.append(minutes);
                    stringBuilder.append(' ');
                    stringBuilder.append(context.getString(R.string.miniMinutes));
                } else {
                    if (minutes < 10) {
                        stringBuilder.append('0');
                    }
                    stringBuilder.append(minutes);
                }
                tempsAjoute = true;
            }
            if (!tempsAjoute) {
                stringBuilder.append("0 ");
                stringBuilder.append(context.getString(R.string.miniMinutes));
            }
        }
        return stringBuilder.toString();
    }
}
