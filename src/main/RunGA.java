package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPFitnessEvaluator;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.SubProgram;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.impl.GPProgram;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.util.NumberKit;

import actions.AddServerL1;
import actions.AddServerL10;
import actions.AddServerL2;
import actions.AddServerL3;
import actions.AddServerL4;
import actions.AddServerL5;
import actions.AddServerL6;
import actions.AddServerL7;
import actions.AddServerL8;
import actions.AddServerL9;
import actions.DecreaseDatabaseAThreads;
import actions.DecreaseDatabaseBThreads;
import actions.DeleteServerL1;
import actions.DeleteServerL10;
import actions.DeleteServerL2;
import actions.DeleteServerL3;
import actions.DeleteServerL4;
import actions.DeleteServerL5;
import actions.DeleteServerL6;
import actions.DeleteServerL7;
import actions.DeleteServerL8;
import actions.DeleteServerL9;
import actions.IfSuccessElse;
import actions.IncreaseDatabaseAThreads;
import actions.IncreaseDatabaseBThreads;
import actions.IncreaseTextResolution;
import actions.ReduceTextResolution;

public class RunGA {
	static boolean hasInitialPlan = true;

	static Class[] types = { CommandGene.VoidClass };
	static Class[][] argTypes = { {} };
	static int[] minDepths = new int[] { 1 };
	static int[] maxDepths = new int[] { 5 };
	public static CommandGene[][] nodeSets; // initialized in create. Thus create
																					// must
	// occur before setInitialPlan.
	static int maxNodes = 1000;

