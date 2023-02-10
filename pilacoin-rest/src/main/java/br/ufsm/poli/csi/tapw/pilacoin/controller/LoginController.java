package br.ufsm.poli.csi.tapw.pilacoin.controller;

import br.ufsm.poli.csi.tapw.pilacoin.model.Usuario;
import br.ufsm.poli.csi.tapw.pilacoin.security.CookieUtil;
import br.ufsm.poli.csi.tapw.pilacoin.security.JwtUtils;
import br.ufsm.poli.csi.tapw.pilacoin.service.LoginService;
import br.ufsm.poli.csi.tapw.pilacoin.service.PilaService;
import br.ufsm.poli.csi.tapw.pilacoin.service.TrataThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin(origins = "*")
@Controller
public class LoginController {

    private final LoginService loginService;
    private final PilaService pilaService;

    @Autowired
    public LoginController(LoginService loginService, PilaService pilaService) {
        this.pilaService = pilaService;
        this.loginService = loginService;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/efetualogin", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView efetualogin(@RequestParam String email, @RequestParam String senha,
                              HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/index");
        System.out.println("TESTANDO");
        modelAndView.addObject("pilas", pilaService.getAllPilas());
        Usuario usuario = loginService.login(email, new BCryptPasswordEncoder().encode(senha));
        if (usuario != null) {
            var token = JwtUtils.generateToken(usuario);
            request.getSession().setAttribute("user", usuario);
            CookieUtil.setCookie("token", token, response);
        }
        //this.threadService.iniciar(0);
        return modelAndView;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/login");
        System.out.println("TESTANDO LOGIN");
        //this.threadService.iniciar(0);
        return modelAndView;
    }

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView logout(HttpServletRequest request) {
        request.getSession().invalidate();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/login");
        return modelAndView;
    }

}
