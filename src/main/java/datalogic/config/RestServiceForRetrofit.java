package datalogic.config;

import datalogic.service.GeocodingAPIClientService;
import datalogic.service.IPGeolocationAPIClientService;
import datalogic.service.WeatherAPIClientService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

@Slf4j
@Configuration
public class RestServiceForRetrofit {
    private final Retrofit retrofit;
    private final Long RETROFIT_CACHE_SIZE;
    private final Long RETROFIT_LONG_RUNNING_READ_TIMEOUT;
    private final File RETROFIT_CACHE_DIRECTORY;
    private final Long RETROFIT_DEFAULT_READ_TIMEOUT;

    @Autowired
    public RestServiceForRetrofit(final List<EndpointProperty> restEndpoints,
                                  @NonNull @Value("${retrofit.integration.cacheSizeInMb}") Long RETROFIT_CACHE_SIZE,
                                  @NonNull @Value("${retrofit.integration.cacheDirectory}") String RETROFIT_CACHE_DIRECTORY,
                                  @NonNull @Value("${retrofit.integration.longRunningReadTimeout}") Long RETROFIT_LONG_READING_TIMEOUT)
    {
        this.RETROFIT_CACHE_SIZE = RETROFIT_CACHE_SIZE;
        this.RETROFIT_CACHE_DIRECTORY = new File(RETROFIT_CACHE_DIRECTORY);
        this.RETROFIT_LONG_RUNNING_READ_TIMEOUT = RETROFIT_LONG_READING_TIMEOUT;
        this.RETROFIT_DEFAULT_READ_TIMEOUT = 200L;

        Map<String, EndpointProperty> serviceNameMap = createEndpointsMap(restEndpoints);
        this.retrofit = defaultSetup(serviceNameMap.get("OpenWeatherMap_geocoding_API"));
    }

    private Retrofit defaultSetup(EndpointProperty endpointProperty) {
        log.info("Rest service for Retrofit is being initialized with default read timeout: " +
                endpointProperty.getServiceName() + " " + endpointProperty.getBaseUrl() + " " + this.RETROFIT_DEFAULT_READ_TIMEOUT);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(RETROFIT_DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(60L, TimeUnit.SECONDS)
                .addInterceptor()
                .addInterceptor()
                .build();

         return new Retrofit.Builder()
                .baseUrl(endpointProperty.getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Bean
    public GeocodingAPIClientService geocodingAPIClientService() {
        return this.retrofit.create(GeocodingAPIClientService.class);
    }

    @Bean
    public IPGeolocationAPIClientService ipGeolocationAPIClientService() {
        return this.retrofit.create(IPGeolocationAPIClientService.class);
    }

    @Bean
    public WeatherAPIClientService weatherAPIClientService() {
        return this.retrofit.create(WeatherAPIClientService.class);
    }

    private Map<String, EndpointProperty> createEndpointsMap(List<EndpointProperty> endpointProperties) {
        return endpointProperties.stream().collect(toImmutableMap(EndpointProperty::getServiceName, Function.identity()));
    }
}
