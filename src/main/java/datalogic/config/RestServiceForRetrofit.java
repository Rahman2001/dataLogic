package datalogic.config;

import datalogic.service.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

@Slf4j
@Component
@Configuration
@RequiredArgsConstructor
public class RestServiceForRetrofit {
    private final Retrofit retrofit;
    private final Long RETROFIT_CACHE_SIZE;
    private final Long RETROFIT_LONG_RUNNING_READ_TIMEOUT;
    private final File RETROFIT_CACHE_DIRECTORY;
    private final Long RETROFIT_DEFAULT_READ_TIMEOUT;
    private OkHttpClient okHttpClient;
    private final Map<String, EndpointProperty> endpointPropertyMap;

    @Autowired
    public RestServiceForRetrofit(@Qualifier("restEndpoints") final List<EndpointProperty> restEndpoints,
                                  @NonNull @Value("${retrofit.integration.cacheSizeInMb}") final Long RETROFIT_CACHE_SIZE,
                                  @NonNull @Value("${retrofit.integration.cacheDirectory}") String RETROFIT_CACHE_DIRECTORY,
                                  @NonNull @Value("${retrofit.integration.longRunningReadTimeout}") Long RETROFIT_LONG_READING_TIMEOUT)
    {
        this.RETROFIT_CACHE_SIZE = RETROFIT_CACHE_SIZE;
        this.RETROFIT_CACHE_DIRECTORY = new File(RETROFIT_CACHE_DIRECTORY);
        this.RETROFIT_LONG_RUNNING_READ_TIMEOUT = RETROFIT_LONG_READING_TIMEOUT;
        this.RETROFIT_DEFAULT_READ_TIMEOUT = 200L;

        this.endpointPropertyMap = createEndpointsMap(restEndpoints);
        this.okHttpClient = defaultSetup();
        this.retrofit = new Retrofit.Builder().client(this.okHttpClient).build();
    }

    private OkHttpClient defaultSetup() {
        return new OkHttpClient.Builder()
                .readTimeout(RETROFIT_DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(60L, TimeUnit.SECONDS)
                .addInterceptor(new RequestLoggerInterceptor())
                .addInterceptor(new CorrelationIdHeaderInterceptor())
                .build();
    }
    @Bean
    public WeatherAPIClientService weatherAPIClientService() {
        this.retrofit.newBuilder().client(this.okHttpClient)
                .baseUrl(this.endpointPropertyMap.get("OpenWeatherMap_currentWeather_API").getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return this.retrofit.create(WeatherAPIClientService.class);
    }

    @Bean
    public IP_APIClientService ip_apiClientService(){
        this.retrofit.newBuilder().client(this.okHttpClient)
                .baseUrl(this.endpointPropertyMap.get("IP_API").getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return this.retrofit.create(IP_APIClientService.class);
    }

    @Bean
    public DailyWeatherAPIClientService dailyWeatherAPIClientService(){
        this.retrofit.newBuilder().client(this.okHttpClient)
                .baseUrl(this.endpointPropertyMap.get("OpenWeatherMap_dailyWeather_API").getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return this.retrofit.create(DailyWeatherAPIClientService.class);
    }

    @Bean
    public HourlyWeatherAPIClientService hourlyWeatherAPIClientService(){
        this.retrofit.newBuilder().client(this.okHttpClient)
                .baseUrl(this.endpointPropertyMap.get("OpenWeatherMap_hourlyWeather_API").getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        return this.retrofit.create(HourlyWeatherAPIClientService.class);
    }

    private Map<String, EndpointProperty> createEndpointsMap(List<EndpointProperty> endpointProperties) {
        return endpointProperties.stream().collect(toImmutableMap(EndpointProperty::getServiceName, Function.identity()));
    }
}
