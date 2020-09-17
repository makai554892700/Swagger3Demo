package www.mys.com.swaggerdemo.vo.request;

import javax.validation.constraints.NotEmpty;

public class RequestInfo {

    @NotEmpty(message = "key 不能为空")
    private String key;
    @NotEmpty(message = "data 不能为空")
    private String data;

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
        return "RequestInfo{" +
                "key='" + key + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
