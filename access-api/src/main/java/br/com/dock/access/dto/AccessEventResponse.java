package br.com.dock.access.dto;

public class AccessEventResponse {
    private boolean success;
    private String message;

    public AccessEventResponse() {
    }

    public AccessEventResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AccessEventResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
