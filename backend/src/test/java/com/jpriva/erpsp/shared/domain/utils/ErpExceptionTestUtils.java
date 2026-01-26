package com.jpriva.erpsp.shared.domain.utils;

import com.jpriva.erpsp.shared.domain.exceptions.ErpException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;

import java.util.List;
import java.util.Map;

public class ErpExceptionTestUtils {

    public static void printExceptionDetails(ErpException ex) {
        System.out.println("----------- TEST DEBUG OUTPUT -----------");
        System.out.println("Module:  " + ex.getModule());
        System.out.println("Code:    " + (ex.getCode() != null ? ex.getCode().getCode() : "N/A"));
        System.out.println("Message: " + ex.getMessage());

        if (ex instanceof ErpValidationException) {
            Map<String, List<String>> errors = ((ErpValidationException) ex).getPlainErrors();
            System.out.println("Validation Errors:");
            errors.forEach((field, error) ->
                System.out.println(" - " + field + ": " + error));
        }
        System.out.println("-----------------------------------------");
    }
}