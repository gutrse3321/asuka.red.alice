package red.asuka.alice.server.controller.test;

import red.asuka.alice.commons.constant.Constant;
import red.asuka.alice.commons.controller.BaseController;
import red.asuka.alice.commons.springExtension.view.MVF;
import red.asuka.alice.service.test.ITestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-06-07 10:26
 */
@RestController
@RequestMapping("/test")
public class TestController extends BaseController {

    @Autowired
    ITestService iTestService;

    @GetMapping("shotMq")
    public ModelAndView shotMq() throws Exception {
        iTestService.shotMq("这是一条MQ消息");
        return MVF.msgData(Constant.MsgData.Opt);
    }
}