	public static void main(String args[]) {
		// used to keep track how long a plan generation takes
		long startTime = System.currentTimeMillis();

		int numberOfRuns = 1;
		boolean usingOutputFile = false;
		File outputFile = new File("/Users/zfc/Desktop/AutoGAResults.txt");
		PrintWriter writer = null;
		if (usingOutputFile) {
			try {
				writer = new PrintWriter(outputFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// the genetic program configuration object
		// Then all of the aspects of the configuration
		// object are set
		GPConfiguration gpConf = null;
		try {
			gpConf = new GPConfiguration();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1); // lazy error handling
		}
		// uses prism to evaluate fitness
		IGPFitnessEvaluator prismEvaluationFunction = new EvaluateFitness();
		gpConf.setGPFitnessEvaluator(prismEvaluationFunction);
		gpConf.setMaxInitDepth(5);
		try {
			gpConf.setPopulationSize(30);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gpConf.setCrossoverProb(0.9f);
		gpConf.setReproductionProb(0.1f);
		gpConf.setNewChromsPercent(0.3f);
		gpConf.setStrictProgramCreation(true);
		gpConf.setUseProgramCache(true);
		gpConf.setCrossoverMethod(new TreeCrossOverExtension(gpConf));
		gpConf.setNoCommandGeneCloning(false);
		try {
			gpConf.setFitnessFunction((GPFitnessFunction) prismEvaluationFunction);
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < numberOfRuns; i++) {
			if (usingOutputFile) {
				writer.println("------------------------");
				writer.println("Run: " + (i + 1));
			}
			GPGenotype gp = null;
			try {
				gp = create(gpConf);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (hasInitialPlan) {
				setInitialPlan(gp, gpConf);
			}

			gp.setVerboseOutput(true);
			int generationCount = 0;
			try {
				int maxGenerations = 30;
				while (generationCount < maxGenerations) {
					System.out.println("Starting to evolve generation");
					gp.evolve();
					System.out.println("Finished a generation");
					gp.calcFitness();
					// Do G.C. for cleanup and to avoid 100% CPU load.
					// -----------------------------------------------
					System.out.println("Finished a calculating fitness");
					printSolution(gp.getGPPopulation().determineFittestProgram(), writer,
							(generationCount == (maxGenerations - 1)));
					System.gc();
					System.out.println("Finished garbage collection");
					generationCount++;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(1);
			}
			System.out.println("Finished Running program!!");
			long endTime = System.currentTimeMillis();
			System.out.println("Total Execution Time: " + (endTime - startTime));
			if (usingOutputFile) {
				writer.println("Total Execution Time: " + (endTime - startTime));
			}
		}
		if (usingOutputFile) {
			writer.flush();
			writer.close();
		}
	}

	public static void setInitialPlan(GPGenotype gp, GPConfiguration gpConf) {

		try {
			int size = 12;
			CommandGene[] commands = new CommandGene[size];
			int[] depths = new int[size];
			commands[0] = new IfSuccessElse(gpConf);
			depths[0] = 0;
			commands[1] = new IfSuccessElse(gpConf);
			depths[1] = 1;
			commands[2] = new DeleteServerL2(gpConf);
			depths[2] = 2;
			commands[3] = new IncreaseDatabaseBThreads(gpConf);
			depths[3] = 2;
			commands[4] = new AddServerL1(gpConf);
			depths[4] = 2;
			commands[5] = new SubProgram(gpConf, new Class[] { CommandGene.VoidClass,
					CommandGene.VoidClass }, true);
			depths[5] = 1;
			commands[6] = new IncreaseDatabaseBThreads(gpConf);
			depths[6] = 2;
			commands[7] = new IncreaseDatabaseBThreads(gpConf);
			depths[7] = 2;
			commands[8] = new IfSuccessElse(gpConf);
			depths[8] = 1;
			commands[9] = new DeleteServerL1(gpConf);
			depths[9] = 2;
			commands[10] = new IncreaseDatabaseAThreads(gpConf);
			depths[10] = 2;
			commands[11] = new IncreaseDatabaseBThreads(gpConf);
			depths[11] = 2;

			/*
			 * int size = 13;
			 * 
			 * CommandGene[] commands = new CommandGene[size]; int[] depths = new
			 * int[size]; commands[0] = new SubProgram(gpConf, new Class[] {
			 * CommandGene.VoidClass, CommandGene.VoidClass }, true);
			 * 
			 * depths[0] = 0; commands[1] = new IncreaseDatabaseAThreads(gpConf);
			 * depths[1] = 1; commands[2] = new SubProgram(gpConf, new Class[] {
			 * CommandGene.VoidClass, CommandGene.VoidClass }, true); depths[2] = 1;
			 * commands[3] = new ReduceTextResolution(gpConf); depths[3] = 2;
			 * commands[4] = new SubProgram(gpConf, new Class[] {
			 * CommandGene.VoidClass, CommandGene.VoidClass }, true); depths[4] = 2;
			 * commands[5] = new IncreaseDatabaseAThreads(gpConf); depths[5] = 3;
			 * commands[6] = new SubProgram(gpConf, new Class[] {
			 * CommandGene.VoidClass, CommandGene.VoidClass }, true); depths[6] = 3;
			 * commands[7] = new IncreaseDatabaseAThreads(gpConf); depths[7] = 4;
			 * commands[8] = new SubProgram(gpConf, new Class[] {
			 * CommandGene.VoidClass, CommandGene.VoidClass }, true); depths[8] = 4;
			 * commands[9] = new IncreaseDatabaseAThreads(gpConf); depths[9] = 5;
			 * commands[10] = new SubProgram(gpConf, new Class[] {
			 * CommandGene.VoidClass, CommandGene.VoidClass }, true); depths[10] = 5;
			 * commands[11] = new DeleteServerL1(gpConf); depths[11] = 6; commands[12]
			 * = new DeleteServerL2(gpConf); depths[12] = 6;
			 */
			ProgramChromosome chromosome = new ProgramChromosome(gpConf, size);
			chromosome.setFunctions(commands);

			Field depthArray = chromosome.getClass().getDeclaredField("m_depth");
			depthArray.setAccessible(true);
			depthArray.set(chromosome, depths);
			GPProgram prog;

			prog = new GPProgram(gpConf, types, argTypes, nodeSets, minDepths,
					maxDepths, maxNodes);

			prog.setChromosome(0, chromosome);
			EvaluateFitness ef = new EvaluateFitness();
			System.out.println("Evaluating default plan");
			ef.evaluate(prog);
			System.out.println("Finished Evaluating default plan");

			gp.addFittestProgram(prog);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("unable to set initial plan");
			System.exit(1);
		}
	}

	/**
	 * Sets up the functions to use and other parameters. Then creates the initial
	 * genotype.
	 * 
	 * @return the genotype created
	 * @author Klaus Meffert
	 * @throws Exception
	 * @since 3.0
	 */
	public static GPGenotype create(GPConfiguration gpConf) throws Exception {
		CommandGene[][] nodeSetsInit = { {
				// new SubProgram(gpConf, new Class[] { CommandGene.VoidClass,
				// CommandGene.VoidClass, CommandGene.VoidClass }, true),
				new SubProgram(gpConf, new Class[] { CommandGene.VoidClass,
						CommandGene.VoidClass }, true),
				// new SubProgram(gpConf,
				// new Class[] {
				// CommandGene.VoidClass, // nonclassic
				// CommandGene.VoidClass, CommandGene.VoidClass,
				// CommandGene.VoidClass }),
				new IfSuccessElse(gpConf),
				new AddServerL1(gpConf),
				new AddServerL2(gpConf),
				new AddServerL3(gpConf),
				new AddServerL4(gpConf),
				new AddServerL5(gpConf),
				new AddServerL6(gpConf),
				new AddServerL7(gpConf),
				new AddServerL8(gpConf),
				new AddServerL9(gpConf),
				new AddServerL10(gpConf),
				new DeleteServerL1(gpConf),
				new DeleteServerL2(gpConf), // nonclassic
				new DeleteServerL3(gpConf), new DeleteServerL4(gpConf),
				new DeleteServerL5(gpConf), new DeleteServerL6(gpConf),
				new DeleteServerL7(gpConf),
				new DeleteServerL8(gpConf),
				new DeleteServerL9(gpConf),
				new DeleteServerL10(gpConf),
				new IncreaseDatabaseAThreads(gpConf),
				new IncreaseDatabaseBThreads(gpConf), // nonclassic
				new DecreaseDatabaseAThreads(gpConf),
				new DecreaseDatabaseBThreads(gpConf),
				new IncreaseTextResolution(gpConf), // nonclassic
				new ReduceTextResolution(gpConf), // nonclassic
		} };
		nodeSets = nodeSetsInit;
		// Create genotype with initial population.
		// ----------------------------------------
		return GPGenotype.randomInitialGenotype(gpConf, types, argTypes, nodeSets,
				minDepths, maxDepths, maxNodes, new boolean[] { true }, true);
	}

	public static void printSolution(IGPProgram a_best, PrintWriter writer,
			boolean lastGeneration) {
		if (a_best == null) {
			System.out.println("No best solution (null)");
			if (writer != null) {
				writer.println("No best solution (null)");
			}
			return;
		}
		double bestValue = a_best.getFitnessValue();
		if (Double.isInfinite(bestValue)) {
			System.out.println("No best solution (infinite)");
			if (writer != null) {
				writer.println("No best solution (infinite)");
			}
			return;
		}
		System.out.println("Best solution fitness: "
				+ NumberKit.niceDecimalNumber(bestValue, 2));
		System.out.println("Best solution: " + a_best.toStringNorm(0));
		String depths = "";
		int size = a_best.size();
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				depths += " / ";
			}
			depths += a_best.getChromosome(i).getDepth(0);
		}
		if (size == 1) {
			System.out.println("Depth of chrom: " + depths);
		} else {
			System.out.println("Depths of chroms: " + depths);
		}
		if (writer != null && lastGeneration) {
			writer.println("Best solution fitness: "
					+ NumberKit.niceDecimalNumber(bestValue, 2));
			writer.println("Best solution: " + a_best.toStringNorm(0));
			if (size == 1) {
				writer.println("Depth of chrom: " + depths);
			} else {
				writer.println("Depths of chroms: " + depths);
			}
		}
	}
}
