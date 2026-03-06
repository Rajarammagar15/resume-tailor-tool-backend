//package com.rajaram.resumetailor.controller;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.ErrorResponse;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
//@RestControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(PdfParsingException.class)
//    public ResponseEntity<ErrorResponse> handlePdfError(PdfParsingException e) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//            .body(new ErrorResponse("PDF_PARSE_ERROR", e.getMessage()));
//    }
//
//    @ExceptionHandler(OpenAiException.class)
//    public ResponseEntity<ErrorResponse> handleAiError(OpenAiException e) {
//        // Don't expose OpenAI errors to users
//        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
//            .body(new ErrorResponse("AI_SERVICE_ERROR", "AI service temporarily unavailable"));
//    }
//}