package deploy;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.ws.Endpoint;

import core.MultiplyMatrixService;
import eu.choreos.vv.deployment.Deployer;

public abstract class AbstractDeployer implements Deployer {

	private Map<String, List<String>> serviceUris = new HashMap<String, List<String>>();

	@Override
	public List<String> getServiceUris(String serviceName) {
		// TODO Auto-generated method stub
		return serviceUris.get(serviceName);
	}


	protected Endpoint deployWorker(int port, int poolSize) throws RemoteException {
		String url = "http://127.0.0.1:" + port
				+ "/MultiplyMatrixService";
		Endpoint e = MultiplyMatrixService.runInstance(url, poolSize);
		registerInstance("worker", url);
		return e;
	}

	protected void registerInstance(String service, String url) {
		List<String> list = serviceUris.get(service);
		if (list == null) {
			list = new ArrayList<String>();
			serviceUris.put(service, list);
		}
		list.add(url);
	}

}
