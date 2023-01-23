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
    private final Long RETROFIT_CACHE_SIZE;
    private final Long RETROFIT_LONG_RUNNING_READ_TIMEOUT;
    private final File RETROFIT_CACHE_DIRECTORY;
    private final Long RETROFIT_DEFAULT_READ_TIMEOUT;
    private OkHttpClient okHttpClient;
    private final Map<String, EndpointProperty> endpointPropertyMap;
    private final JacksonConverterFactory jacksonConverterFactory;

    @Autowired
    public RestServiceForRetrofit(@Qualifier("restEndpoints") final List<EndpointProperty> restEndpoints,
                                  @NonNull @Value("${retrofit.integration.cacheSizeInMb}") final Long RETROFIT_CACHE_SIZE,
                                  @NonNull @Value("${retrofit.integration.cacheDirectory}") String RETROFIT_CACHE_DIRECTORY,
                                  @NonNull @Value("${retrofit.integration.longRunningReadTimeout}") Long RETROFIT_LONG_READING_TIMEOUT,
                                  ServiceUtil serviceUtil)
    {
        this.RETROFIT_CACHE_SIZE = RETROFIT_CACHE_SIZE;
        this.RETROFIT_CACHE_DIRECTORY = new File(RETROFIT_CACHE_DIRECTORY);
        this.RETROFIT_LONG_RUNNING_READ_TIMEOUT = RETROFIT_LONG_READING_TIMEOUT;
        this.RETROFIT_DEFAULT_READ_TIMEOUT = 200L;

        this.endpointPropertyMap = serviceUtil.groupsEndpoints(restEndpoints);
        this.okHttpClient = defaultSetup();
        this.jacksonConverterFactory = JacksonConverterFactory.create();
    }

    private OkHttpClient defaultSetup() {
        return new OkHttpClient.Builder()
                .readTimeout(RETROFIT_DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(60L, TimeUnit.SECONDS)
                .callTimeout(20L, TimeUnit.SECONDS)
                .addInterceptor(new RequestLoggerInterceptor())
                .addInterceptor(new CorrelationIdHeaderInterceptor())
                .build();
    }
    @Bean
    public WeatherAPIClientService weatherAPIClientService() {
        Retrofit retrofit = new Retrofit.Builder()
                .client(this.okHttpClient)
                .baseUrl(this.endpointPropertyMap.get("OpenWeatherMap_currentWeather_API").getBaseUrl())
                .addConverterFactory(this.jacksonConverterFactory)
                .build();
        return retrofit.create(WeatherAPIClientService.class);
    }

    @Bean
    public IP_APIClientService ip_apiClientService(){
        Retrofit retrofit = new Retrofit.Builder().client(this.okHttpClient)
                .baseUrl(this.endpointPropertyMap.get("IP_API").getBaseUrl())
                .addConverterFactory(this.jacksonConverterFactory)
                .build();
        return retrofit.create(IP_APIClientService.class);
    }

    @Bean
    public DailyWeatherAPIClientService dailyWeatherAPIClientService(){
        Retrofit retrofit = new Retrofit.Builder().client(this.okHttpClient)
                .baseUrl(this.endpointPropertyMap.get("OpenWeatherMap_dailyWeather_API").getBaseUrl())
                .addConverterFactory(this.jacksonConverterFactory)
                .build();
        return retrofit.create(DailyWeatherAPIClientService.class);
    }

    @Bean
    public HourlyWeatherAPIClientService hourlyWeatherAPIClientService(){
        Retrofit retrofit = new Retrofit.Builder().client(this.okHttpClient)
                .baseUrl(this.endpointPropertyMap.get("OpenWeatherMap_hourlyWeather_API").getBaseUrl())
                .addConverterFactory(this.jacksonConverterFactory)
                .build();
        return retrofit.create(HourlyWeatherAPIClientService.class);
    }
}
