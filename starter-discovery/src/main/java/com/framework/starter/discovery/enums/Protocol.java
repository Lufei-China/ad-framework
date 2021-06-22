package com.framework.starter.discovery.enums;

public enum Protocol {
    HTTP("http"), HTTPS("https"), GRPC("grpc");

    Protocol(String protocolName) {
        this.protocolName = protocolName;
    }

    private String protocolName;

    public String of() {
        return protocolName;
    }

}
