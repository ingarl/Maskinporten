package no.svv.dto;

public class JWT {
    private String header;
    private String body;
    private String signature;

    public JWT(String header, String body) {
        this.header = header;
        this.body = body;
    }

    public JWT(String header, String body, String signature) {
        this.header = header;
        this.body = body;
        this.signature = signature;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "JWT {" +
                "\n header='" + header + '\'' +
                ",\n body='" + body + '\'' +
                ",\n signature='" + signature + '\'' +
                "\n"+ '}';
    }
}
