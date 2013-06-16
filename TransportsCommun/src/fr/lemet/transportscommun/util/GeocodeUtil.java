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

package fr.lemet.transportscommun.util;

import android.content.Context;
import android.location.Address;
import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderGeometry;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderResult;
import com.google.code.geocoder.model.GeocoderStatus;
import com.google.code.geocoder.model.LatLng;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class GeocodeUtil {

    private static final int MAX_RESULTS = 5;

    private android.location.Geocoder geocoderAndroid;

    public GeocodeUtil(Context context) {
        geocoderAndroid = new android.location.Geocoder(context);
    }

    public GeocodeResponse geocode(GeocoderRequest request) {
        // Utilisation du geocoderAndroid
        List<Address> adresses = null;
        try {
            if (request.getBounds() != null) {
                adresses = geocoderAndroid.getFromLocationName(request.getAddress(), MAX_RESULTS, request.getBounds()
                        .getSouthwest().getLat().doubleValue(), request.getBounds().getSouthwest().getLng()
                        .doubleValue(), request.getBounds().getNortheast().getLat().doubleValue(), request.getBounds()
                        .getNortheast().getLng().doubleValue());
            } else {
                adresses = geocoderAndroid.getFromLocationName(request.getAddress(), MAX_RESULTS);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (adresses == null || adresses.isEmpty()) {
            return Geocoder.geocode(request);
        }
        GeocodeResponse response = new GeocodeResponse();
        response.setStatus(GeocoderStatus.OK);
        List<GeocoderResult> results = new ArrayList<GeocoderResult>();
        for (Address address : adresses) {
            GeocoderResult result = new GeocoderResult();
            result.setFormattedAddress(address.getAddressLine(0) + ", " + address.getLocality());
            result.setGeometry(new GeocoderGeometry());
            result.getGeometry().setLocation(
                    new LatLng(BigDecimal.valueOf(address.getLatitude()), BigDecimal.valueOf(address.getLongitude())));
            results.add(result);
        }
        response.setResults(results);
        return response;
    }

}
