package io.github.jzdayz.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.io.IOException;

@Configuration(proxyBeanMethods = false)
public class JacksonConfig {

    /**
     * Jackson全局转化long类型为String，解决jackson序列化时long类型缺失精度问题
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.serializerByType(DataSize.class, DataSizeStdSerializer.INSTANCE);
    }

    public static class DataSizeStdSerializer extends StdSerializer<DataSize> {

        public static final DataSizeStdSerializer INSTANCE = new DataSizeStdSerializer();

        private DataSizeStdSerializer() {
            super(DataSize.class);
        }

        @Override
        public void serialize(DataSize value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            final long kb = value.toKilobytes();
            final long mb = value.toMegabytes();
            if (mb == 0) {
                gen.writeString(kb + "KB");
                return;
            }
            gen.writeString(mb + "MB");
        }
    }

}
