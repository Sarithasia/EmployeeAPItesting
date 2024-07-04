package com.poppulo.employee.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDataResponse {
    private User data;

    private SupportData support;

    // Getters and setters
    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }

    public SupportData getSupport() {
        return support;
    }

    public void setSupport(SupportData support) {
        this.support = support;
    }
}

