package datalogic.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class RequestLoggerInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Marker loggerMarker = MarkerFactory.getMarker("OkHttp Request Marker");
        StopWatch clock = new StopWatch();
        String requestBody = bodyToString(request);
        String requestMethod = request.method();

        StringBuilder stringBuilder = new StringBuilder().append("OkHttp Request:").append("\n")
                .append(requestMethod).append(" - ").append(request.url());

        if(requestBody != null) {
            stringBuilder.append("\nBody:\n").append(requestBody);
        }
        else {
            stringBuilder.append("\nBody: ****\n");
        }

        clock.start();
        Response response;
        try{
            response = chain.proceed(request);
        }
        catch (IOException e){
            clock.stop();
            log.error(loggerMarker, stringBuilder.append("\nTime: ").append(clock.getTotalTimeMillis()).append("ms")
                    .append("\nMessage: ").append(e.getMessage()).toString(), e);
            throw e;
        }
        clock.stop();
        stringBuilder.append("\nTime: ").append(clock.getLastTaskTimeMillis()).append("ms")
                .append("\nResponse code: ").append(response.code());

        if(response.isSuccessful()){
            log.info(loggerMarker, stringBuilder.toString());
        }else {
            log.error(loggerMarker, stringBuilder.toString());
        }
        return response;
    }

    private String bodyToString(Request request) {
        try {
            Request copy = request.newBuilder().build();
            Buffer buffer = new Buffer();
            Objects.requireNonNull(copy.body()).writeTo(buffer);
            return buffer.readUtf8();
        }
        catch (IOException ignored) {
            return null;
        }
    }
}
