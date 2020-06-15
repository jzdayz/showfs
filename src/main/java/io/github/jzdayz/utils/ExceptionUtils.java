package io.github.jzdayz.utils;


public class ExceptionUtils {

    public interface SupplierEx<T> {
        T get() throws Exception;
    }

    public static <T> T ex(SupplierEx<T> get,T def){
        try {
            return get.get();
        }catch (Exception e){
            return def;
        }
    }
}
