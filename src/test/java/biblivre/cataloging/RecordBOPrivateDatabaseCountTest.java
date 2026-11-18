package biblivre.cataloging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import biblivre.administration.permissions.PermissionBO;
import biblivre.administration.permissions.PermissionDAO;
import biblivre.cataloging.enums.RecordDatabase;
import biblivre.cataloging.enums.RecordType;
import biblivre.cataloging.search.SearchDTO;
import biblivre.cataloging.search.SearchQueryDTO;
import biblivre.core.auth.AuthorizationPoints;
import biblivre.core.configurations.ConfigurationBO;
import biblivre.core.configurations.ConfigurationsDAO;
import biblivre.core.exceptions.AuthorizationException;
import biblivre.digitalmedia.DigitalMediaBO;
import biblivre.digitalmedia.DigitalMediaDAO;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class RecordBOPrivateDatabaseCountTest {
    private static AnnotationConfigApplicationContext context;
    private static PermissionBO permissionBO;
    private static RecordDAO recordDAO;
    private static CountingRecordBO countingRecordBO;

    @BeforeAll
    static void startContext() {
        permissionBO = mock(PermissionBO.class);
        recordDAO = mock(RecordDAO.class);

        context = new AnnotationConfigApplicationContext(Wiring.class);
        countingRecordBO = context.getBean(CountingRecordBO.class);
    }

    @AfterAll
    static void closeContext() {
        context.close();
    }

    @BeforeEach
    void resetCollaborators() {
        org.mockito.Mockito.reset(permissionBO, recordDAO);
    }

    @Test
    void countOnPrivateDatabaseAsksPermissionBOInsteadOfThrowingNullPointerException() {
        AuthorizationPoints authorizationPoints = mock(AuthorizationPoints.class);
        doThrow(new AuthorizationException())
                .when(permissionBO)
                .authorize(
                        authorizationPoints, "cataloging.bibliographic", "private_database_access");

        assertThrows(
                AuthorizationException.class,
                () -> countingRecordBO.count(search(RecordDatabase.PRIVATE), authorizationPoints));

        verify(recordDAO, never()).count(any());
    }

    @Test
    void countOnPrivateDatabaseReturnsTheDaoCountWhenAccessIsAllowed() {
        AuthorizationPoints authorizationPoints = mock(AuthorizationPoints.class);
        SearchDTO search = search(RecordDatabase.PRIVATE);
        when(recordDAO.count(search)).thenReturn(7);

        assertEquals(7, countingRecordBO.count(search, authorizationPoints));

        verify(permissionBO)
                .authorize(
                        authorizationPoints, "cataloging.bibliographic", "private_database_access");
    }

    @Test
    void countOnMainDatabaseSkipsThePrivateDatabasePermissionCheck() {
        AuthorizationPoints authorizationPoints = mock(AuthorizationPoints.class);
        SearchDTO search = search(RecordDatabase.MAIN);
        when(recordDAO.count(search)).thenReturn(4);

        assertEquals(4, countingRecordBO.count(search, authorizationPoints));

        verify(permissionBO, never()).authorize(any(), any(), any());
    }

    private static SearchDTO search(RecordDatabase database) {
        SearchDTO search = new SearchDTO(RecordType.BIBLIO);
        search.setQuery(new SearchQueryDTO(database));
        return search;
    }

    @Configuration
    static class Wiring {
        @Bean
        PermissionBO permissionBO() {
            return permissionBO;
        }

        @Bean
        PermissionDAO permissionDAO() {
            return mock(PermissionDAO.class);
        }

        @Bean
        RecordDAO recordDAO() {
            return recordDAO;
        }

        @Bean
        DigitalMediaDAO digitalMediaDAO() {
            return mock(DigitalMediaDAO.class);
        }

        @Bean
        ConfigurationsDAO configurationsDAO() {
            return mock(ConfigurationsDAO.class);
        }

        @Bean
        DigitalMediaBO digitalMediaBO() {
            return new DigitalMediaBO();
        }

        @Bean
        ConfigurationBO configurationBO() {
            return new ConfigurationBO();
        }

        @Bean
        CountingRecordBO countingRecordBO() {
            return new CountingRecordBO();
        }
    }

    static class CountingRecordBO extends RecordBO {
        @Override
        public void populateDetails(RecordDTO record, int mask) {}

        @Override
        public RecordType getRecordType() {
            return RecordType.BIBLIO;
        }
    }
}
