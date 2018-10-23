package com.imooc.coursedubboservice.ServiceImpl;

import com.imooc.thrift.user.UserService;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ServiceProvider {
    @Value("${thrift.user.ip}")
    private String serverIp;

    @Value("${thrift.user.port}")
    private int serverPort;


    private enum ServiceType {
        USER
    }

    @Bean
    public UserService.Client getUserService() {
        return getService(serverIp, serverPort, ServiceType.USER);
    }



    public <T>T getService(String ip, int port, ServiceType type) {
        TSocket socket = new TSocket(ip, port, 5000);
        TTransport tTransport = new TFramedTransport(socket);
        try {
            tTransport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
            return null;
        }

        TProtocol protocol = new TBinaryProtocol(tTransport);

        TServiceClient client = null;
        switch (type) {
            case USER:
                client = new UserService.Client(protocol);
                break;
        }


        return (T)client;
    }
}
