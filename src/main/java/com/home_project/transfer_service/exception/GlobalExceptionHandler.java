package com.home_project.transfer_service.exception;

import com.home_project.transfer_service.entity.TransferStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ProblemDetail handleAccountNotFound(AccountNotFoundException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Account not found");
        pd.setDetail("A megadott bankszámla nem létezik");
        pd.setInstance(URI.create(request.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ProblemDetail handleInsufficientFunds(InsufficientFundsException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Insufficient funds");
        pd.setDetail("Nincs elegendő fedezet");
        pd.setInstance(URI.create(request.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(DailyLimitExceededException.class)
    public ProblemDetail handleDailyLimit(DailyLimitExceededException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Daily limit exceeded");
        pd.setDetail("A napi tranzakciós limit túllépve");
        pd.setInstance(URI.create(request.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(FraudDetectionException.class)
    public ProblemDetail handleDailyLimit(FraudDetectionException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Fraud detected");
        pd.setDetail("Túl sok tranzakció");
        pd.setInstance(URI.create(request.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(TechnicalException.class)
    public ProblemDetail handleTechnical(TechnicalException ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Technical error");
        pd.setDetail("Technikai hiba történt, kérjük próbáld később");
        pd.setInstance(URI.create(request.getRequestURI()));
        return pd;
    }

    @ExceptionHandler(TransferNotCancelableException.class)
    public ProblemDetail handleTechnical(TransferStatus ex, HttpServletRequest request) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Transfer not cancelable");
        pd.setDetail("Az tranzakció már nem vonható vissza");
        pd.setInstance(URI.create(request.getRequestURI()));
        return pd;
    }
}
