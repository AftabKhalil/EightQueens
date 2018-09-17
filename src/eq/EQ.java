package eq;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.random;
import java.util.LinkedList;
import java.util.Random;

/*
 * @author aftab
 */
public class EQ {

    private static int n = 12;
    private static int Generations = 50;
    private static int noOfInd = 25;
    private static int noOfChld = 20;
    private static Random random = new Random(50);

    public static void main(String[] args) throws IOException {

        File file = new File("result.txt");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter myWriter = new BufferedWriter(fileWriter);

        for (n = 2; n <= 50; n += 2) {

            Generation generation = new Generation(noOfInd);
            for (int i = 0; i < noOfInd; i++) {
                generation.individual[i] = Individual.getRandomInd();
            }
            //generation.print();
            //System.out.println("Random Creted");
            //UNCOMMENT THE COMMITED LINES TO SHOW THE WHOLE OUTPUT ON CONSOLE
            int G = 0;
            myWriter.write("n, noOfGeneration,");
            myWriter.newLine();

            while (true) {

                Generation children = generation.createChildren(noOfChld);
                //System.out.println("Children Created");
                generation = Generation.chooseBest(generation, children);

                //System.out.println("----------------------------------------------------------------------");
                //System.out.println("Generation = " + G);

                //System.out.println(generation.individual[0].fitness);

                //System.out.println("---------------------------------------------------------------------");
                if (generation.individual[0].fitness == n) {
                    myWriter.write(n + ", " + G);
                    myWriter.newLine();
                    System.out.println("n = "+n+" G = "+G+" fittness = "+generation.individual[0].fitness);
                    generation.individual[0].print();
                    System.out.println("------------------------------------------------------------------");
                    break;
                }
                G++;
                if(G>3000){
                    n-=2;
                    break;
                }
            }
        }

        myWriter.close();
        fileWriter.close();

    }

    private static class Parent {

        public static Individual[] createTwoChilds(Individual parent1, Individual parent2) {
            //System.out.println("Creating Children");
            //System.out.println(Arrays.toString(parent1.pos));
            //System.out.println(Arrays.toString(parent2.pos));
            LinkedList geneOne = new LinkedList();
            LinkedList geneTwo = new LinkedList();

            for (int i = 0; i < n / 2; i++) {
                geneOne.add(parent1.pos[i]);
                geneTwo.add(parent2.pos[i]);
            }
            //System.out.println(geneOne);
            //System.out.println(geneTwo);

            for (int i = 0; i < n / 2; i++) {
                int j = i;

                while (true) {
                    int x = parent2.pos[(j + n / 2) % n];
                    //System.out.println(x);
                    if (geneOne.contains(x)) {
                        j++;
                    } else {
                        geneOne.add(x);
                        break;
                    }
                }
                j = i;
                //System.out.println("geneTwo");
                while (true) {
                    int x = parent1.pos[(j + n / 2) % n];
                    if (geneTwo.contains(x)) {
                        j++;
                    } else {
                        geneTwo.add(x);
                        break;
                    }
                }
            }
            //System.out.println("Pass");

            int geneOneArray[] = new int[n];
            int geneTwoArray[] = new int[n];

            for (int i = 0; i < n; i++) {
                geneOneArray[i] = (int) geneOne.get(i);
                geneTwoArray[i] = (int) geneTwo.get(i);
            }

            //System.out.println(Arrays.toString(geneOneArray));
            //System.out.println(Arrays.toString(geneTwoArray));
            Individual child1 = new Individual(geneOneArray).mutate();
            Individual child2 = new Individual(geneTwoArray).mutate();

            //System.out.println("(rerurn");
            return new Individual[]{child1, child2};
        }
    }

    private static class Generation {

        public Individual[] individual;

        public static Generation chooseBest(Generation parents, Generation children) {
            Generation all = new Generation(parents.individual.length + children.individual.length);
            int i;
            for (i = 0; i < parents.individual.length; i++) {
                all.individual[i] = parents.individual[i];
            }
            for (int j = 0; j < children.individual.length; j++, i++) {
                all.individual[i] = children.individual[j];
            }

            for (i = 0; i < all.individual.length; i++) {
                for (int j = i; j < all.individual.length; j++) {
                    if (all.individual[i].fitness < all.individual[j].fitness) {
                        Individual ind = all.individual[i];
                        all.individual[i] = all.individual[j];
                        all.individual[j] = ind;
                    }
                }
            }

            Generation best = new Generation(parents.individual.length);
            for (i = 0; i < best.individual.length; i++) {
                best.individual[i] = all.individual[i];
            }
            return best;
        }

        public Generation(int noOfInd) {
            individual = new Individual[noOfInd];
        }

