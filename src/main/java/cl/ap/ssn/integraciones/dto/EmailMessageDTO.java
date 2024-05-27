package cl.ap.ssn.integraciones.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
public class EmailMessageDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7605103309082449620L;

	private String asunto;
	private String origen;
	@Builder.Default
	private List<String> receptores = new ArrayList<>();
	@Builder.Default
	private List<String> receptoresEnCopia = new ArrayList<>();
	private String contenido;

}
