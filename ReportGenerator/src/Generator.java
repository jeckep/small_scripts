import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.io.*;


public class Generator {
	// source file -> String[]
	private static String[] parseSourceData(String filename){
		ArrayList<String> strs = new ArrayList<String>();
		String thisLine;
		try(BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-16"))){
			while((thisLine=fin.readLine())!=null){
				if(thisLine.split("\t").length!=3){
					System.err.println("Wrong format of source_data.tsv!");
					System.exit(1);
				}
				strs.add(thisLine);
			}
		}
		catch(FileNotFoundException e){
			System.err.println("File " + filename + " not found!");
			System.exit(1);
		}
		catch(IOException e){
			System.err.println("IO error with file: " + filename);
			System.exit(1);
		}
		return strs.toArray(new String[strs.size()]);
	}
	// just("*",4) -> "****"
	private static String just(String str, int iter){
		String s = "";
		for(int i=0; i<iter; i++){
			s = s + str;
		}
		return s;
	}
	// String -> String[]
	// ("Number\tDate\tName",format) -> "| Number | 18/10/  | Ivan   |"
	//                                  "|        | 2012    | Ivanov |"
	private static String[] genReportBlock(String str,final int ... w){
		ArrayList<String> strs = new ArrayList<String>();
		String substrs[] = str.split("\t");
		Splitter splitter = new Splitter("[^a-zA-Zа-яА-Я0-9]");
		strs.add(new String("|"));
		int curStrIndex;
		int curFreeSpace;
		Boolean freeSpace = true;
		for(int i=0; i<3;i++){
			freeSpace=true;
			curStrIndex = 0;
			curFreeSpace = w[i];	
			String words[] = splitter.split(substrs[i]);
			Boolean beginLine = true;
			for(int j=0; j<words.length; j++){
				if(beginLine) strs.set(curStrIndex, strs.get(curStrIndex) + " ");
				String word = words[j];
				if(word.length()<curFreeSpace){
					if(!(word.equals(" ")&& beginLine)){
						strs.set(curStrIndex, strs.get(curStrIndex) + word);
						curFreeSpace-=word.length();
					}
					beginLine = false;
				}else if(word.length()==curFreeSpace){
					strs.set(curStrIndex, strs.get(curStrIndex) + word + " |");
					if(j!=words.length-1){
						curStrIndex++;
					}else{
						freeSpace = false;
					}
					curFreeSpace=w[i];
					beginLine = true;
				}else if((word.length()>curFreeSpace)&& beginLine){
					strs.set(curStrIndex, strs.get(curStrIndex) + word.substring(0, curFreeSpace) + " |");
					words[j] = word.substring(curFreeSpace);
					curStrIndex++;
					curFreeSpace=w[i];
					beginLine = true;
					j--;
				}else if(word.length()>curFreeSpace){
					strs.set(curStrIndex, strs.get(curStrIndex) + just(" ",curFreeSpace) + " |");
					curStrIndex++;
					curFreeSpace=w[i];
					beginLine = true;
					j--;
				}
				if(curStrIndex==strs.size() && j != words.length -1){
					strs.add(new String("|"));
					for(int col=0; col < i; col++){
						strs.set(curStrIndex, strs.get(curStrIndex) + just(" ", w[col] + 2) + "|");
					}
				}

			}
			if(freeSpace)strs.set(curStrIndex, strs.get(curStrIndex) + just(" ", curFreeSpace) + " |");
			for(int k=curStrIndex+1; k < strs.size();k++){
				strs.set(k, strs.get(k) + just(" ", w[k]+2) + "|");
			}
		}
		return strs.toArray(new String[strs.size()]);
	}
	// source data, settings -> report
	private static void generateReport(ReportSettings repSets, String [] strs, String filename){
		int pageWidth = repSets.getPageWidth();
		int pageHeight = repSets.getPageHeight();
		int col[] = new int[3];
		String titles[] = new String[3];
		col[0] = repSets.getColumns()[0].getWidth();
		col[1] = repSets.getColumns()[1].getWidth();
		col[2] = repSets.getColumns()[2].getWidth();
		titles[0] = repSets.getColumns()[0].getTitle();
		titles[1] = repSets.getColumns()[1].getTitle();
		titles[2] = repSets.getColumns()[2].getTitle();
		String header[] = genReportBlock(titles[0] + "\t" + titles[1] + "\t" + titles[2], col[0], col[1], col[2]);
		String dLine = just("-",pageWidth); 
		try(BufferedWriter fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),"UTF-16"))){
			int curFreeRows = pageHeight;
			for(String str:header){
				fout.write(str);
				fout.newLine();
			}
			curFreeRows-=header.length;
			for(String _str: strs){
				String block[] = genReportBlock(_str, col[0], col[1], col[2]);
				if(block.length < curFreeRows){
					fout.write(dLine);
					fout.newLine();
					for(String blockRow: block){
						fout.write(blockRow);
						fout.newLine();
					}
					curFreeRows=curFreeRows-block.length - 1;
				}else{
					fout.write("~");
					fout.newLine();
					for(String str:header){
						fout.write(str);
						fout.newLine();
					}
					fout.write(dLine);
					fout.newLine();
					for(String blockRow: block){
						fout.write(blockRow);
						fout.newLine();
					}
					curFreeRows=pageHeight-block.length - header.length - 1;
				}
			}
		}
		catch(IOException ioe){
			System.err.println("Can not write to output file!");
			System.exit(1);
		}	
	}
	
	public static void main(String[] args) {
		if(args.length != 3){
			System.err.println("Wrong number of arguments!");
			System.err.println("Please use: Generator settings.xml source-data.tsv example-report.txt");
			System.exit(1);
		} 
		ReportSettings repSets= new ReportSettings(args[0]);
		String sourceDataStrings[] = parseSourceData(args[1]);
		generateReport(repSets, sourceDataStrings, args[2]);	
	}
}
