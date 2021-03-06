import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import model.Excel;
import model.GTF;
import model.Parameters;
import tools.Utils;

/**
 * @author Vincent Gardeux
 * @see vincent.gardeux@epfl.ch
 *
 */
public class BRBseqTools 
{	
	public static void main(String[] args) throws Exception
	{
		if(args.length < 1) Parameters.printHelp();
		else
		{
			String[] argsParsed = new String[args.length - 1];
			for(int i = 0; i < args.length - 1; i++) argsParsed[i] = args[i + 1];
			
			switch(args[0])
			{
				case "CreateDGEMatrix":
					System.out.println("BRBSeqTools [CreateDGEMatrix]\n");
					Parameters.loadDGE(argsParsed);
					Parameters.barcodes = Utils.readConfig();
					Parameters.BC1 = new ArrayList<String>();
					Parameters.barcodeIndex = new HashMap<String, Integer>();
					for(String B1:Parameters.barcodes) Parameters.BC1.add(B1);
					for(int i = 0; i < Parameters.BC1.size(); i++) Parameters.barcodeIndex.put(Parameters.BC1.get(i), i);
					Parameters.barcodeIndex.put("Unknown", Parameters.barcodeIndex.size());
					Utils.patterning();
					GTF.readGTF();
					DGEMatrixManager.readR1Fastq();
					DGEMatrixManager.readR2BAM();
					DGEMatrixManager.createOutputDGE();
					break;
				case "Demultiplex":
					System.out.println("BRBSeqTools [Demultiplex]\n");
					Parameters.loadDemultiplex(argsParsed);
					Parameters.barcodes = Utils.readConfig();
					Parameters.BC1 = new ArrayList<String>();
					Parameters.barcodeIndex = new HashMap<String, Integer>();
					for(String B1:Parameters.barcodes) Parameters.BC1.add(B1);
					for(int i = 0; i < Parameters.BC1.size(); i++) Parameters.barcodeIndex.put(Parameters.BC1.get(i), i);
					Parameters.barcodeIndex.put("Unknown", Parameters.barcodeIndex.size());
					Utils.patterning();
					DemultiplexingManager.demultiplex();
					break;
				case "Trim":
					System.out.println("BRBSeqTools [Trim]\n");
					Parameters.loadTrim(argsParsed);
					BufferedWriter bw_excel = new BufferedWriter(new FileWriter(Parameters.outputFolder + "brbseq.trimming.output.txt"));
					bw_excel.write("sample\tnbReads\tnbRemaininingReads\tnbContaminated\tnbPolyATrimmed\tnbRemoved\tnbPolyABefore\n");
					for(File fi:Parameters.fastqToTrim) 
					{
						System.out.println("\nTrimming " + fi.getName() + "...");
						long start = System.currentTimeMillis();
						Excel sheet = TrimmingManager.trimFastQ(fi.getAbsolutePath().replaceAll("\\\\", "/"));
						bw_excel.write(fi.getName());
						sheet.write(bw_excel);
						System.out.println("Trimmming done in " + Utils.toReadableTime(System.currentTimeMillis() - start));
					}
					bw_excel.close();
					break;
				default:
					System.err.println("This method is not implemented. Please use one of the following: [CreateDGEMatrix, Demultiplex, Trim].");
					Parameters.printHelp();
			}
		}
	}
	

}

