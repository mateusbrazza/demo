package br.com.seuprojeto.consents.repository;

import br.com.seuprojeto.consents.model.Consent;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ConsentRepository {

    private final Map<String, Consent> database = new HashMap<>();

    public void save(Consent consent) {
        database.put(consent.getConsentId(), consent);
    }

    public Consent findById(String id) {
        return database.get(id);
    }
}
