package io.github.jzdayz.utils;

import java.net.ServerSocket;

public abstract class SocketUtils {

    public static int availablePort(){
        for (int j = 8080; j < 60000; j++) {
            try (
                    ServerSocket ignored = new ServerSocket(j)
            ){
                return j;
            }catch (Exception e){ /*ignore*/}
        }
        throw new RuntimeException();
    }

}
