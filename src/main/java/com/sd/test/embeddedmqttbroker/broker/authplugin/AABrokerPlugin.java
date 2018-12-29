package com.sd.test.embeddedmqttbroker.broker.authplugin;

import org.apache.activemq.broker.*;
import org.apache.activemq.broker.region.Subscription;
import org.apache.activemq.broker.region.virtual.VirtualDestination;
import org.apache.activemq.command.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*

    For the testing dummy authentication is used. Where everyone is authenticated.

 */

@Component
public class AABrokerPlugin implements BrokerPlugin {

    @Autowired
    UPIAAService upiaaService;

    @Override
    public Broker installPlugin(Broker broker) throws Exception {
        return new AABroker(broker, upiaaService);
    }
}

class AABroker extends BrokerFilter {

    Logger logger = null;

    UPIAAService aaService;

    public AABroker(Broker next, UPIAAService aaService) {
        super(next);
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.aaService = aaService;
    }

    @Override
    public void addConnection(ConnectionContext context, ConnectionInfo info) throws Exception {
        logger.info("Connection from " + context + ", " + info);

        String clientId = context.getClientId();
        String username =  info.getUserName();
        String password = info.getPassword();

        if (aaService.isUserAuthenticated(clientId, username, password)) {
            logger.info("Added Connection from " + context.getClientId());
            super.addConnection(context, info);
        }else {
            throw new SecurityException("Unauthorized user");
        }
    }

    @Override
    public void virtualDestinationAdded(ConnectionContext context, VirtualDestination virtualDestination) {
        logger.info("Virtual dest from " + context + ", " + virtualDestination);
        super.virtualDestinationAdded(context, virtualDestination);
    }

    @Override
    public Subscription addConsumer(ConnectionContext context, ConsumerInfo info) throws Exception {
        logger.info("Consumer from " + context + ", " + info);

        String clientId = context.getClientId();
        String topic = info.getDestination().getPhysicalName();

        if(aaService.isAuthorizedSubscriber(clientId, topic)) {
            logger.info("Added Consumer from " + context.getClientId());
            return super.addConsumer(context, info);
        }else {
            throw new SecurityException("Unauthorized user");
        }
    }

    @Override
    public void addProducer(ConnectionContext context, ProducerInfo info) throws Exception {
        logger.info("Producer from " + context + ", " + info);

        String clientId = context.getClientId();
        String topic = null;//info.getDestination().getPhysicalName();


        if(aaService.isAuthorizedSubscriber(clientId, topic)) {
            logger.info("Added Producer from " + context.getClientId());
            super.addProducer(context, info);
        }else{
            throw new SecurityException("Unauthorized user");
        }
    }

    @Override
    public void beginTransaction(ConnectionContext context, TransactionId xid) throws Exception {
        logger.info("Transaction from " + context + ", " + xid);
        super.beginTransaction(context, xid);
    }

    @Override
    public void addSession(ConnectionContext context, SessionInfo info) throws Exception {
        logger.info("Session from " + context + ", " + info);
        super.addSession(context, info);
    }

    @Override
    public void removeSession(ConnectionContext context, SessionInfo info) throws Exception {
        logger.info("Remove Session from " + context + ", " + info);
        super.removeSession(context, info);
    }

    @Override
    public void addDestinationInfo(ConnectionContext context, DestinationInfo info) throws Exception {
        logger.info("Destination from " + context + ", " + info);
        super.addDestinationInfo(context, info);
    }

    @Override
    public void send(ProducerBrokerExchange producerExchange, Message messageSend) throws Exception {
        logger.info("Message from " + producerExchange + ", " + messageSend);

        String clientId = producerExchange.getConnectionContext().getClientId();
        String topic = messageSend.getDestination().getPhysicalName();

        if(aaService.isAuthorizedPublisher(clientId, topic)) {
            logger.info("Message : " + new String(messageSend.getContent().getData()));
            super.send(producerExchange, messageSend);
        }else {
            throw new SecurityException("Unauthorized user");
        }
    }

}

