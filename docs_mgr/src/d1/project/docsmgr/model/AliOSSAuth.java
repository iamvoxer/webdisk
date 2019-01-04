package d1.project.docsmgr.model;

public class AliOSSAuth {
    private String accessKeyId;
    private String accessKeySerect;
    private String endPoint;
    private String url;
    private String bucket;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySerect() {
        return accessKeySerect;
    }

    public void setAccessKeySerect(String accessKeySerect) {
        this.accessKeySerect = accessKeySerect;
    }

    public String getEndPoint() {
        return "http://" + endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getUrl() {
        return "http://" + url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
