package com.nisa.itsm.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {

    @NotBlank(message = "Content cannot be empty") // @NotBlank → boş comment engeller
    private String content;

    @NotNull(message = "Internal flag must be provided") // @NotNull → internal null olamaz (çok kritik)
    private Boolean internal;
}
