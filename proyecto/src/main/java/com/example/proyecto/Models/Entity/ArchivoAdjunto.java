package com.example.proyecto.Models.Entity;
import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pasarela_archivo_adjunto")
@Getter
@Setter
public class ArchivoAdjunto implements Serializable{

    private static final long serialVersionUID = 2629195288020321924L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_archivo_adjunto;
    private String nombre_archivo;
    private String ruta_archivo;
    private String tipo_archivo;
    private String estado;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "archivoAdjunto", fetch = FetchType.LAZY)
	private List<Proyecto> proyecto;


}
