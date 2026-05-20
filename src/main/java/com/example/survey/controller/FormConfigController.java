package com.example.survey.controller;

import com.example.survey.dto.FormQuestionDTO;
import com.example.survey.repository.EvaluationFormTypeDetQuestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/form-questions")
public class FormConfigController {

  @Autowired
  private EvaluationFormTypeDetQuestRepository repository;

  @GetMapping("/{formType}/{skillSet}")
  public ResponseEntity<List<FormQuestionDTO>> getFormQuestions(
      @PathVariable String formType,
      @PathVariable String skillSet) {

    List<FormQuestionDTO> questions = repository
        .findByFormTypeAndSkillSetOrderByQuestionNumber(formType.toUpperCase(), skillSet.toUpperCase())
        .stream()
        .map(FormQuestionDTO::from)
        .toList();

    return ResponseEntity.ok(questions);
  }
}
