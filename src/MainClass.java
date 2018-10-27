import java.io.*;
import java.util.*;

public class MainClass {
static long startTime = System.nanoTime();

static String tempFileName = "C:\\Users\\stefa\\Downloads\\caverns files\\input4-100.cav"; 

	public static void main(String[] args) {
		// If a file is specified, update the name
 		if(args.length == 1) {
			tempFileName = args[0];
		}
		// Search for a path
		aStar(readFile(tempFileName));
		// Print how long it took
		long runTime = (System.nanoTime() - startTime);
		System.out.printf("Run time: %s nanoseconds, %s milliseconds, %s seconds.\n", Float.toString(runTime), Float.toString(runTime/1000000), Float.toString(runTime/1000000000));
	} // End of main
	
	// Read from the file passed in and return ArrayList of Integers (instructions)
	private static ArrayList<Integer> readFile(String fileName) {
		// Set of instructions to return
		ArrayList<Integer> instructions = new ArrayList<Integer>();
		
		File fileToRead = new File(fileName);
		
		try {
			// Buffer a chunk of data
			BufferedReader getChunk = new BufferedReader(new FileReader(fileToRead));
			// Read line by line
			String info = getChunk.readLine();
			while(info != null) {
				
				// Split the line into string integer
				String[] singleLine = info.split(",");
				
				// Parse each into an integer
				for(int i = 0; i < singleLine.length; i++) {
					instructions.add(Integer.parseInt(singleLine[i]));
				}
	
				// read next line
				info = getChunk.readLine();
			}
			
			// Close the buffer chunk
			getChunk.close();
			
		} //End of try 
		catch (FileNotFoundException e) {
			System.out.println("Could not find the file. Please check the path.");
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println("IO Exception...");
			e.printStackTrace();
		}
		
		return instructions;
	}

	// Write the answer to a file
	private static void writeFile(String answer) {
		//TODO: actually write it to a file!
		System.out.println(answer);
	}

	// Search with A*
	private static void aStar(ArrayList<Integer> instructions) {
		String answer = "0";
		ArrayList<Integer> finalPath = new ArrayList<Integer>();
		// Set myMap as reference to the map
		Cave[] arrayOfCaves = loadData(instructions);
		// Evaluated caves - empty at start
		ArrayList<Cave> closedSet = new ArrayList<Cave>();
		// Discovered caves that are not yet evaluated
		PriorityQueue<Cave> openSet = new PriorityQueue<Cave>();
		// Add the start cave to the openSet
		openSet.add(arrayOfCaves[0]);
		// The Cave under evaluation
		Cave currentCave;
		
		// While we have possible ways to go
		while(!openSet.isEmpty()) {
			// Pick the best one
			currentCave = openSet.poll();
			
			// If we are at the destination
			if (currentCave.heuristicCost == 0) {
				while(currentCave.cameFrom != null) {
					finalPath.add(currentCave.id);
					currentCave = currentCave.cameFrom;
				}
				// Add the first one
				finalPath.add(1);
				Collections.reverse(finalPath);
				answer = "";
				for (Integer i : finalPath) {
					answer = answer + i.toString() + " ";
				}
				writeFile(answer);
				return;
			}
			
			closedSet.add(currentCave);
			
			// Check each canGoTo for this current one
			for (Cave caveToEvaluate : currentCave.canGoTo) {
				
				float suggestedNewPath = currentCave.pathCost + 
						(float)Math.hypot(currentCave.xpos - caveToEvaluate.xpos,
								currentCave.ypos - caveToEvaluate.ypos);
				
				// Have we evaluated this cave before?
				// If we have that means we reached it with a better path
				if (closedSet.contains(caveToEvaluate)) {
					// Have we found this cave before?
					continue;
				}
				
				if (!openSet.contains(caveToEvaluate)) {
					// We got best path, save it and update it.
					openSet.add(caveToEvaluate);
					// Is the path same or worse?
				} else if (suggestedNewPath >= caveToEvaluate.pathCost) {
					continue;
				}
				// We got best path, save it and update it.
				caveToEvaluate.cameFrom = currentCave;
				caveToEvaluate.pathCost = suggestedNewPath;
				caveToEvaluate.updateValue();
				
	        }
		}
		
		// If we come out of the loop without return we didn't find a solution
		writeFile(answer);
	}
	
	// Method for loading the data into memory and returning a map object with the variables
	private static Cave[] loadData(ArrayList<Integer> instructions) {
		// Variables to return
		int numberOfCaves = instructions.get(0);
		Cave[] arrayOfCaves = new Cave[numberOfCaves];
		
		// Create the caves
		for (int i = 0; i < numberOfCaves; i++){
				Cave cave = new Cave(i+1, instructions.get(i*2 + 1), instructions.get(i*2 + 2));
				arrayOfCaves[i] = cave;
		}
		
		// By default the path cost is really high, update the first to be 0
		arrayOfCaves[0].pathCost = 0;
		
		// Update the heuristics, value and canGoTo
		for (int i = 0; i < numberOfCaves; i++) {
			arrayOfCaves[i].updateHeuristics(arrayOfCaves[numberOfCaves-1].xpos, 
					arrayOfCaves[numberOfCaves-1].ypos);
			arrayOfCaves[i].updateValue();
			for (int j = 0; j < numberOfCaves; j++) {
				if (instructions.get(numberOfCaves*(2+j)+(1+i)) == 1) {	
					arrayOfCaves[i].canGoTo.add(arrayOfCaves[j]);
				}
			}
		}
		
		return arrayOfCaves;
	}

	// Class for caves
	private static class Cave implements Comparable<Cave>{
		int id;
		int xpos;
		int ypos;
		float heuristicCost;
		float pathCost = Float.MAX_VALUE;
		float value;
		Cave cameFrom;
		ArrayList<Cave> canGoTo = new ArrayList<Cave>();

		// Constructor to be used by default
		Cave(int id, int xpos, int ypos){
			this.id = id;
			this.xpos = xpos;
			this.ypos = ypos;
		}

		@Override
		public int compareTo(Cave other) {
			if(this.equals(other)) {
				return 0;
			} else if (this.value > other.value) {
				return 1;
			} else {
				return -1;
			}
		}
		
		private void updateValue() {
			this.value = this.pathCost + this.heuristicCost;
		}
		
		private void updateHeuristics(int goalX, int goalY) {
			this.heuristicCost = (float)Math.hypot(this.xpos - goalX, this.ypos - goalY);
		}
	}
	
} // End of MainClass




