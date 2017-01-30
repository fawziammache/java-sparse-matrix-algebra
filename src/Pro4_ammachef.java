import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeMap;

// This class implements a Mini-Matlab command line interface to the Matrix library
public class Pro4_ammachef {

	public static BufferedReader cin = new BufferedReader(new InputStreamReader(System.in));
		
	// Change the return in the following line to switch between Dense and Sparse matrices
	public static Matrix readFile(String filename) throws MatrixException {
		try {
			// TODO: Once you've implemented SparseMatrix, comment out the first line for
			//       DenseMatrix and uncomment the second line for SparseMatrix.
			//return new DenseMatrix(filename);
			return new SparseMatrix(filename);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static void displayCommands() throws IOException {
		System.out.println("MINI-MATLAB COMMAND LIST");
		System.out.println("Show this command list................ HELP");
		System.out.println("Exit the program...................... QUIT");
		System.out.println("Comment............................... % This is a comment");
		System.out.println("Show all variables in memory.......... SHOW_VARS");
		System.out.println("Print result of matrix operations..... SHOW_RESULTS {ON,OFF}");
		System.out.println("Read a matrix and assign to a var..... B = READ_FILE [FILENAME]");
		System.out.println("Matrix multiply....................... B = A * C");
		System.out.println("0-1 Matrix multiply................... C = A *01 C");
		System.out.println("Matrix transpose...................... E = A'");
		System.out.println("Display value (ignores SHOW_RESULTS).. A[3,4]");
	}

	// NOTE: You should not need to change the following code
	public static void main(String[] args) throws Exception {

		TreeMap<String,Matrix> variable_map = new TreeMap<String,Matrix>();
		boolean SHOW_RESULTS = true;

		while (true) {
			System.out.print("\n> ");
			String command = cin.readLine();
			String[] command_split = command.split("/"); // Workaround for chk_pro server anomaly
			command = command_split[command_split.length - 1];
			String[] split = command.split("\\s");
			if (split[0].equalsIgnoreCase("HELP")) {
				displayCommands();
			} else if (split[0].equalsIgnoreCase("QUIT")) {
				break;
			} else if (split[0].equalsIgnoreCase("%")) {
				// Ignore comments
			} else if (split[0].equalsIgnoreCase("SHOW_VARS")) {
				for (String var : variable_map.keySet()) {
					Matrix m = variable_map.get(var);
					System.out.format("%s = Matrix[%d X %d]\n", var, m.getNumRows(), m.getNumCols());
				}
			} else if (split[0].equalsIgnoreCase("SHOW_RESULTS")) {
				if (split.length != 2 || (!split[1].equalsIgnoreCase("ON") && !split[1].equalsIgnoreCase("OFF"))) 
					System.out.println("Expected 'SHOW_COMMAND on' or 'SHOW_COMMAND off' but got " + command);
				else {
					SHOW_RESULTS = split[1].equalsIgnoreCase("ON");
					System.out.print("SHOW_RESULTS turned " + (SHOW_RESULTS ? "on" : "off"));
				}
					
			} else { // Must be a more complex command
				
				// Check for a file read
				if (split.length == 4 && split[1].equals("=") && split[2].equalsIgnoreCase("READ_FILE")) {
					Matrix result = readFile(split[3]);
					if (result == null) {
						System.out.println("Could not read file '" + split[3] + "'");
						continue;
					}
					variable_map.put(split[0], result);
					if (SHOW_RESULTS) System.out.print(split[0] + " = \n" + result);
					
				// Check for a matrix multiply
				} else if (split.length == 5 && split[1].equals("=") && (split[3].equals("*") || split[3].equals("*01"))) {
					if (!variable_map.containsKey(split[2])) {
						System.out.println("Variable '" + split[2] + "' has not been defined");
						continue;
					}
					if (!variable_map.containsKey(split[4])) {
						System.out.println("Variable '" + split[4] + "' has not been defined");
						continue;
					}
					
					Matrix result = null;
					try {
						if (split[3].equals("*"))
							result = variable_map.get(split[2]).multiply(variable_map.get(split[4]));
						else
							result = variable_map.get(split[2]).multiply01(variable_map.get(split[4]));
					} catch (MatrixException e) {
						System.out.println("Error processing command '" + command + "':\n" + e.getMessage());
						continue;						
					}
					variable_map.put(split[0], result);
					if (SHOW_RESULTS) System.out.print(split[0] + " = \n" + result);

				// Check for a transpose
				} else if (split.length == 3 && split[1].equals("=") && split[2].endsWith("'")) {
					String var = split[2].substring(0, split[2].length()-1);
					if (!variable_map.containsKey(var)) {
						System.out.println("Variable '" + var + "' has not been defined");
						continue;
					}
					Matrix result = variable_map.get(var).transpose();
					variable_map.put(split[0], result);
					if (SHOW_RESULTS) System.out.print(split[0] + " = \n" + result);		
					
				// Check for a value display
				} else if (split.length == 1 && split[0].contains("[") && split[0].contains(",") && split[0].contains("]")) {
					String[] split2 = split[0].split("[\\[\\,\\]]");
					if (!variable_map.containsKey(split2[0])) {
						System.out.println("Variable '" + split2[0] + "' has not been defined");
						continue;
					}
					try {
						int row = Integer.parseInt(split2[1]);
						int col = Integer.parseInt(split2[2]);
						System.out.print(split2[0] + "[" + row + "," + col + "] = " + variable_map.get(split2[0]).get(row, col));
					} catch (Exception e) {
						System.out.println("Error processing command '" + command + "':\n" + e.getMessage());
						continue;
					}
					
				} else {
					System.out.println("Could not match command '" + command + "' to known template");
				}
				
			}
		}

	}

}
