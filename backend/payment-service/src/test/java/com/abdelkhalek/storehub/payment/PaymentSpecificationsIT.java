package com.abdelkhalek.storehub.payment;

import com.abdelkhalek.storehub.payment.entity.PaymentEntity;
import com.abdelkhalek.storehub.payment.enums.PaymentStatus;
import com.abdelkhalek.storehub.payment.repository.PaymentRepository;
import com.abdelkhalek.storehub.payment.repository.PaymentSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class PaymentSpecificationsIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("payments_test")
            .withUsername("test")
            .withPassword("test");



    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private UUID customerA;
    private UUID customerB;

    private PaymentEntity payment1; // captureId=cap_1, status=CAPTURED, customerA
    private PaymentEntity payment2; // captureId=cap_2, status=CREATED, customerA
    private PaymentEntity payment3; // authorizationId=auth_3, status=CAPTURED, customerB
    private PaymentEntity payment4; // status=CANCELLED, customerB, older createdAt

    @BeforeEach
    void setUp() {
        customerA = UUID.randomUUID();
        customerB = UUID.randomUUID();

        payment1 = savePayment("cap_1", null, customerA, PaymentStatus.CAPTURED,
                LocalDateTime.now().minusDays(1));
        payment2 = savePayment("cap_2", null, customerA, PaymentStatus.CREATED,
                LocalDateTime.now().minusDays(2));
        payment3 = savePayment(null, "auth_3", customerB, PaymentStatus.CAPTURED,
                LocalDateTime.now().minusDays(3));
        payment4 = savePayment(null, null, customerB, PaymentStatus.CANCELLED,
                LocalDateTime.now().minusDays(10));

        entityManager.flush();
        entityManager.clear();
    }

    private PaymentEntity savePayment(String captureId, String authorizationId,
                                      UUID customerId, PaymentStatus status,
                                      LocalDateTime createdAt) {
        PaymentEntity payment = new PaymentEntity();
        payment.setCaptureId(captureId);
        payment.setAuthorizationId(authorizationId);
        payment.setCustomerId(customerId);
        payment.setStatus(status);
        payment.setCreatedAt(createdAt);
        return entityManager.persist(payment);
    }

    @Test
    void captureIdEquals_filtersCorrectly() {
        Specification<PaymentEntity> spec = PaymentSpecifications.captureIdEquals("cap_1");

        List<PaymentEntity> results = paymentRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(payment1.getId());
    }

    @Test
    void captureIdEquals_nullValue_returnsAll() {
        Specification<PaymentEntity> spec = PaymentSpecifications.captureIdEquals(null);

        List<PaymentEntity> results = paymentRepository.findAll(spec);

        assertThat(results).hasSize(4);
    }

    @Test
    void authorizationIdEquals_filtersCorrectly() {
        Specification<PaymentEntity> spec = PaymentSpecifications.authorizationIdEquals("auth_3");

        List<PaymentEntity> results = paymentRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(payment3.getId());
    }

    @Test
    void customerIdEquals_filtersCorrectly() {
        Specification<PaymentEntity> spec = PaymentSpecifications.customerIdEquals(customerA);

        List<PaymentEntity> results = paymentRepository.findAll(spec);

        assertThat(results)
                .extracting(PaymentEntity::getId)
                .containsExactlyInAnyOrder(payment1.getId(), payment2.getId());
    }

    @Test
    void statusEquals_filtersCorrectly() {
        Specification<PaymentEntity> spec = PaymentSpecifications.statusEquals(PaymentStatus.CAPTURED);

        List<PaymentEntity> results = paymentRepository.findAll(spec);

        assertThat(results)
                .extracting(PaymentEntity::getId)
                .containsExactlyInAnyOrder(payment1.getId(), payment3.getId());
    }

    @Test
    void createdBetween_filtersCorrectly() {
        LocalDateTime start = LocalDateTime.now().minusDays(4);
        LocalDateTime end = LocalDateTime.now();

        Specification<PaymentEntity> spec = PaymentSpecifications.createdBetween(start, end);

        List<PaymentEntity> results = paymentRepository.findAll(spec);

        // payment4 is 10 days old, should be excluded
        assertThat(results)
                .extracting(PaymentEntity::getId)
                .containsExactlyInAnyOrder(payment1.getId(), payment2.getId(), payment3.getId());
    }

    @Test
    void createdBetween_onlyStartDate_returnsFromStartOnward() {
        LocalDateTime start = LocalDateTime.now().minusDays(4);

        Specification<PaymentEntity> spec = PaymentSpecifications.createdBetween(start, null);

        List<PaymentEntity> results = paymentRepository.findAll(spec);

        assertThat(results)
                .extracting(PaymentEntity::getId)
                .containsExactlyInAnyOrder(payment1.getId(), payment2.getId(), payment3.getId());
    }

    @Test
    void createdBetween_bothNull_returnsAll() {
        Specification<PaymentEntity> spec = PaymentSpecifications.createdBetween(null, null);

        List<PaymentEntity> results = paymentRepository.findAll(spec);

        assertThat(results).hasSize(4);
    }

    @Test
    void combinedSpecs_customerAndStatus_filtersCorrectly() {
        Specification<PaymentEntity> spec = PaymentSpecifications.customerIdEquals(customerA)
                .and(PaymentSpecifications.statusEquals(PaymentStatus.CAPTURED));

        List<PaymentEntity> results = paymentRepository.findAll(spec);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(payment1.getId());
    }

    @Test
    void combinedSpecs_allFiltersNull_returnsAllWithPagination() {
        Specification<PaymentEntity> spec = PaymentSpecifications.captureIdEquals(null)
                .and(PaymentSpecifications.authorizationIdEquals(null))
                .and(PaymentSpecifications.customerIdEquals(null))
                .and(PaymentSpecifications.statusEquals(null))
                .and(PaymentSpecifications.createdBetween(null, null));

        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<PaymentEntity> page = paymentRepository.findAll(spec, pageable);

        assertThat(page.getTotalElements()).isEqualTo(4);
        assertThat(page.getContent()).hasSize(2);
        // most recent first
        assertThat(page.getContent().get(0).getId()).isEqualTo(payment1.getId());
    }

    @Test
    void combinedSpecs_noMatches_returnsEmpty() {
        Specification<PaymentEntity> spec = PaymentSpecifications.customerIdEquals(customerA)
                .and(PaymentSpecifications.statusEquals(PaymentStatus.CANCELLED));

        List<PaymentEntity> results = paymentRepository.findAll(spec);

        assertThat(results).isEmpty();
    }
}