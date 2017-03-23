import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

class Choco {

    private static Integer n, alphabet_cardinal, solutions;
    private static String alphabet;


    private static void set_cardinal() {
        if (alphabet.equals("latin"))
            alphabet_cardinal = 26;
        if (alphabet.equals("greek"))
            alphabet_cardinal = 24;
    }
    private static void read_arguments(String[] args) {
        if (args.length == 0) {
            n = 3;
            alphabet = "latin";
            solutions = 0;

            System.out.println("\n\tA B C\n\tB C A\n\tC A B\n");
        }
        else if (args.length == 2) {
            n = Integer.valueOf(args[0]);
            alphabet = args[1];
            solutions = 1;
        }
        else if (args.length == 3){
            n = Integer.valueOf(args[0]);
            alphabet = args[1];
            solutions = Integer.valueOf(args[2]);
        }
        else {
            n = -1;
            return;
        }

        set_cardinal();
    }

    private static void display(IntVar[][] table) {
        switch (alphabet_cardinal) {
            case 24:
                for (int i = 0; i < n; ++i) {
                    System.out.print("\t");
                    for (int j = 0; j < n; ++j)
                        System.out.print((char)(table[i][j].getValue() + '\u03B1' - 1) + " ");
                    System.out.println();
                }
                break;

            case 26:
                for (int i = 0; i < n; ++i) {
                    System.out.print("\t");
                    for (int j = 0; j < n; ++j)
                        System.out.print((char)(table[i][j].getValue() + 64) + " ");
                    System.out.println();
                }
                break;

            default:
                break;
        }
    }

    private static void latin_square() {

        Model model = new Model("Latin Square");

        IntVar[][] table = model.intVarMatrix(n, n, 1, n /*alphabet_cardinal*/);

        // FOR ALL MATRIX ELEMENTS
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {

                // CURRENT ELEMENT MUST BE...

                // ...DIFFERENT FROM ALL OTHER VALUES ON THE COLUMN
                for (int k = i + 1; k < n; ++k)
                    model.arithm(table[i][j], "!=", table[k][j]).post();

                // ...DIFFERENT FROM ALL OTHER VALUES ON THE ROW
                for (int k = j + 1; k < n; ++k)
                    model.arithm(table[i][j], "!=", table[i][k]).post();
            }
        }

        Solver sol = model.getSolver();

        //solutions = 0;
        while (sol.solve() && solutions > 0) {
            System.out.print("\n=====================\n\n" + (solutions--) + ".\n");
            display(table);
            /*solutions++;
            if (solutions % 1000000 == 0)
                System.out.println(solutions/1000000.0 + " million results found");*/
        }
    }


    public static void main(String[] args) {

        Long start = System.currentTimeMillis();

        read_arguments(args);

        if (n == -1) {
            System.out.println("INCORRECT ARGUMENTS");
            return;
        }

        latin_square();

        Long stop = System.currentTimeMillis();

        System.out.println("\nRUNNING TIME:\t" + ((stop - start)/1000.0) + " seconds");
    }
}
