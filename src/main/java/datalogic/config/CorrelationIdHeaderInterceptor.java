package datalogic.config;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Optional;

public class CorrelationIdHeaderInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        final Request request;
        Optional<String> correlationId = CorrelationId.value();
        request = correlationId.map(s -> chain.request().newBuilder().addHeader(CorrelationId.HTTP_HEADER, s).build()).orElseGet(chain::request);
        return chain.proceed(request);
    }
}
