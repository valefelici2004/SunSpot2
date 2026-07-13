package com.group3boot.sunspot.source.spot;

import com.group3boot.sunspot.models.Spot;
import com.group3boot.sunspot.models.SpotAPIResponse;
import com.group3boot.sunspot.util.Constants;
import com.group3boot.sunspot.util.JSONParserUtils;

import java.io.IOException;

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
        spotCallback.onAddSpotSuccess(spot);
    }

    @Override
    public void updateSpot(Spot spot) {
        spotCallback.onUpdateSpotSuccess(spot);
    }

    @Override
    public void deleteSpot(Spot spot) {
        spotCallback.onDeleteSpotSuccess(spot);
    }
}