package com.platform.familytree.evidence.dto;

import com.platform.familytree.evidence.model.Source;

public record CreateSourceRequest(Source.Type sourceType,
                                  String title,
                                  String authorOrSpeaker) {
}