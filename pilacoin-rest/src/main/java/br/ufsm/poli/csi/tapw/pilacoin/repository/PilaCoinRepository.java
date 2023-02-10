package br.ufsm.poli.csi.tapw.pilacoin.repository;

import br.ufsm.poli.csi.tapw.pilacoin.model.PilaCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PilaCoinRepository extends JpaRepository<PilaCoin, Long> {

}
