package com.jpriva.erpsp.shared.domain.utils;

import com.jpriva.erpsp.shared.domain.exceptions.ErpException;
import com.jpriva.erpsp.shared.domain.exceptions.ErpValidationException;

public class ErpExceptionTestUtils {

    public static void printExceptionDetails(ErpException ex) {
        System.out.println("----------- TEST DEBUG OUTPUT -----------");
        System.out.println("Module:  " + ex.getModule());
        System.out.println("Code:    " + (ex.getCode() != null ? ex.getCode().getCode() : "N/A"));
        System.out.println("Message: " + ex.getMessage());

        if (ex instanceof ErpValidationException) {
            var errors = ((ErpValidationException) ex).getValidationErrors().errors();
            System.out.println("Validation Errors:");
            errors.forEach(error ->
                System.out.println(" - " + error.getField() + ": " + error.getMessage()));
        }
        System.out.println("-----------------------------------------");
    }
}