package www.mys.com.swaggerdemo.vo.response;

public class ResponseInfo {

    private String key;
    private String data;

    public ResponseInfo() {
    }

    public ResponseInfo(String key, String data) {
        this.key = key;
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseInfo{" +
                "key='" + key + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
