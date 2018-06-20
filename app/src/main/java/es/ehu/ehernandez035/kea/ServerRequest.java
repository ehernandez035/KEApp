package es.ehu.ehernandez035.kea;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ServerRequest extends AsyncTask<Void, Void, Response> {

    private final Context context;
    private final String url;
    private final Map<String, String> params;
    private final RequestCallback successListener;
    private Runnable errorListener = null;
    private boolean withCookie = true;

    public ServerRequest(Context context, String url, RequestCallback successListener) {
        this.context = context;
        this.url = url;
        this.successListener = successListener;
        this.params = null;
    }

    public ServerRequest(Context context, String url, Map<String, String> params, RequestCallback successListener) {
        this.context = context;
        this.url = url;
        this.params = params;
        this.successListener = successListener;
    }

    public ServerRequest withoutCookie() {
        this.withCookie = false;
        return this;
    }

    public ServerRequest execute() {
        this.execute((Void[]) null);
        return this;
    }

    public ServerRequest setErrorListener(Runnable callback) {
        this.errorListener = callback;
        return this;
    }

    @Override
    protected Response doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).build();
        Request.Builder builder = new okhttp3.Request.Builder()
                .url(url);

        if (this.withCookie) {
            String cookie = SharedPrefManager.getInstance(context).getCookie();

            builder.header("Cookie", "PHPSESSID=" + cookie);
        }
        if (this.params != null) {
            FormBody.Builder formbuilder = new FormBody.Builder();
            for (Map.Entry<String, String> e : this.params.entrySet()) {
                formbuilder.add(e.getKey(), e.getValue());
            }
            RequestBody body = formbuilder.build();
            builder.post(body);
        }

        Request request = builder.build();
        try {
            return client.newCall(request).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("KEA", "Errorea zerbitzariarekin konektatzean", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Response response) {
        try {
            if (response != null) this.successListener.onSuccess(response);
            else if (errorListener != null) this.errorListener.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
