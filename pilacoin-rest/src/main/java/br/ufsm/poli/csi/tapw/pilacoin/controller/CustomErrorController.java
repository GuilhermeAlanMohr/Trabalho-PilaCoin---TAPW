package br.ufsm.poli.csi.tapw.pilacoin.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    String error(HttpServletRequest request) {
        return "/error";
    }

}
