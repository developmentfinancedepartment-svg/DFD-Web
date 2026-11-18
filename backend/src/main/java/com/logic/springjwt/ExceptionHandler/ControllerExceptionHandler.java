
package com.logic.springjwt.ExceptionHandler;

import com.logic.springjwt.response.MessageResponse;
import org.hibernate.HibernateError;
import org.hibernate.HibernateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.hibernate5.HibernateQueryException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ExceptionLoginBlocked.class)
    public ResponseEntity<?> ExceptionLoginBlocked(ExceptionLoginBlocked ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse("Maximum login attempts has been reached. Login blocked for 10 minutes.Please try again later."));
    }

    @ExceptionHandler(ExceptionDataNotSaved.class)
    public ResponseEntity<?> resourceNotFoundException(ExceptionDataNotSaved ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse("Data not saved due to invalid data binding or internal server error."));
    }

    @ExceptionHandler(ExceptionInvalidSystemAccess.class)
    public ResponseEntity<?> resourceLoginBlockedDueToInvalidDataException(ExceptionInvalidSystemAccess ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse("Login blocked due to invalid system access or role not mentioned."));
    }


    @ExceptionHandler(ExceptionInvalidFileXY.class)
    public ResponseEntity<?> InvalidFileXYException(ExceptionInvalidFileXY ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse("Invalid file attached.Please check the file size and the dimension of image."));
    }

    @ExceptionHandler(ExceptionEmployeeAlreadyAssigned.class)
    public ResponseEntity<?> ExceptionEmployeeAlreadyAssigned(ExceptionEmployeeAlreadyAssigned ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse("The selected employee is already assigned to this group."));
    }

    @ExceptionHandler(ExceptionEmployeeInactiveMode.class)
    public ResponseEntity<?> ExceptionEmployeeInactiveMode(ExceptionEmployeeInactiveMode ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse("The selected employee is in inactive mode. Please reactivate."));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> MaxUploadSizeExceededException(MaxUploadSizeExceededException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse("file size exceeds. Please reupload with compression."));
    }

    @ExceptionHandler(ExceptionDataAlreadyExists.class)
    public ResponseEntity<?> ExceptionDataAlreadyExists(ExceptionDataAlreadyExists ex, WebRequest request) {
        return ResponseEntity.badRequest().body(new MessageResponse("Data already exists.Please update."));
    }


    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> BindException(BindException e, WebRequest request) {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        List<String> errorList = new ArrayList<>();

        errors.forEach((error) -> {
            Object fieldErrorsFeild = ((FieldError) error).getRejectedValue();
            String fieldErrorsDescription = ((FieldError) error).getDefaultMessage();
            errorList.add("Value rejected : '" + fieldErrorsFeild + "'.");
        });
        return ResponseEntity.badRequest().body(new MessageResponse("Invalid characters exists. Please check and validate your inputs.<br/><br/>" + errorList));
    }

    @ExceptionHandler(HibernateError.class)
    public ResponseEntity<Object> HibernateError(HibernateError ex) {
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT);
        body.put("message", ex.getLocalizedMessage());
        return new ResponseEntity<Object>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(HibernateException.class)
    public ResponseEntity<Object> HibernateException(HibernateException ex) {
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT);
        body.put("message", ex.getLocalizedMessage());
        return new ResponseEntity<Object>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(HibernateQueryException.class)
    public ResponseEntity<Object> HibernateQueryException(HibernateQueryException ex) {
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT);
        body.put("message", ex.getLocalizedMessage());
        return new ResponseEntity<Object>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> ConstraintViolationException(ConstraintViolationException ex) {

        List<String> errorList = new ArrayList<String>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errorList.add("Value rejected in '" + violation.getPropertyPath().toString().toUpperCase() + "' field. Description : " + violation.getMessage().toString().toUpperCase());
        }

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT);
        body.put("message", "Data Validation Error : " + errorList);
        return new ResponseEntity<Object>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT);
        body.put("message", "Data Validation Error : " + errors);
        return new ResponseEntity<Object>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InternalServerError.class)
    public ResponseEntity<Object> InternalServerError(InternalServerError ex
    ) {
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT);
        body.put("message", ex.getLocalizedMessage());
        return new ResponseEntity<Object>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadRequest.class)
    public ResponseEntity<Object> BadRequest(BadRequest ex
    ) {
        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.CONFLICT);
        body.put("message", ex.getLocalizedMessage());
        return new ResponseEntity<Object>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<Object> handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        final Map<String, Object> body = new HashMap<>();
        body.put("statusCode", HttpStatus.FORBIDDEN.value());
        body.put("timestamp", new java.util.Date());
        body.put("message", ex.getMessage());
        body.put("description", request.getDescription(false));
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

}
