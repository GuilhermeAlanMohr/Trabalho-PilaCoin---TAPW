package br.ufsm.poli.csi.tapw.pilacoin.repository;

import br.ufsm.poli.csi.tapw.pilacoin.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}
