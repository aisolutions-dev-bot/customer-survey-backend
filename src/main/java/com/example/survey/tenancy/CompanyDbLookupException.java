package com.example.survey.tenancy;

/**
 * Signals that org-api could not say which database a company owns, for a
 * reason other than the company having none. Resolution fails the request
 * rather than assuming the default database, because that assumption serves
 * one company's request from another company's data.
 */
public class CompanyDbLookupException extends RuntimeException {

  public CompanyDbLookupException(String message, Throwable cause) {
    super(message, cause);
  }

  public CompanyDbLookupException(String message) {
    super(message);
  }
}
