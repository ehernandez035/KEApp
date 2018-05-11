package es.ehu.ehernandez035.kea;

import java.io.IOException;

import okhttp3.Response;

public interface RequestCallback {

    void onSuccess(Response response) throws IOException;
}
