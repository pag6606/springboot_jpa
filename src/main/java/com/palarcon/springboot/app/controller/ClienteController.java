package com.palarcon.springboot.app.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
	// logs por consola
	private final Logger log = LoggerFactory.getLogger(getClass());

	@GetMapping("/listar")
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

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
		if (cliente == null) {
			flash.addAttribute("error", "El empleado no existe");
			return "redirect:/empleado/listado";

		}
		model.put("cliente", cliente);
		model.put("titulo", "Detalle empleado: " + cliente.getNombre());

		return "ver";
	}

	@GetMapping(value = "/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
		Path pathFoto = Paths.get("uploads").resolve(filename).toAbsolutePath();
		log.info("pathFoto: " + pathFoto);
		Resource recurso = null;
		try {
			recurso = new UrlResource(pathFoto.toUri());
			if (!recurso.exists() && !recurso.isReadable()) {
				throw new RuntimeException("Error: no se puede cargar la imagen: " + pathFoto.toString());
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
				.body(recurso);

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
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de clientes con errores");
			return "form";
		}
		if (!foto.isEmpty()) {
			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {
				Path rootPath= Paths.get("uploads").resolve(cliente.getFoto()).toAbsolutePath();
				File archivo = rootPath.toFile();
				if (archivo.exists() && archivo.canRead()) {
					archivo.delete();
				
				}

			}
			String uniqueFilename = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
			Path rootPath = Paths.get("uploads").resolve(uniqueFilename);
			Path absolutePath = rootPath.toAbsolutePath();
			log.info("rootPath: " + rootPath);
			try {

				Files.copy(foto.getInputStream(), absolutePath);
				flash.addFlashAttribute("info", "ha subido correctamente '" + uniqueFilename + "'");
				cliente.setFoto(uniqueFilename);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String mensaje = (cliente.getId() != null) ? "Cliente editado con exito" : "Cliente guardado con extio";
		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", mensaje);
		return "redirect:listar";
	}

	@GetMapping(value = "/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {
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

	@GetMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
		if (id > 0) {
			Cliente cliente = clienteService.findOne(id);

			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente borrado con exito");
			Path rootPath = Paths.get("uploads").resolve(cliente.getFoto()).toAbsolutePath();
			File archivo = rootPath.toFile();
			if (archivo.exists() && archivo.canRead()) {
				if (archivo.delete()) {
					flash.addFlashAttribute("info", "foto " + cliente.getFoto() + " eliminada con exito");
				}
			}
		}
		return "redirect:/listar";

	}

}
