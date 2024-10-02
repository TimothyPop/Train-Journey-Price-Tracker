package com.pdf.backend.payload.response;

import com.pdf.backend.entities.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    private boolean status;
    private String message;
    private Object data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object error;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<User> adminUsers;

    public CommonResponse(boolean status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public CommonResponse(boolean b, String badRequest, Object o, Object errors) {
    }
}

