package www.mys.com.swaggerdemo.controller;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import www.mys.com.swaggerdemo.api.InfoApi;
import www.mys.com.swaggerdemo.vo.request.RequestInfo;
import www.mys.com.swaggerdemo.vo.response.ResponseInfo;

import java.util.HashMap;

@RestController
public class InfoController implements InfoApi {

    private final HashMap<String, String> kv = new HashMap<>();

    @Override
    public String getInfo(String key) {
        return kv.get(key);
    }

    @Override
    public ResponseInfo saveInfo(RequestInfo requestInfo, BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            throw new Exception(bindingResult.getFieldError().getDefaultMessage());
        }
        kv.put(requestInfo.getKey(), requestInfo.getData());
        return new ResponseInfo(requestInfo.getKey(), requestInfo.getData());
    }
}
