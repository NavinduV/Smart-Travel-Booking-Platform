package com.Smart.Travel.Booking.Platform.Payment.Service.service;

import com.Smart.Travel.Booking.Platform.Payment.Service.dto.BookingDTO;
import com.Smart.Travel.Booking.Platform.Payment.Service.dto.CreatePaymentRequest;
import com.Smart.Travel.Booking.Platform.Payment.Service.dto.PaymentDTO;
import com.Smart.Travel.Booking.Platform.Payment.Service.dto.RefundRequest;
import com.Smart.Travel.Booking.Platform.Payment.Service.entity.Payment;
import com.Smart.Travel.Booking.Platform.Payment.Service.entity.Payment.PaymentStatus;
import com.Smart.Travel.Booking.Platform.Payment.Service.exception.PaymentException;
import com.Smart.Travel.Booking.Platform.Payment.Service.exception.ResourceNotFoundException;
import com.Smart.Travel.Booking.Platform.Payment.Service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingServiceClient bookingServiceClient;

    public PaymentDTO processPayment(CreatePaymentRequest request) {
        log.info("Processing payment for booking: {}", request.getBookingId());

        // Verify booking
        BookingDTO booking = bookingServiceClient.getBookingById(request.getBookingId()).block();
        if (booking == null) {
            throw new PaymentException("Booking not found with id: " + request.getBookingId());
        }

        // Verify amount
        if (request.getAmount().compareTo(booking.getTotalAmount()) != 0) {
            log.warn("Payment amount {} does not match booking total {}", 
                    request.getAmount(), booking.getTotalAmount());
        }

        // Create payment
        Payment payment = Payment.builder()
                .bookingId(request.getBookingId())
                .userId(request.getUserId())
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .description(request.getDescription())
                .status(PaymentStatus.PENDING)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with transaction ID: {}", savedPayment.getTransactionId());

        // Process payment
        try {
            savedPayment.setStatus(PaymentStatus.PROCESSING);
            paymentRepository.save(savedPayment);

            Thread.sleep(100);

            savedPayment.setStatus(PaymentStatus.COMPLETED);
            savedPayment.setPaymentDate(LocalDateTime.now());
            Payment completedPayment = paymentRepository.save(savedPayment);

            // Update booking
            try {
                bookingServiceClient.updateBookingPaymentId(
                        request.getBookingId(), 
                        completedPayment.getId()
                ).subscribe();
            } catch (Exception e) {
                log.warn("Failed to update booking with payment ID: {}", e.getMessage());
            }

            log.info("Payment completed successfully: {}", completedPayment.getTransactionId());
            return mapToDTO(completedPayment);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            savedPayment.setStatus(PaymentStatus.FAILED);
            savedPayment.setFailureReason("Payment processing was interrupted");
            paymentRepository.save(savedPayment);
            throw new PaymentException("Payment processing failed");
        } catch (Exception e) {
            savedPayment.setStatus(PaymentStatus.FAILED);
            savedPayment.setFailureReason(e.getMessage());
            paymentRepository.save(savedPayment);
            throw new PaymentException("Payment processing failed: " + e.getMessage());
        }
    }

    public PaymentDTO refundPayment(Long paymentId, RefundRequest request) {
        log.info("Processing refund for payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentException("Only completed payments can be refunded");
        }

        if (request.getAmount().compareTo(payment.getAmount()) > 0) {
            throw new PaymentException("Refund amount cannot exceed payment amount");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setDescription(payment.getDescription() + " | Refund reason: " + request.getReason());
        Payment refundedPayment = paymentRepository.save(payment);

        log.info("Refund processed successfully: {}", refundedPayment.getTransactionId());
        return mapToDTO(refundedPayment);
    }

    public PaymentDTO cancelPayment(Long paymentId) {
        log.info("Cancelling payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new PaymentException("Cannot cancel a completed payment. Use refund instead.");
        }

        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new PaymentException("Payment is already cancelled");
        }

        payment.setStatus(PaymentStatus.CANCELLED);
        Payment cancelledPayment = paymentRepository.save(payment);

        log.info("Payment cancelled: {}", cancelledPayment.getTransactionId());
        return mapToDTO(cancelledPayment);
    }

    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        log.info("Fetching payment with id: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
        return mapToDTO(payment);
    }

    @Transactional(readOnly = true)
    public PaymentDTO getPaymentByTransactionId(String transactionId) {
        log.info("Fetching payment with transaction ID: {}", transactionId);
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with transaction ID: " + transactionId));
        return mapToDTO(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        log.info("Fetching all payments");
        return paymentRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByBookingId(Long bookingId) {
        log.info("Fetching payments for booking: {}", bookingId);
        return paymentRepository.findByBookingId(bookingId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByUserId(Long userId) {
        log.info("Fetching payments for user: {}", userId);
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByStatus(PaymentStatus status) {
        log.info("Fetching payments with status: {}", status);
        return paymentRepository.findByStatus(status).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private PaymentDTO mapToDTO(Payment payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .description(payment.getDescription())
                .failureReason(payment.getFailureReason())
                .paymentDate(payment.getPaymentDate())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
