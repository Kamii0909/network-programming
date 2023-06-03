module network.core {
    exports com.kien.network.core.support;
    exports com.kien.network.core.support.adapter;
    exports com.kien.network.core;
    exports com.kien.network.core.socket.api.context;
    exports com.kien.network.core.socket.api.handler;
    exports com.kien.network.core.socket.api.adapter;
    
    requires com.google.common;
    requires transitive org.slf4j;
}
