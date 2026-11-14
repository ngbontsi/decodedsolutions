package com.platform.familytree.identity.dto;

import com.platform.familytree.identity.model.Person;

import java.time.LocalDate;
import java.util.UUID;

public record CreatePersonRequest(Person.Gender gender,
                                  LocalDate birthDate,
                                  Person.DatePrecision birthDatePrecision,
                                  String biography,
                                  Person.Visibility visibility,
                                  UUID actorId) {
}