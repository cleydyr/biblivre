package biblivre.acquisition.supplier;

import static org.junit.jupiter.api.Assertions.*;

import biblivre.AbstractContainerDatabaseTest;
import biblivre.TestDatasourceConfiguration;
import biblivre.core.SchemaThreadLocal;
import biblivre.core.exceptions.DAOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import net.datafaker.Faker;
import net.datafaker.providers.base.Address;
import net.datafaker.providers.base.Internet;
import net.datafaker.providers.base.PhoneNumber;
import net.datafaker.providers.base.Superhero;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Import({TestDatasourceConfiguration.class})
@ActiveProfiles("test")
public class SupplierDAOImplTest extends AbstractContainerDatabaseTest {
    Faker faker = new Faker();

    @Autowired SupplierDAO supplierDAO;

    @BeforeEach
    void setUp() {
        SchemaThreadLocal.setSchema("single");
        Assertions.assertEquals(supplierDAO.search(null, Integer.MAX_VALUE, 0).size(), 0);
    }

    @AfterEach
    void tearDown() {
        SchemaThreadLocal.setSchema("single");

        supplierDAO
                .search(null, Integer.MAX_VALUE, 0)
                .forEach(supplier -> supplierDAO.delete(supplier.getId()));
    }

    @Test
    public void save() {
        SupplierDTO savedSupplier = generateRandomSupplierDTO();

        int id = supplierDAO.save(savedSupplier);

        savedSupplier.setId(id);

        SupplierDTO retrievedSupplier = supplierDAO.get(id);

        equalsGetters(savedSupplier, retrievedSupplier);
    }

    @Test
    public void updateWithInexistentIdThrows() {
        SupplierDTO supplierWithDefaultId = generateRandomSupplierDTO();

        assertThrows(
                DAOException.class,
                () -> {
                    supplierDAO.update(supplierWithDefaultId);
                });
    }

    @Test
    public void update() {
        SupplierDTO savedSupplier = generateRandomSupplierDTO();

        int id = supplierDAO.save(savedSupplier);

        SupplierDTO replacing = generateRandomSupplierDTO();

        replacing.setId(-1);

        assertThrows(
                DAOException.class,
                () -> {
                    supplierDAO.update(replacing);
                });

        replacing.setId(id);

        supplierDAO.update(replacing);

        SupplierDTO retrievedSupplier = supplierDAO.get(id);

        equalsGetters(retrievedSupplier, replacing);
    }

    @Test
    public void delete() {
        SupplierDTO savedSupplier = generateRandomSupplierDTO();

        int id = supplierDAO.save(savedSupplier);

        assertEquals(1, supplierDAO.search(null, Integer.MAX_VALUE, 0).size());

        savedSupplier.setId(id);

        supplierDAO.delete(savedSupplier.getId());

        assertEquals(0, supplierDAO.search(null, Integer.MAX_VALUE, 0).size());
    }

    @Test
    public void search() {
        SupplierDTO savedSupplier = generateRandomSupplierDTO();

        supplierDAO.save(savedSupplier);

        assertEquals(
                1, supplierDAO.search(savedSupplier.getTrademark(), Integer.MAX_VALUE, 0).size());

        assertEquals(
                0,
                supplierDAO
                        .search(savedSupplier.getTrademark() + "suffix", Integer.MAX_VALUE, 0)
                        .size());
    }

    private static void equalsGetters(SupplierDTO savedSupplier, SupplierDTO retrievedSupplier) {
        Arrays.stream(SupplierDTO.class.getDeclaredMethods())
                .filter(method -> method.getName().startsWith("get"))
                .forEach(
                        method -> {
                            try {
                                Assertions.assertEquals(
                                        method.invoke(savedSupplier),
                                        method.invoke(retrievedSupplier));
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                fail(e);
                            }
                        });
    }

    @NotNull
    private SupplierDTO generateRandomSupplierDTO() {
        SupplierDTO savedSupplier = new SupplierDTO();

        Address address = faker.address();
        Internet internet = faker.internet();
        PhoneNumber phoneNumber = faker.phoneNumber();
        Superhero superhero = faker.superhero();

        savedSupplier.setTrademark(faker.brand().car());
        savedSupplier.setName(superhero.name());
        savedSupplier.setSupplierNumber(phoneNumber.subscriberNumber());
        savedSupplier.setVatRegistrationNumber(faker.cnpj().valid());
        savedSupplier.setAddress(address.fullAddress());
        savedSupplier.setAddressNumber(address.streetAddressNumber());
        savedSupplier.setArea(address.city());
        savedSupplier.setCity(address.city());
        savedSupplier.setComplement(address.buildingNumber());
        savedSupplier.setContact1(phoneNumber.cellPhone());
        savedSupplier.setContact2(phoneNumber.cellPhone());
        savedSupplier.setContact3(phoneNumber.cellPhone());
        savedSupplier.setContact4(phoneNumber.cellPhone());
        savedSupplier.setCountry(address.country());
        savedSupplier.setEmail(internet.emailAddress());
        savedSupplier.setInfo(superhero.descriptor());
        savedSupplier.setState(address.state());
        savedSupplier.setTelephone1(phoneNumber.phoneNumber());
        savedSupplier.setTelephone2(phoneNumber.phoneNumber());
        savedSupplier.setTelephone3(phoneNumber.phoneNumber());
        savedSupplier.setTelephone4(phoneNumber.phoneNumber());
        savedSupplier.setUrl(internet.url());
        savedSupplier.setZipCode(address.zipCode());
        return savedSupplier;
    }
}
