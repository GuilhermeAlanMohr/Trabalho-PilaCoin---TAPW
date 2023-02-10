package br.ufsm.poli.csi.tapw.pilacoin.service;

import br.ufsm.poli.csi.tapw.pilacoin.model.Log;
import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import br.ufsm.poli.csi.tapw.pilacoin.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class LogService {

    private final LogRepository repository;

    @Autowired
    public LogService (LogRepository logRepository) {
        this.repository = logRepository;
    }

    @Transactional
    public Log createLog(Log log) throws RuntimeException {
        Log newLog = repository.save(log);
        return newLog;
    }

}
