/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ujuzi.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/**
 *
 * @author Ahmed Maawy
 */
public class ApiMethods {
    private String apiAddress = null;
    private String postalCode = null;

    HttpConnection httpConnection = null;

    public ApiMethods(String apiAddress, String postalCode) {
        this.apiAddress = apiAddress;
        this.postalCode = postalCode;
    }

    private boolean parameterValidity() {
        if(this.apiAddress == null || this.apiAddress.equals("")) {
            return false;
        }
        if(this.postalCode == null || this.postalCode.equals("")) {
            return false;
        }

        return true;
    }

    public String getXMLResponse(String apiString) throws ApiException {
        InputStream inputstream = null;

        String httpResponse = null;

        // Check if the API was properly initialized
        if(!this.parameterValidity()) {
            throw new ApiException("Required parameters missing");
        }

        // Open HTTP connection
        try {
            httpConnection = (HttpConnection) Connector.open(this.apiAddress + "/XMLService/" + apiString);

            // Read response data

            inputstream = httpConnection.openInputStream();
            int length = (int) httpConnection.getLength();

            if(length != -1){
                byte incomingData[] = new byte[length];

                // Legacy method:
                // inputstream.read(incomingData);

                int actual = 0;
                int bytesread = 0;

                while ((bytesread != length) && (actual != -1)) {
                    actual = inputstream.read(incomingData, bytesread, length - bytesread);
                    bytesread += actual;
                }

                httpResponse = new String(incomingData);
            }
            else
            {
                ByteArrayOutputStream bytestream =
                    new ByteArrayOutputStream();

                int ch;

                while ((ch = inputstream.read()) != -1)
                {
                    bytestream.write(ch);
                }
                httpResponse = new String(bytestream.toByteArray());
                bytestream.close();
            }

            // Always safe to make sure you close the connection

            try {
                httpConnection.close();

                httpConnection = null;
            } catch (IOException ex) {
                throw new ApiException("Unable to close connection to server, " + ex.getMessage());
            }
        } catch (IOException ex) {
            throw new ApiException("Unable to open connection to server, " + ex.getMessage());
        } finally {
            if(httpConnection != null) {
                try {
                    httpConnection.close();
                } catch (IOException ex) {
                    throw new ApiException("Unable to close connection to server, " + ex.getMessage());
                }
            }
        }

        return httpResponse;
    }

    public String getApiAddress() {
        return apiAddress;
    }

    public void setApiAddress(String apiAddress) {
        this.apiAddress = apiAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
