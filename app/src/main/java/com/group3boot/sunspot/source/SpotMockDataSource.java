package com.group3boot.sunspot.source;

import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.models.SpotAPIResponse;
import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.util.JSONParserUtils;

import java.io.IOException;

/**
 * Classe per ottenere gli spot da un file JSON locale,
 * per simulare la risposta di Firestore.
 */
public class SpotMockDataSource extends BaseSpotRemoteDataSource {

    private final JSONParserUtils jsonParserUtil;

    public SpotMockDataSource(JSONParserUtils jsonParserUtil) {
        this.jsonParserUtil = jsonParserUtil;
    }

    @Override
    public void getSpots() {
        SpotAPIResponse spotAPIResponse = null;

        try {
            spotAPIResponse = jsonParserUtil.parseJSONWithGson(
                    Constants.SAMPLE_SPOT_JSON_FILENAME, SpotAPIResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (spotAPIResponse != null) {
            spotCallback.onSuccessFromRemote(spotAPIResponse.getSpots(), System.currentTimeMillis());
        } else {
            spotCallback.onFailureFromRemote(new Exception(Constants.MOCK_DATA_ERROR));
        }
    }

    @Override
    public void addSpot(Spot spot) {
        // In modalità mock non c'è un vero salvataggio remoto:
        // simula semplicemente il successo, utile per testare la UI
        spotCallback.onAddSpotSuccess(spot);
    }

    @Override
    public void deleteSpot(Spot spot) {
        spotCallback.onDeleteSpotSuccess(null);
    }
}