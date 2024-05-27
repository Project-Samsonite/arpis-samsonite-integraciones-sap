package cl.ap.ssn.integraciones.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cl.ap.ssn.integraciones.exceptions.LecturaRespuestaException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 
 * @author David Nilo
 *
 */
@NoArgsConstructor(access = AccessLevel.NONE)
public class XmlUtils {

	public static String leerEstadoRespuesta(final String xml) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			dbFactory.setNamespaceAware(false);
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
			doc.getDocumentElement().normalize();
			// XPath para ruta del valor
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			XPathExpression xPathInvoice = xpath.compile("//Status/text()");
			// Obtener valor
			Object valor = xPathInvoice.evaluate(doc, XPathConstants.NODESET);
			NodeList nodoValor = (NodeList) valor;
			if(nodoValor.getLength() == 1) {
				return nodoValor.item(0).getNodeValue();
			}
			throw new LecturaRespuestaException("Respuesta no reconocida");
		} catch(IOException | ParserConfigurationException | SAXException | XPathExpressionException e) {
			throw new LecturaRespuestaException("", e);
		}
	}

}
