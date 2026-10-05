package com.example.survey.client;

import java.util.Arrays;

import com.example.survey.dto.NotificationConfigDTO;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SystemParameterClientNativeHintsTest {

    @Test
    void registersNotificationConfigForNativeBinding() {
        RegisterReflectionForBinding registration =
                SystemParameterClient.class.getAnnotation(RegisterReflectionForBinding.class);

        assertNotNull(registration);
        assertTrue(Arrays.asList(registration.value()).contains(NotificationConfigDTO.class));
    }

    @Test
    void fallsBackWhenTheOrganizationApiIsUnavailable() {
        RestTemplate unavailableRestTemplate = new RestTemplate() {
            @Override
            public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables)
                    throws RestClientException {
                throw new RestClientException("org-api unavailable");
            }
        };

        NotificationConfigDTO config = new SystemParameterClient(unavailableRestTemplate).fetchNotificationConfig();

        assertTrue(config.isEmailEnabled());
        assertTrue(config.isSmsEnabled());
        assertTrue(config.isWhatsappEnabled());
    }
}
