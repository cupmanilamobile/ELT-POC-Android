package org.cambridge.eltpoc.connections;

import org.cambridge.eltpoc.api.TestHarnessService;

import retrofit.RestAdapter;

/**
 * Created by etorres on 6/19/15.
 */
public class ConnectionClient {

    private TestHarnessService service;
    private static final String ROOT_URL = "http://content-poc-api.cambridgelms.org";

    public TestHarnessService getService() {
        return service;
    }

    public void setupBearerTokenClient() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ROOT_URL).build();
        service = restAdapter.create(TestHarnessService.class);
    }
}
