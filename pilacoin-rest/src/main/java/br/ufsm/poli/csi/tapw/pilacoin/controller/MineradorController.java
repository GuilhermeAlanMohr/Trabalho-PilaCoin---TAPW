package br.ufsm.poli.csi.tapw.pilacoin.controller;

import br.ufsm.poli.csi.tapw.pilacoin.service.TrataThreadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*")
@Controller
@RequestMapping("/pila")
public class MineradorController {

    private final TrataThreadService threadService;

    @Autowired
    public MineradorController(TrataThreadService threadService) {
        this.threadService = threadService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/minerador/start")
    public String startMining(HttpServletRequest request) {
        System.out.println("TESTANDO");
        this.threadService.iniciar(0);
        return "redirect:/index";
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/minerador/stop")
    public String stopMining() {
        this.threadService.pausar(0);
        return "redirect:/";
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/validacao/start")
    public String startValidation(HttpServletRequest request) {
        this.threadService.iniciar(1);
        return "redirect:/";
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/validacao/stop")
    public String stopValidation() {
        this.threadService.pausar(1);
        return "redirect:/";
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/descobrir-bloco/start")
    public String startDiscover(HttpServletRequest request) {
        this.threadService.iniciar(2);
        return "redirect:/";
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/descobrir-bloco/stop")
    public String stopDiscover() {
        this.threadService.pausar(2);
        return "redirect:/";
    }

}
