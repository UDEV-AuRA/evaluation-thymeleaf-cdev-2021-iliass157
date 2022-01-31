package com.ipiecoles.java.eval.th330.controllers;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.net.BindException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerExceptionHandler {


    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleNoHandlerFoundException(NoHandlerFoundException e){
        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("error", "L'URL + " + e.getRequestURL() + " n'existe pas (méthode " + e.getHttpMethod() + ")");
        modelAndView.addObject("status", HttpStatus.NOT_FOUND.value());
        return modelAndView;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest req, RedirectAttributes attributes){
        return handleError(req, attributes, e.getMessage(), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView handleIllegalAccessException(IllegalArgumentException e, HttpServletRequest req, RedirectAttributes attributes){
        return handleError(req, attributes, e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ModelAndView handleEntityExistsException(EntityExistsException e, HttpServletRequest req, RedirectAttributes attributes){
        return handleError(req, attributes, e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView handleBindException(DataIntegrityViolationException e, HttpServletRequest req, RedirectAttributes attributes){
        return handleError(req, attributes, "Impossible de sauvegarder l'employé. Vérifier que le matricule n'excède pas 6 caractères.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest req, RedirectAttributes attributes){
        return handleError(req, attributes, "La valeur " + e.getValue() + " pour le paramètre " + e.getName() + " est incorrect !", HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleUnhandledException(Exception e, HttpServletRequest req, RedirectAttributes attributes){
        return handleError(req, attributes, "Une erreur technique est survenue !", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ModelAndView handleError(HttpServletRequest req, RedirectAttributes attributes, String error, HttpStatus status) {
        attributes.addFlashAttribute("type", "danger");
        attributes.addFlashAttribute("message", error);
        String referer = req.getHeader("Referer");
        if(referer != null){
            //Si on vient d'une page du site on redirige vers la page précédente
            return new ModelAndView("redirect:" + referer);
        } else {
            //Sinon on redirige vers la page d'erreur
            ModelAndView modelAndView = new ModelAndView("error", status);
            modelAndView.addObject("error", error);
            modelAndView.addObject("status", status);
            return modelAndView;
        }
    }
}