        public Generation createChildren(int noOfChld) {
            Generation children = new Generation(noOfChld);

            for (int i = 0, j = 0; i < noOfChld / 2; i++) {

                int p1 = (int) (random() * 10);
                int p2 = (int) (random() * 10);

                while (p1 == p2) {
                    p1 = (int) (random() * 10);
                    p2 = (int) (random() * 10);
                }

                Individual[] newChilds = Parent.createTwoChilds(this.individual[p1], this.individual[p2]);
                children.individual[j] = newChilds[0];
                j++;
                children.individual[j] = newChilds[1];
                j++;
            }
            return children;
        }

    }

    private static class Individual {

        private static int getFitness(int pos[]) {
            //System.out.println(Arrays.toString(pos));

            //System.out.println("In fit");
            int fittness = 0;

            for (int i = 0; i < n; i++) {

                int row = i;
                int col = pos[i];

                //System.out.println("main row is "+row+" col is "+col);
                row++;
                col++;

                while (true) {
                    if (row >= n - 1 || col >= n - 1) {
                        row = i;
                        col = pos[i];
                        //System.out.println("back checking");
                        row--;
                        col--;

                        while (true) {

                            if (row <= 0 || col <= 0) {
                                //System.out.println("lower right checking");
                                row = i;
                                col = pos[i];
                                row++;
                                col--;

                                //System.out.println("upeerleft checking");
                                while (true) {
                                    //System.out.println("checking row = "+row+" col = "+col);

                                    if (row >= n - 1 || col <= 0) {
                                        row = i;
                                        col = pos[i];
                                        row--;
                                        col++;
                                        while (true) {

                                            //System.out.println("checking row = "+row+" col = "+col);
                                            if (row <= 0 || col >= n - 1) {
                                                fittness++;
                                                break;
                                            } else if (pos[row - 1] == col + 1) {
                                                //System.out.println("pos[row+1] = "+pos[row+1]+" col+1 = "+(col+1));
                                                break;
                                            } else {
                                                row--;
                                                col++;
                                                //System.out.println("checking row = "+row+" col = "+col);
                                            }
                                        }
                                        break;
                                    } else if (pos[row + 1] == col - 1) {
                                        //System.out.println("pos[row+1] = "+pos[row+1]+" col+1 = "+(col+1));
                                        break;
                                    } else {
                                        row++;
                                        col--;
                                        //System.out.println("checking row = "+row+" col = "+col);
                                    }
                                }

                                break;
                            } else if (pos[row - 1] == col - 1) {
                                //System.out.println("pos[row+1] = "+pos[row+1]+" col+1 = "+(col+1));
                                break;
                            } else {
                                row--;
                                col--;
                                //System.out.println("checking row = "+row+" col = "+col);
                            }
                        }
                        break;
                    } else if (pos[row] == col) {
                        //System.out.println("pos[row+1] = "+pos[row+1]+" col+1 = "+(col+1));
                        break;
                    } else {
                        row++;
                        col++;
                        //System.out.println("checking row = "+row+" col = "+col);
                    }

                }

            }
            //System.out.println("fittness is "+fittness);
            return fittness;
        }

        private static Individual getRandomInd() {
            //System.out.println("Here");
            int pos[] = new int[n];
            LinkedList posl = new LinkedList();

            for (int i = 0; i < n; i++) {
                int x = random.nextInt(n);
                while (posl.contains(x)) {
                    x = random.nextInt(n);
                }
                posl.add(x);
                pos[i] = x;
            }

            //System.out.println("Ind Created");
            return new Individual(pos);

        }

        public int pos[];
        public double fitness;

        public Individual(int pos[]) {
            this.pos = pos;
            //this.print();
            this.fitness = Individual.getFitness(pos);
        }

        private Individual mutate() {
            //System.out.println("MUTAYE");
            int pro = (int) (random() * 100);
            if (pro <= 75) {
                //System.out.println(n);
                int pos1 = random.nextInt(n);
                int pos2 = random.nextInt(n);
                int pos3 = random.nextInt(n);
                int pos4 = random.nextInt(n);
                //System.out.println("pos1 = "+pos1+" pos2 = "+pos2);
                // System.out.println("before");
                while (pos1 == pos2) {
                    pos2 = random.nextInt(n);
                }
                //System.out.println("after");
                //System.out.println("pos1 = "+pos1+" pos2 = "+pos2);
                int x = this.pos[pos2];
                this.pos[pos2] = this.pos[pos1];
                this.pos[pos1] = x;

                x = this.pos[pos3];
                this.pos[pos3] = this.pos[pos4];
                this.pos[pos4] = x;

                return this;
            } else {
                return this;
            }
        }

        private void print() {

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (this.pos[i] == j) {
                        System.out.print("Q ");
                    } else {
                        System.out.print("X ");
                    }
                }
                System.out.println("");
            }
            System.out.println("");
        }
    }
}
