package deploy;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;

import matrix.multiply.MultiplyMatrixService;
import matrix.multiply.MultiplyMatrixServiceService;


public class MultithreadRemote extends AbstractDeployer {

	private String baseURI;
	MultiplyMatrixService client;

	public MultithreadRemote(String baseURI) {
		this.baseURI = baseURI;
	}
	
	@Override
	public void deploy() throws Exception {
		MultiplyMatrixServiceService mms = new MultiplyMatrixServiceService(new URL(baseURI + "/MultiplyMatrixService?wsdl"), new QName("multiply.matrix", "MultiplyMatrixServiceService"));
		client = mms.getPort(new QName("multiply.matrix", "MultiplyMatrixServicePort"), MultiplyMatrixService.class);
	}

	@Override
	public void scale(Map<String, Object> params) throws Exception {
		int size = (Integer)params.get("workers");
		client.setPoolSize(size);
	}

}
