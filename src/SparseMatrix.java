import java.util.HashMap;
import java.util.Iterator;


public class SparseMatrix extends Matrix {

	private HashMap<Integer,HashMap<Integer,Double>> _hmRow2Col2Val;
	
	public SparseMatrix(int rows, int cols) throws MatrixException {
		super(rows, cols);
		//this._hmRow2Col2Val.clear();
	}

	public SparseMatrix(String filename) throws MatrixException {
		super(filename);
		//this._hmRow2Col2Val.clear();
	}
	
	@Override
	public void init(int rows, int cols) {
		_hmRow2Col2Val = new HashMap<Integer,HashMap<Integer,Double>>();
	}

	@Override
	public double get(int i, int j) throws MatrixException {
		
		double value = 0;
		
		// Checks bounds
		if (i < 0 || j < 0 || i >= getNumRows() || j >= getNumCols())
			throw new MatrixException("get(" + i + "," + j + ") not in matrix bounds " + 
					getNumRows() + " X " + getNumCols());
		
		// If bounds are legal, check if the value is assigned or of its zero (not assigned/stored)
			HashMap<Integer,Double> row_vector = _hmRow2Col2Val.get(i);
			
			if (this._hmRow2Col2Val.containsKey(i)){ //Checks if column is assigned to a value at that row
				if (row_vector.containsKey(j))
					return row_vector.get(j);
				else
					value = 0;
			}
			else{
				value = 0;	
			}
		
	return value;
	}

	@Override
	public void set(int i, int j, double val) throws MatrixException {		
		// Bounds check
				if (i < 0 || j < 0 || i >= getNumRows() || j >= getNumCols())
					throw new MatrixException("set(" + i + "," + j + ") not in matrix bounds " + 
							getNumRows() + " X " + getNumCols());
				
		//If bounds legal						
			if (this._hmRow2Col2Val.containsKey(i))	
				this._hmRow2Col2Val.get(i).put(j, val);
			
			else if (!this._hmRow2Col2Val.containsKey(i)){
				HashMap<Integer, Double> colVal = new HashMap<Integer,Double>();
				colVal.put(j, val);
				this._hmRow2Col2Val.put(i, colVal);
			}	
	}


	@Override
	public Matrix transpose() throws MatrixException {
		SparseMatrix t = new SparseMatrix(this.getNumCols(), this.getNumRows());
		
		// Perform the transposition
				for (int row: this._hmRow2Col2Val.keySet()) {
					for (int col : this._hmRow2Col2Val.get(row).keySet()) {
						t.set(col, row, get(row, col));
					}
				}
		return t;
	}

	@Override
	public Matrix multiply(Matrix b) throws MatrixException {

		// Check for proper operand dimensions
				if (this.getNumCols() != b.getNumRows())
					throw new MatrixException("Dimension mismatch for multiply: " + this.getNumCols() 
						+ " cols in op1 and " + b.getNumRows() + " rows in op2");
		
		Matrix product = new SparseMatrix(this.getNumRows(), b.getNumCols());
			
		SparseMatrix t = (SparseMatrix) b.transpose(); // transposed matrix of b

		// Compute the inner product for every (row,col) in the new prod matrix
				for (int row: this._hmRow2Col2Val.keySet())
					for (int col : t._hmRow2Col2Val.keySet()) {
						// prod[row,col] = dot_product(this[row,:], b[:,col])
						double sum = 0d;
						for (int index : this._hmRow2Col2Val.get(row).keySet())
							sum += this.get(row, index) * b.get(index, col);
						product.set(row, col, sum);
					}
		return product;
	}

	@Override
	public Matrix multiply01(Matrix b) throws MatrixException {
		
			// Check for proper operand dimensions
				if (this.getNumCols() != b.getNumRows())
					throw new MatrixException("Dimension mismatch for multiply: " + this.getNumCols() 
						+ " cols in op1 and " + b.getNumRows() + " rows in op2");
	
				
				// Return type will be same matrix type as "this"
				Matrix prod = new SparseMatrix(this.getNumRows(), b.getNumCols());
				
				SparseMatrix t = (SparseMatrix) b.transpose();
				
				// Compute the inner product for every (row,col) in the new prod matrix
				for (int row: this._hmRow2Col2Val.keySet())
					for (int col : t._hmRow2Col2Val.keySet()) {
						// Only difference between multiply and multiply01 is that multiply01
						// converts all numbers >= 1 to 1 and otherwise to 0.
						double sum = 0d;
						for (int index : this._hmRow2Col2Val.get(row).keySet())
							sum += this.get(row, index) * b.get(index, col);
						prod.set(row, col, sum >= 1d ? 1d : 0d);
					}
				
				return prod;
	}
	// NOTE: some simple tests... DenseMatrix should produce exactly the same results,
		//       only much faster and in much less space for large, sparse matrices.
		public static void main(String[] args) throws Exception {
			
			final boolean SHOW_RESULTS = true;
			
			SparseMatrix d = new SparseMatrix("smatrix1.txt");
			//DenseMatrix d = new DenseMatrix("dmatrix2.txt");
			//DenseMatrix d = new DenseMatrix("smatrix1.txt"); // NOTE: sparse encoding of dmatrix1.txt
			//DenseMatrix d = new DenseMatrix("smatrix2.txt"); // NOTE: too large to load as dense matrix
			if (SHOW_RESULTS) System.out.println(d);
			
			Matrix dt = d.transpose();
			if (SHOW_RESULTS) System.out.println("Transpose:\n" + dt);

			Matrix dmult = d.multiply(dt);
			if (SHOW_RESULTS) System.out.println("Self-multiply:\n" + dmult);

			Matrix dmult01 = d.multiply01(dt);
			if (SHOW_RESULTS) System.out.println("Self-01-multiply:\n" + dmult01);
			
			System.out.println("DMult[3,3] = " + dmult.get(3, 3));
		}
	
}
