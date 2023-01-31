package serviceTests.serviceImplTests;

import datalogic.model.UserLocation;
import datalogic.service.clientService.IP_APIClientService;
import datalogic.service.serviceImpl.IP_APIClientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IPApiClientServiceImplTest {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS) //we used DEEP_STUBS because we intend to mock argument of method (.getUserLocation(String ipAddress))
    IP_APIClientService ipService;
    @InjectMocks
    IP_APIClientServiceImpl ipImpl; // a class we want to test for functionality.

    @Test
    public void getUserLocationTest(){
        when(this.ipService.getUserLocation(anyString()).join()).thenReturn(UserLocation.builder().build());
        assertNotNull(this.ipImpl.getUserLocation());
        verify(this.ipService, times(1)).getUserLocation(anyString());
    }

}
