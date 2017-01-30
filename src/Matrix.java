import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Implement equals, fixed point
public abstract class Matrix {
	
	private int _numRows = 0;
	private int _numCols = 0;
	
	// Populates an all-zeroes matrix of size rows and cols
	public Matrix(int rows, int cols) throws MatrixException {
		_numRows = rows;
		_numCols = cols;
		init(_numRows, _numCols); // This is abstract and implemented by subclasses
	}
	
	// Populates the matrix from a file
	public Matrix(String filename) throws MatrixException {
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			String[] split = line.split("\\s");
			
			// Parse first line with matrix type, #rows, #cols
			if (split.length != 3) {
				throw new MatrixException(String.format("First Line '%s'\nExpected header row of 3 values '{sparse,dense} #rows and #cols', but instead got %d entries",
						line, split.length));
			}
			String matrix_type = split[0];
			_numRows = Integer.parseInt(split[1]);
			_numCols = Integer.parseInt(split[2]);
			init(_numRows, _numCols); // This is abstract and implemented by subclasses
			
			// Now delegate to the right matrix format reader
			if (matrix_type.equalsIgnoreCase("dense")) {
				readDenseMatrixFormat(br);
				
			} else if (matrix_type.equalsIgnoreCase("sparse")) {
				readSparseMatrixFormat(br);
				
			} else
				throw new MatrixException("Unknown matrix type");
			
		} catch (MatrixException e) {
			throw e; // Briefly catch the Exception so we can close the BufferedReader below
	         // in the finally clause.  But we don't want to handle the Exception
	         // here so rethrow it.  Make sure you understand the rationale here.
		} catch (Exception e) { // Convert General Exceptions to a MatrixException
			throw new MatrixException("Matrix read Exception: " + e.getMessage());
		} finally {
			try {
				br.close(); // This is executed regardless of whether an Exception is thrown or not
			} catch (IOException e2) { // Closing could cause an IOException so need to catch
				throw new MatrixException("Matrix read Exception: " + e2.getMessage());
			}
		}
	}

	// Note how this method makes use of polymorphic calls to init(...) and set(...).  Note
	// that these two methods are abstract in Matrix so they cannot be implemented here.
	public void readDenseMatrixFormat(BufferedReader br) throws Exception {
		
		// As long as we can set entries, we can read the actual Matrix and set the
		// entries here in the superclass without knowing how the Matrix is implemented.
		int cur_row = 0;
		String line = null;
		
		// Read and parse each line of the matrix
		while ((line = br.readLine()) != null) {
			String[] split = line.split("\\s");
			if (split.length != _numCols)
				throw new MatrixException(String.format("Row[%d] = '%s'\nExpected %d columns, got %d columns",
						cur_row, line, _numCols, split.length));
			for (int cur_col = 0; cur_col < _numCols; cur_col++)
				set(cur_row, cur_col, Double.parseDouble(split[cur_col]));
			++cur_row;
		}
		if (cur_row != _numRows)
			throw new MatrixException(String.format("Expected %d rows, but read %d lines in file",
					_numRows, cur_row));
	}
	
	public void readSparseMatrixFormat(BufferedReader br) throws Exception {
		// TODO: You need to implement, see readDenseMatrix for an example.
		//       This should throw an MatrixException if the matrix indices are
		//       out of bounds.  See smatrix1.txt and smatrix2.txt for examples
		//       of the sparse matrix input format; note that (1) dmatrix1.txt and
		//       smatrix1.txt encode the same matrix, (2) it should be very
		//       difficult to read smatrix2.txt into a DenseMatrix, and (3) do
		//       not confuse the file storage type with the class name -- a
		//       DenseMatrix can read its data from a sparse matrix format and
		//       a SparseMatrix can read its data from a dense matrix format --
		//       this is why file reading is implemented in superclass Matrix.
		
		String line = null;
		
		
		while ((line = br.readLine()) != null) {
			
			String[] split = line.split("\\s");
			int row = Integer.parseInt(split[0]);
			int column = Integer.parseInt(split[1]);
			double value = Double.parseDouble(split[2]);
			
			if (row >= _numRows || row < 0)
				throw new MatrixException(String.format("Expected %d rows, but read %d lines in file", _numRows, row));
			
			if (column >= _numCols || row < 0)
				throw new MatrixException(String.format("Line '%s'\nExpected 3 entries, but got %d",line, split.length));
			
			set(row, column, value);		
		}
		
		
		//throw new MatrixException("readSparseMatrix not implemented yet");

		// Exception handling: do not catch Exceptions, let the caller handle them.
		//                     If the file is incorrectly formatted, throw the following Exception:
		// throw new MatrixException(String.format("Line '%s'\nExpected 3 entries, but got %d",
		//				line, number_of_distinct_strings_on_line_separated_by_whitespace));
	}
	
	// Get a String representation of the matrix in dense format
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getNumRows(); i++) {
			for (int j = 0; j < getNumCols(); j++)
				try {
					sb.append(String.format("%8.4f ", get(i,j)));
				} catch (MatrixException e) {
					sb.append("ERROR@get(" + i + "," + j + ") ");
				}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	// Initializes necessary space to store a matrix of rows X cols
	public abstract void init(int rows, int cols);
	
	// Get value at the matrix indices
	public abstract double get(int i, int j) throws MatrixException;

	// Set the value at the matrix indices
	public abstract void set(int i, int j, double val) throws MatrixException;

	// Number of rows in this matrix
	public int getNumRows() { return _numRows; }

	// Number of columns in this matrix
	public int getNumCols() { return _numCols; }

	// Return the transpose of this matrix
	public abstract Matrix transpose() throws MatrixException;
	
	// Return the matrix multiply of this*b
	public abstract Matrix multiply(Matrix b) throws MatrixException;
	
	// Return the 0-1 matrix multiply of this*b
	public abstract Matrix multiply01(Matrix b) throws MatrixException;
}
