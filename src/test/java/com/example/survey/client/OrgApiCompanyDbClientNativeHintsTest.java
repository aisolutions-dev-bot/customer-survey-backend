package com.example.survey.client;

import java.util.Arrays;

import com.aisolutions.shared.tenancy.CompanyDbResponse;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrgApiCompanyDbClientNativeHintsTest {

    /** Native images deserialize the org-api company database response only when it is registered. */
    @Test
    void registersCompanyDatabaseResponseForNativeBinding() {
        RegisterReflectionForBinding registration =
                OrgApiCompanyDbClient.class.getAnnotation(RegisterReflectionForBinding.class);

        assertNotNull(registration);
        assertTrue(Arrays.asList(registration.value()).contains(CompanyDbResponse.class));
    }
}
