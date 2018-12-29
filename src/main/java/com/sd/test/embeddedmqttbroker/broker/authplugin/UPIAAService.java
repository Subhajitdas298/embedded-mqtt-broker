package com.sd.test.embeddedmqttbroker.broker.authplugin;

public interface UPIAAService {
    boolean isUserAuthenticated(String clientId, String username, String password);
    boolean isAuthorizedPublisher(String clientId, String destination);
    boolean isAuthorizedSubscriber(String clientId, String destination);
}
