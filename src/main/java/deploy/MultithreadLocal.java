package deploy;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Endpoint;

import matrix.multiply.MultiplyMatrixService;
import matrix.multiply.MultiplyMatrixServiceService;


public class MultithreadLocal extends AbstractDeployer {

	private Endpoint endpoint;
	MultiplyMatrixService client;

	@Override
	public void deploy() throws Exception {
		endpoint = deployWorker(1234, 1);
		MultiplyMatrixServiceService mms = new MultiplyMatrixServiceService(new URL("http://127.0.0.1:1234/MultiplyMatrixService?wsdl"), new QName("multiply.matrix", "MultiplyMatrixServiceService"));
		client = mms.getPort(new QName("multiply.matrix", "MultiplyMatrixServicePort"), MultiplyMatrixService.class);
	}

	@Override
	public void scale(Map<String, Object> params) throws Exception {
		endpoint.stop();
		int size = (Integer)params.get("workers");
		endpoint = deployWorker(1234, size);
		
//		client.setPoolSize(size);
	}

}
