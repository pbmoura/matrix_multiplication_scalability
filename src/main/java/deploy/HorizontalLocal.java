package deploy;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.MatrixMultiplicationCoordinator;
import core.MultiplyMatrixService;
import eu.choreos.vv.deployment.Deployer;

public class HorizontalLocal extends AbstractDeployer {

	private int nextPort;

	@Override
	public void deploy() throws Exception {
		String url = "http://127.0.0.1:1099/MultiplyMatrixCoordinator";
		MatrixMultiplicationCoordinator.runInstance(url);
		registerInstance("coordinator", url);
		nextPort = 1100;
	}

	@Override
	public void scale(Map<String, Object> params) throws Exception {
		int workers = (Integer)params.get("workers");
		int qtdToCreate = workers - getServiceUris("worker").size();
		for (int i = 0; i < qtdToCreate; i++)
			deployWorker(nextPort++, 1);
	}

}
