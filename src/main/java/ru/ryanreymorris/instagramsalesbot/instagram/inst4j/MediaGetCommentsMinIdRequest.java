package ru.ryanreymorris.instagramsalesbot.instagram.inst4j;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.requests.IGGetRequest;
import com.github.instagram4j.instagram4j.responses.media.MediaGetCommentsResponse;

public class MediaGetCommentsMinIdRequest extends IGGetRequest<MediaGetCommentsResponse> implements IGReversedPaginatedRequest {

    private String mediaId;
    private String minId;

    public MediaGetCommentsMinIdRequest(String mediaId) {
        this.mediaId = mediaId;
    }

    @Override
    public String path() {
        return "media/" + mediaId + "/comments/";
    }


    @Override
    public String getQueryString(IGClient client) {
        return mapQueryString("min_id", minId);
    }

    @Override
    public Class<MediaGetCommentsResponse> getResponseType() {
        return MediaGetCommentsResponse.class;
    }

    @Override
    public void setMinId(String minId) {
        this.minId = minId;
    }
}