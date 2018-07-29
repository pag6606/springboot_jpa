package com.palarcon.springboot.app.models.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.palarcon.springboot.app.models.dao.IClienteDao;
import com.palarcon.springboot.app.models.entity.Cliente;

@Service
public class ClienteServiceImpl implements IclienteService{

	@Autowired
	private IClienteDao clienteDao;
	
	@Transactional(readOnly = true)
	public List<Cliente> findAll() {
	
		return (List<Cliente>) clienteDao.findAll();
	}

	@Transactional
	@Override
	public void save(Cliente cliente) {
		clienteDao.save(cliente);
	}
	
	@Transactional(readOnly = true)
	@Override
	public Cliente findOne(Long id) {
		return clienteDao.findById(id).orElse(null);
	}

	@Transactional
	@Override
	public void delete(Long id) {
		
		clienteDao.deleteById(id) ;
		
	}
	
}
