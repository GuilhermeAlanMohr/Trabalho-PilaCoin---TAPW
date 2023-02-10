package br.ufsm.poli.csi.tapw.pilacoin.controller;

import br.ufsm.poli.csi.tapw.pilacoin.service.PilaService;
import br.ufsm.poli.csi.tapw.pilacoin.service.TrataThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*")
@Controller
public class MineradorController {

    private final TrataThreadService threadService;
    private final PilaService pilaService;

    @Autowired
    public MineradorController(TrataThreadService threadService, PilaService pilaService) {

        this.threadService = threadService;
        this.pilaService = pilaService;

    }

    @CrossOrigin(origins = "*")
    @RequestMapping("/")
    @ResponseBody
    public ModelAndView formLogin(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pilas", pilaService.getAllPilas());
        System.out.println("TESTANDO 2");
        modelAndView.setViewName("/login");
        return modelAndView;
    }

    @CrossOrigin(origins = "*")
    //@GetMapping("/minerador/start")
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView home(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pilas", pilaService.getAllPilas());
        modelAndView.setViewName("/index");
        return modelAndView;
    }

    @CrossOrigin(origins = "*")
    //@GetMapping("/minerador/start")
    @RequestMapping(value = "/minerador/start", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView startMining(HttpServletRequest request) {
        System.out.println("Iniciando a Mineração");
        this.threadService.iniciar(0);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pilas", pilaService.getAllPilas());
        modelAndView.setViewName("/index");
        return modelAndView;
    }

    @CrossOrigin(origins = "*")
    //@GetMapping("/minerador/stop")
    @RequestMapping(value = "/minerador/stop", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView stopMining(HttpServletRequest request) {
        System.out.println("Parando a Mineração");
        this.threadService.pausar(0);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pilas", pilaService.getAllPilas());
        modelAndView.setViewName("/index");
        return modelAndView;
    }

    @CrossOrigin(origins = "*")
    //@GetMapping("/validacao/start")
    @RequestMapping(value="/validacao/start", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView startValidation(HttpServletRequest request) {
        System.out.println("Iniciando a Validação do PilaCoin de Outro Usuário");
        this.threadService.iniciar(1);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pilas", pilaService.getAllPilas());
        modelAndView.setViewName("/index");
        return modelAndView;
    }

    @CrossOrigin(origins = "*")
    //@GetMapping("/validacao/stop")
    @RequestMapping(value = "/validacao/stop", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView stopValidation(HttpServletRequest request) {
        System.out.println("Parando a Validação do PilaCoin de Outro Usuário");
        this.threadService.pausar(1);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pilas", pilaService.getAllPilas());
        modelAndView.setViewName("/index");
        return modelAndView;
    }

    @CrossOrigin(origins = "*")
    //@GetMapping("/descobrir-bloco/start")
    @RequestMapping(value = "/descobrir-bloco/start", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView startDiscover(HttpServletRequest request) {
        System.out.println("Iniciando Busca e Mineração de Blocos");
        this.threadService.iniciar(2);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pilas", pilaService.getAllPilas());
        modelAndView.setViewName("/index");
        return modelAndView;
    }

    @CrossOrigin(origins = "*")
    //@GetMapping("/descobrir-bloco/stop")
    @RequestMapping(value = "/descobrir-bloco/stop", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView stopDiscover(HttpServletRequest request) {
        System.out.println("Parando Busca e Mineração de Blocos");
        this.threadService.pausar(2);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pilas", pilaService.getAllPilas());
        modelAndView.setViewName("/index");
        return modelAndView;
    }

}