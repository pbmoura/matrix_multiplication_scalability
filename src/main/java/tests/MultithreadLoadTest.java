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
import deploy.MultithreadLocal;
import eu.choreos.vv.analysis.AggregatePerformance;
import eu.choreos.vv.analysis.ComposedAnalysis;
import eu.choreos.vv.analysis.SaveToXML;
import eu.choreos.vv.chart.creator.MeanChartCreator;
import eu.choreos.vv.experiments.Experiment;
import eu.choreos.vv.experiments.strategy.ExperimentStrategy;
import eu.choreos.vv.experiments.strategy.ParameterScaling;
import eu.choreos.vv.increasefunctions.ExponentialIncrease;
import eu.choreos.vv.loadgenerator.LoadGeneratorFactory;
import eu.choreos.vv.loadgenerator.SequentialLoadGenerator;
import eu.choreos.vv.loadgenerator.strategy.NullStrategy;


public class MultithreadLoadTest extends Experiment<Object, Object> {
	
	private static String baseURI;
	private MultiplyMatrixService client;
	
	private static int SIZE;
	private List<DoubleArray> a;
	private List<DoubleArray> b;
	
	@Override
	public void beforeExperiment() throws Exception {
//		Thread.sleep(500);
		MultithreadLocal deployer = new MultithreadLocal();
		deployer.deploy();
		MultiplyMatrixServiceService mms = new MultiplyMatrixServiceService(new URL(baseURI + "/MultiplyMatrixService?wsdl"), new QName("multiply.matrix", "MultiplyMatrixServiceService"));
        client = mms.getPort(new QName("multiply.matrix", "MultiplyMatrixServicePort"), MultiplyMatrixService.class);
		
	}
	
	@Override
	public void beforeIteration() {
		System.out.println("Starting new iteration");
		
		SIZE = (Integer)this.getParam("size");
		
		a = generate(SIZE);
		b = generate(SIZE);
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
		
		
		SIZE = Integer.parseInt(props.getProperty("size"));
		baseURI = props.getProperty("baseURI");
		fileName = props.getProperty("resultFile");
		warmup = Integer.parseInt(props.getProperty("warmup"));
		requests = Integer.parseInt(props.getProperty("requests"));
		steps = Integer.parseInt(props.getProperty("iterations"));
		
		MultithreadLoadTest test = new MultithreadLoadTest();
		
		ExperimentStrategy scaling = new ParameterScaling("size");
		scaling.setFunction(new ExponentialIncrease(2));
		scaling.setParameterInitialValue(SIZE);
		test.setStrategy(scaling);
		
		LoadGeneratorFactory.getInstance().setStrategy(new NullStrategy());
		LoadGeneratorFactory.getInstance().setLoadGeneratorClass(SequentialLoadGenerator.class);
		
//		test.setAnalyser(new AggregatePerformance("matrix multiplication", new MeanChartCreator()));
//		test.setAnalyser(new SaveToXML(new File(fileName)));
		test.setAnalyser(new ComposedAnalysis(new SaveToXML(new File(fileName)),new AggregatePerformance("matrix multiplication", new MeanChartCreator())));
		
		test.setNumberOfRequestsPerMinute(1);
		
		test.setNumberOfRequestsPerStep(warmup);
		test.setNumberOfSteps(1);
		test.run("warm-up", false, false);
		
		test.setNumberOfRequestsPerStep(requests);
		test.setNumberOfSteps(steps);
		test.run("test1");
		

	}

}
