package www.mys.com.swaggerdemo.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import www.mys.com.swaggerdemo.vo.request.RequestInfo;
import www.mys.com.swaggerdemo.vo.response.ResponseInfo;

@Api(tags = "信息接口")
@RequestMapping("/api/info")
public interface InfoApi {

    //    @ApiIgnore
    @ApiOperation(value = "获取信息")
    @GetMapping("/getInfo/{key}")
    public String getInfo(@PathVariable("key") String key);

    @ApiOperation(value = "保存信息")
    @PostMapping("/saveInfo")
    public ResponseInfo saveInfo(@RequestBody RequestInfo requestInfo, BindingResult bindingResult) throws Exception;

}
