package tests;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

import matrix.multiply.MultiplyMatrixService;
import matrix.multiply.MultiplyMatrixServiceService;
import net.java.dev.jaxb.array.DoubleArray;
import deploy.MultithreadRemote;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.loadgenerator.LoadGeneratorFactory;
import eu.choreos.vv.loadgenerator.SequentialLoadGenerator;
import eu.choreos.vv.loadgenerator.strategy.NullStrategy;


public class MultithreadTest extends Experiment<Object, Object> {
	
	private static String baseURI;
	private MultiplyMatrixService client;
	
	private static final int SIZE = 512;
	private List<DoubleArray> a;
	private List<DoubleArray> b;
	
	@Override
	public void beforeExperiment() throws Exception {
//		Thread.sleep(500);
		MultiplyMatrixServiceService mms = new MultiplyMatrixServiceService(new URL(baseURI + "/MultiplyMatrixService?wsdl"), new QName("multiply.matrix", "MultiplyMatrixServiceService"));
        client = mms.getPort(new QName("multiply.matrix", "MultiplyMatrixServicePort"), MultiplyMatrixService.class);
		
        a = generate(SIZE);
        b = generate(SIZE);
	}
	
	@Override
	public void beforeIteration() {
		System.out.println("Starting new iteration");
	}
	
	
	
	@Override
	public Integer request(Object p) throws Exception {
		
		List<DoubleArray> M = client.multiply(a, b, SIZE, 0, SIZE);
		
		return null;
	}
	
	private List<DoubleArray> generate(int size) {
		List<DoubleArray> matrix = new ArrayList<DoubleArray>();
		for(int i = 0; i < size; i++) {
			DoubleArray array = new DoubleArray();
			matrix.add(array);
			for(int j = 0; j < size; j++)
				array.getItem().add(Math.random()*10);
		}
		return matrix;
	}
	
	

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		int warmup,requests,steps;
		String fileName;
		
		props.load(new FileReader("test.properties"));
		
		
		baseURI = props.getProperty("baseURI");//args[0];
		fileName = props.getProperty("resultFile");
		warmup = Integer.parseInt(props.getProperty("warmup"));
		requests = Integer.parseInt(props.getProperty("requests"));
		steps = Integer.parseInt(props.getProperty("iterations"));
		
		MultithreadTest test = new MultithreadTest();
		test.setDeployer(new MultithreadRemote(baseURI));
		
		ExperimentStrategy scaling = new ParameterScaling("workers");
		scaling.setFunction(new ExponentialIncrease(2));
		scaling.setParameterInitialValue(1);
		test.setStrategy(scaling);
		
		LoadGeneratorFactory.getInstance().setStrategy(new NullStrategy());
		LoadGeneratorFactory.getInstance().setLoadGeneratorClass(SequentialLoadGenerator.class);
		
//		test.setAnalyser(new AggregatePerformance("matrix multiplication", new MeanChartCreator()));
		test.setAnalyser(new SaveToXML(new File(fileName)));
		
		test.setNumberOfRequestsPerMinute(1);
		
		test.setNumberOfRequestsPerStep(warmup);
		test.setNumberOfSteps(1);
		test.run("warm-up", false, false);
		
		test.setNumberOfRequestsPerStep(requests);
		test.setNumberOfSteps(steps);
		test.run("test1");
		

	}

}
