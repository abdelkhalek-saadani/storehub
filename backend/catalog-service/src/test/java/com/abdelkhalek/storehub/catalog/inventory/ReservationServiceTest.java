package com.abdelkhalek.storehub.catalog.inventory;

import com.abdelkhalek.storehub.catalog.inventory.entity.Reservation;
import com.abdelkhalek.storehub.catalog.inventory.repository.ReservationRepository;
import com.abdelkhalek.storehub.catalog.inventory.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @InjectMocks
    private ReservationService reservationService;

    @Test
    void setReservationsOrderId_setsOrderId_whenReservationFound() {
        UUID reservationId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Reservation reservation = new Reservation();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        reservationService.setReservationsOrderId(List.of(reservationId), orderId);

        assertThat(reservation.getOrderId()).isEqualTo(orderId);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void setReservationsOrderId_logsError_andThrows_whenReservationNotFound() {
        UUID missingId = UUID.randomUUID();
        UUID foundId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Reservation reservation = new Reservation();

        when(reservationRepository.findById(missingId)).thenReturn(Optional.empty());
        when(reservationRepository.findById(foundId)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(
                () -> reservationService.setReservationsOrderId(List.of(missingId, foundId), orderId))
                .isInstanceOf(NoSuchElementException.class);

        assertThat(reservation.getOrderId()).isEqualTo(orderId);

        verify(reservationRepository).save(reservation);
        verify(reservationRepository, never()).save(argThat(Objects::isNull));
    }
}