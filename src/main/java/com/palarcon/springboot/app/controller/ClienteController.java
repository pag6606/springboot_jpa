package com.palarcon.springboot.app.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.palarcon.springboot.app.models.dao.IClienteDao;
import com.palarcon.springboot.app.models.entity.Cliente;
import com.palarcon.springboot.app.models.service.IclienteService;
import com.palarcon.springboot.app.util.paginator.PageRender;
import org.springframework.web.multipart.MultipartFile;


@Controller
@SessionAttributes("cliente")
public class ClienteController {
	@Autowired
	private IclienteService clienteService;

	@GetMapping("/listar")
	public String listar(@RequestParam(name="page", defaultValue="0") int page,  Model model) {
		
		Pageable pageRequest = PageRequest.of(page, 5);
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		return "listar";

	}
	
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		Cliente cliente = clienteService.findOne(id);
		if (cliente==null) {
			flash.addAttribute("error", "El empleado no existe");
			return "redirect:/empleado/listado";
			
		}
		model.put("cliente", cliente);
		model.put("titulo", "Detalle empleado: " + cliente.getNombre() );
		
		return "ver";
	}

	
	@GetMapping("/form")
	public String crear(Map<String, Object> model) {
		Cliente cliente = new Cliente();
		model.put("cliente", cliente);
		model.put("titulo", "Formulario de clientes");
		return "form";
	}

	@PostMapping("/form")
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash , SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de clientes con errores");
			return "form";
		}
		if (!foto.isEmpty()) {
			String rootPath ="/home/palarcon/uploads";
			try {
				byte[] bytes = foto.getBytes();
				Path rutaCompleta = Paths.get(rootPath + "//" + foto.getOriginalFilename());
				Files.write(rutaCompleta, bytes);
				flash.addFlashAttribute("info", "ha subido correctamente '"+ foto.getOriginalFilename()+ "'");
				cliente.setFoto(foto.getOriginalFilename());
			} catch (IOException  e) {
				e.printStackTrace();
			}
		}
		
		String mensaje =(cliente.getId()!=null)?"Cliente editado con exito":"Cliente guardado con extio";
		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", mensaje);
		return "redirect:listar";
	}

	@GetMapping(value = "/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash ) {
		Cliente cliente = null;
		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("error", "No se encontro al cliente");
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", "El id del cliente no puede ser 0");
			return "redirect:/listar";
			
		}
		model.put("cliente", cliente);
		model.put("titulo", "Editar cliente");
		return "/form";
	}
	
	@GetMapping(value="/eliminar/{id}")
	public String eliminar(@PathVariable(value="id") Long id, RedirectAttributes flash) {
		if(id> 0) {
			
			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente borrado con exito");
		}
		return "redirect:/listar";
		
	}

}
