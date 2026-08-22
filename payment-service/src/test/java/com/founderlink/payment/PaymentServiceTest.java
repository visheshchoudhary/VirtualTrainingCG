package com.founderlink.payment;

import com.founderlink.payment.dto.PaymentRequest;
import com.founderlink.payment.dto.PaymentResponse;
import com.founderlink.payment.entity.Payment;
import com.founderlink.payment.event.EventPublisher;
import com.founderlink.payment.exception.InvalidInputException;
import com.founderlink.payment.exception.ResourceNotFoundException;
import com.founderlink.payment.repository.PaymentRepository;
import com.founderlink.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	@Mock
	private PaymentRepository paymentRepository;
	@Mock
	private EventPublisher eventPublisher;
	@InjectMocks
	private PaymentService paymentService;

	private Payment payment;
	private PaymentRequest request;

	@BeforeEach
	void setUp() {
		payment = new Payment();
		payment.setId(1L);
		payment.setInvestmentId(1L);
		payment.setSenderId(2L);
		payment.setReceiverId(3L);
		payment.setAmount(10000.0);
		payment.setPaymentMethod("BANK_TRANSFER");
		payment.setStatus("PENDING");
		payment.setCreatedAt(LocalDateTime.now());

		request = new PaymentRequest();
		request.setInvestmentId(1L);
		request.setSenderId(2L);
		request.setReceiverId(3L);
		request.setAmount(10000.0);
		request.setPaymentMethod("BANK_TRANSFER");
	}

	@Test
	void makePayment_success() {
		when(paymentRepository.save(any())).thenReturn(payment);
		PaymentResponse response = paymentService.makePayment(request);
		assertNotNull(response);
		assertEquals("PENDING", response.getStatus());
		assertEquals(10000.0, response.getAmount());
	}

	@Test
	void makePayment_senderEqualsReceiver_throwsInvalidInputException() {
		request.setReceiverId(2L);
		assertThrows(InvalidInputException.class, () -> paymentService.makePayment(request));
	}

	@Test
	void makePayment_nullSenderId_throwsInvalidInputException() {
		request.setSenderId(null);
		assertThrows(InvalidInputException.class, () -> paymentService.makePayment(request));
	}

	@Test
	void markSuccess_success() {
		when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
		when(paymentRepository.save(any())).thenReturn(payment);
		doNothing().when(eventPublisher).publishPaymentSuccess(any(), any(), any(), any(), any());

		PaymentResponse response = paymentService.markSuccess(1L);

		assertNotNull(response);
		verify(eventPublisher, times(1)).publishPaymentSuccess(any(), any(), any(), any(), any());
	}

	@Test
	void markSuccess_notFound_throwsResourceNotFoundException() {
		when(paymentRepository.findById(99L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> paymentService.markSuccess(99L));
	}
}
