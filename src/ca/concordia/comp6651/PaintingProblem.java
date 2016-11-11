package ca.concordia.comp6651;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class PaintingProblem {

	//Change to test file
	private static final String INPUT_FILE_NAME = "./ProgrammingAssignment2SampleInput2.txt";
	private static final String OUTPUT_FILE_NAME = "./ProgrammingAssignment2SampleOutput2.txt";
	//End
	private String inputData[];
	private int outputData[];
	private int minNum[][] = new int[50][50];

	private int getMinNumber(String str, int i, int j) {
		if(i<=0||j<=0||i>str.length()||j>str.length()||i>j) 
			return 0;
		if(minNum[i-1][j-1] < Integer.MAX_VALUE) {
			return minNum[i-1][j-1];
		}
		if(i==j) {
			minNum[i-1][j-1] = 1;//A
		} else if(j-i==1) {
			minNum[i-1][j-1] = 2;//AB
		} else if(j-i==2) {
			if(str.charAt(i-1) == str.charAt(j-1)) {
				minNum[i-1][j-1] = 2;//ABA
			} else {
				minNum[i-1][j-1] = 3;//ABC
			}
		} else if(j-i==3) {
			if(str.charAt(i-1) != str.charAt(j-1-1)&&str.charAt(i-1) != str.charAt(j-1)
					&&str.charAt(i+1-1) != str.charAt(j-1)) {
				minNum[i-1][j-1] = 4;//ABCD
			} else {
				minNum[i-1][j-1] = 3;//ABCA,ABCB,ABAC
			}
		} else if(j-i==4) {
			if(str.charAt(i-1) == str.charAt(j-1)&&str.charAt(i+1-1) == str.charAt(j-1-1) 
					|| str.charAt(i-1) == str.charAt(j-1)&&str.charAt(i-1) == str.charAt(i+2-1)) {
				minNum[i-1][j-1] = 3;//ABCBA,ABABA
			} else if(str.charAt(i-1) != str.charAt(i+2-1)&&str.charAt(i-1) != str.charAt(j-1)
					&&str.charAt(i+1-1) != str.charAt(j-1-1)&&str.charAt(i+2-1) != str.charAt(j-1)
					&&str.charAt(i+1-1) != str.charAt(j-1)&&str.charAt(i-1) != str.charAt(j-1-1)) {
				minNum[i-1][j-1] = 5;//ABCDE
			} else {
				minNum[i-1][j-1] = 4;//other
			}
		} else {
			if(str.charAt(i-1) == str.charAt(j-1)) {
				boolean exist = false;
				char c = str.charAt(i-1);
				int n = 2;
				for(int k=i+1;k<j;k++) {
					if(c==str.charAt(k-1)) {
						n++;
						exist = true;
					}
				}
				if(!exist) {
					if(minNum[i-1+1][j-1-1] < Integer.MAX_VALUE) {
						minNum[i-1][j-1] = minNum[i-1+1][j-1-1]+1;
					} else {
						minNum[i-1][j-1] = getMinNumber(str, i+1, j-1)+1;
					}
				} else {
					//A divide to individual parts
					int[] head = new int[n];
					head[0] = i;
					head[n-1] = j;
					int m = 1;
					for(int k=i+1;k<j&&m<n-1;k++) {
						if(c==str.charAt(k-1)) {
							head[m] = k;
							m++;
						}
					}
					int min1 = Integer.MAX_VALUE;//1;
					int min = Integer.MAX_VALUE;
					int sum = Integer.MAX_VALUE;
					for(int r=1;r<=n-1;r++) {
						for(int p=1;p+r<=n;p++) {
							sum = getMinNumber(str,head[0]+1,head[p-1]-1)+getMinNumber(str,head[p-1]+1,head[p+r-1]-1)+getMinNumber(str,head[p+r-1]+1,head[n-1]-1);
							if(sum < min) {
								min = sum;
							}
						}
					}
					min1= min+1;
					int min2 = getMinNumber(str, i+1, j-1)+1;
					if(min1 < min2) {
						minNum[i-1][j-1] = min1;
					} else {
						minNum[i-1][j-1] = min2;
					}
				}
			} else {
				int min = Integer.MAX_VALUE;
				int sum = 0;
				for(int r=1;r<=j-i;r++) {
					for(int p=i;p+r<=j+1;p++) {
						sum = getMinNumber(str,i,p-1)+getMinNumber(str,p,p+r-1)+getMinNumber(str,p+r,j);
						if(sum < min) {
							min = sum;
						}
					}
				}
				minNum[i-1][j-1] = min;
			}
		}
		return minNum[i-1][j-1];
	}
	
	private void calculateSweep(String f, String[] out) {
		int index = 0;
		try {
			File file = new File(f);
			if (file.isFile() && file.exists() && out!= null) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTXT = null;
				while ((lineTXT = bufferedReader.readLine()) != null) {
					out[index] = lineTXT.toString().trim();
					if(index < out.length) {
						String newWord = "";
						for(newWord = removeDuplicate(removeSingle(index, out[index]));true;) {
							if(!newWord.equals(out[index])) {
								out[index] = newWord;
								newWord = removeDuplicate(removeSingle(index, out[index]));
							} else {
								break;
							}
						}
						minNum = new int[out[index].length()][out[index].length()];
						for(int i=0;i<out[index].length();i++) {
							for(int j=0;j<out[index].length();j++) {
								minNum[i][j] = Integer.MAX_VALUE;
							}
						}
						outputData[index] += getMinNumber(out[index], 1, out[index].length());
					}
					index++;
					
				}
				read.close();
			} else {
				System.out.println("can not find the file!");
			}
		} catch (Exception e) {
			System.out.println("unknow error when open file!");
			e.printStackTrace();
		}
	}
	
	
	private void echo2File(String f, int[] outputData) {
		FileWriter fw;
		try {
			fw = new FileWriter(f, false);
			for(int i=0;i<outputData.length;i++) {
				fw.write(outputData[i]+" \n");
			}
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int readFileLines(String f) {
		int lines = 0;
		try {
			File file = new File(f);
			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file));
				BufferedReader bufferedReader = new BufferedReader(read);
				while (bufferedReader.readLine() != null) {
					lines++;
				}
				read.close();
			} else {
				System.out.println("can not find the file!");
			}
		} catch (Exception e) {
			System.out.println("unknow error when open file!");
			e.printStackTrace();
		}
		return lines;
	}
	
	private String removeDuplicate(String input) {
		String out = "";
		if(input==null||input.length()==0)
			return out;
		char f = input.charAt(0);
		out = out+f;
		for(int i=1;i<input.length();i++) {
			char c = input.charAt(i);
			if(f==c) {
				continue;
			} else {
				out = out+c;
				f = c;
			}
		}
		return out;
	}
	
	private String removeSingle(int index, String input) {
		int[] word = {
				 0,0,0,0,0,0,0,0,0,0,0,0,0,
				 0,0,0,0,0,0,0,0,0,0,0,0,0};
		String out = "";
		if(input==null||input.length()==0)
			return out;
		for(int i=0;i<input.length();i++) {
			char c = input.charAt(i);
			word[c-'A']++;
		}
		for(int i=0;i<input.length();i++) {
			char c = input.charAt(i);
			if(word[c-'A'] == 1) {
				outputData[index]++;
			} else {
				out = out+c;
			}
		}
		return out;
	}
	
	private long begin = 0;
	public void doIt() {
		begin=System.currentTimeMillis();
		int lines = readFileLines(INPUT_FILE_NAME);
		inputData = new String[lines];
		outputData = new int[lines];
		calculateSweep(INPUT_FILE_NAME, inputData);
		echo2File(OUTPUT_FILE_NAME, outputData);
		
		System.out.println("Total process time is: " + (System.currentTimeMillis()-begin)/1000.0 + "s");
	}
	
	public static void main(String[] args) {
		PaintingProblem p = new PaintingProblem();
		p.doIt();
	}

}
