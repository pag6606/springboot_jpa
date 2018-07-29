package com.palarcon.springboot.app.util.paginator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

public class PageRender<T> {
	private String url;
	private Page<T> page;
	private int totalPaginas;
	private int nroElementospagina;
	private int paginaActual;
	private List<PageItem> paginas; 
	
	public PageRender(String url, Page<T> page) {

		this.url = url;
		this.page = page;
		this.paginas= new ArrayList<PageItem>();
		nroElementospagina=  page.getSize();
		totalPaginas = page.getTotalPages();
		paginaActual = page.getNumber() + 1;
		int desde, hasta;
		if (totalPaginas <= nroElementospagina) {
			desde = 1;
			hasta = totalPaginas;
				
		} else {
			if(paginaActual <= (nroElementospagina/2) ) {
				desde= 1;
				hasta = nroElementospagina;
			}else if(paginaActual>= totalPaginas - nroElementospagina/2 ) {
				desde = totalPaginas - nroElementospagina + 1;
				hasta = nroElementospagina;
			} else {
				desde = paginaActual - nroElementospagina/2;
				hasta = nroElementospagina;
			}
			
		}
		for (int i=0;i< hasta; i++) {
			paginas.add( new PageItem(desde + i, paginaActual== desde+ i));
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getTotalPaginas() {
		return totalPaginas;
	}

	public void setTotalPaginas(int totalPaginas) {
		this.totalPaginas = totalPaginas;
	}

	public int getPaginaActual() {
		return paginaActual;
	}

	public void setPaginaActual(int paginaActual) {
		this.paginaActual = paginaActual;
	}

	public List<PageItem> getPaginas() {
		return paginas;
	}

	public void setPaginas(List<PageItem> paginas) {
		this.paginas = paginas;
	}
	
	public boolean isFirst() {
		return page.isFirst();
	}
	
	public boolean isLast() {
		return page.isLast();
	}
	public boolean isHasNext()  {
		return page.hasNext();
	}
	public boolean isHasPrevious() {
		return page.hasPrevious();
	}
}
