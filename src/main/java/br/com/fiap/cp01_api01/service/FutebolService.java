package br.com.fiap.cp01_api01.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.fiap.cp01_api01.model.Futebol;
import br.com.fiap.cp01_api01.repository.FutebolRepository;

@Service
public class FutebolService {
    
    @Autowired
    private FutebolRepository repository;

    public Futebol createOrUpdate(Futebol futebol){
        return repository.save(futebol);
    }

    public Optional<Futebol> findById(Long id){
        return repository.findById(id);
    }

    public List<Futebol> findAll(){
        return repository.findAll();
    }

    public void deleteById(Long id){
        repository.deleteById(id);
    }
}
