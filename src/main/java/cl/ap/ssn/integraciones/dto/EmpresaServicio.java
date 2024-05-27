package cl.ap.ssn.integraciones.dto;

import java.io.Serializable;

import cl.ap.ssn.integraciones.enums.EmpresaEnum;
import cl.ap.ssn.integraciones.enums.PaisEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @author David
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaServicio implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8396753370419338828L;

	private PaisEnum pais;
	private EmpresaEnum empresa;
	private Short codigo;

}
