package com.beastek.eol.Extras.Interface;

import com.beastek.eol.ui.patient.EolRestClient;


public interface RestCallbackHandler {
    public void handleResponse(EolRestClient client);
}
