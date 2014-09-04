package tests;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;

import client.matrix.MultiplyMatrixClient;
import client.matrix.MultiplyMatrixClientService;
import deploy.HorizontalLocal;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ComposedStrategy;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.loadgenerator.LoadGeneratorFactory;
import eu.choreos.vv.loadgenerator.strategy.NullStrategy;


public class LocalTest extends Experiment<Integer, Integer> {
	
	private URL wsdl;
	private MultiplyMatrixClient client;
	
	@Override
	public void beforeExperiment() throws MalformedURLException {
		URL baseUrl;
        baseUrl = client.matrix.MultiplyMatrixClientService.class.getResource(".");
        
        wsdl = new URL(baseUrl, "http://localhost:1235/multiplyMatrices?wsdl");
		
		MultiplyMatrixClientService mmcs = new MultiplyMatrixClientService(wsdl, new QName("matrix.client", "MultiplyMatrixClientService"));
		client = mmcs.getMultiplyMatrixClientPort();
	}
	
	

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		LocalTest test = new LocalTest();
		test.setDeployer(new HorizontalLocal());
		
		ExperimentStrategy scaling = new ParameterScaling("workers");
		scaling.setFunction(new ExponentialIncrease(2));
		scaling.setParameterInitialValue(1);
		test.setStrategy(scaling);
		
		LoadGeneratorFactory.getInstance().setStrategy(new NullStrategy());
		
		test.setAnalyser(new AggregatePerformance("matrix multiplication", new MeanChartCreator()));
		
		test.setNumberOfRequestsPerStep(10);
		test.setNumberOfSteps(4);
		test.run("test1");
		

	}

}
