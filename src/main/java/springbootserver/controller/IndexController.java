package springbootserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


public class IndexController {

    @GetMapping("/")
    @ResponseBody
    public String index(){
        return "hello world";
    }
}
