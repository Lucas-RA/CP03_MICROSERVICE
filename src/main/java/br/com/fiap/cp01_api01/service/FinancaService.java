package br.com.fiap.cp01_api01.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.cp01_api01.model.Financa;
import br.com.fiap.cp01_api01.repository.FinancaRepository;

@Service
public class FinancaService {

    @Autowired
    private FinancaRepository repository;

    public Financa createOrUpdate(Financa financa){
        return repository.save(financa);
    }

    public Optional<Financa> findById(Long id){
        return repository.findById(id);
    }

    public List<Financa> findAll(){
        return repository.findAll();
    }

    public void deleteById(Long id){
        repository.deleteById(id);
    }

    
    
}
