package red.asuka.alice.commons.controller;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author Tomonori
 * @Mail gutrse3321@live.com
 * @Date 2020-09-20 2:11 AM
 */
public abstract class HttpServletController {

    @Getter
    @Autowired
    protected HttpServletRequest request;

    @Getter
    @Autowired
    protected HttpServletResponse response;
}
