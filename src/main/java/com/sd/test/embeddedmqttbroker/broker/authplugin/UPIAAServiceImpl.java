package com.sd.test.embeddedmqttbroker.broker.authplugin;

import org.springframework.stereotype.Service;


/*

    For the testing dummy authentication is used. Where everyone is authenticated.

 */

@Service
public class UPIAAServiceImpl implements UPIAAService {
    @Override
    public boolean isUserAuthenticated(String clientId, String username, String password) {
        return true;
    }

    @Override
    public boolean isAuthorizedPublisher(String clientId, String destination) {
        return true;
    }

    @Override
    public boolean isAuthorizedSubscriber(String clientId, String destination) {
        return true;
    }

}
