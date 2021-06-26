package red.asuka.alice.service.test;

import red.asuka.alice.commons.constant.QueueConstant;
import red.asuka.alice.mq.MQSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Tomonori
 * @mail gutrse3321@live.com
 * @data 2021-06-07 10:55
 */
@Service
public class TestService implements ITestService{

    @Autowired
    MQSender mqSender;

    @Override
    public void shotMq(String msg) throws Exception {
        mqSender.send(QueueConstant.EVENT, msg);
    }
}
