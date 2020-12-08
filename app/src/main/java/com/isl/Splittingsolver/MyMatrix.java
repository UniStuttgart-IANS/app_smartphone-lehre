package com.uni_stuttgart.isl.Splittingsolver;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by nborg on 04.03.16.
 */
public class MyMatrix {
    private int dimension;
    private int entryNumbers;
    private int selectedBlock;
    private ArrayList<Entry> entryArrayList;
    private Entry[][] entrysOfBlock0;
    private Entry[][] entrysOfBlock1;
    private Entry[][] entrysOfBlock2;
    private Entry[][] entrysOfBlock3;
    private Entry[][] entrysOfBlock4;
    private Entry[][] entrysOfBlock5;
    private Entry[][] entrysOfBlock6;
    private Entry[][] entrysOfBlock7;
    private Entry[][] entrysOfBlock8;
    private boolean[][] blockSelecterMatrix = new boolean[10][9];
    private boolean[] wholeSelecterVector = new boolean[10];
    private boolean[] blockActive = new boolean[9];
    private double[][] A;
    private double[][] invA;
    private double[][] invM;
    private double[][] M;
    private double[][] N;
    private boolean Marker = true;
    private boolean[][] activityStructure = new boolean[dimension][dimension];
    private boolean zoom = false;

    public MyMatrix(int dimension) {
        this.dimension = dimension;
        this.entryNumbers = dimension * dimension;
        this.entryArrayList = new ArrayList<Entry>();
        this.entrysOfBlock0 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock1 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock2 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock3 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock4 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock5 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock6 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock7 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock8 = new Entry[dimension / 3][dimension / 3];

        this.A = new double[dimension][dimension];
        this.M = new double[dimension][dimension];
        this.invA = new double[dimension][dimension];
        this.invM = new double[dimension][dimension];
        this.N = new double[dimension][dimension];

        for (int i = 0; i < 9; i++) {
            blockActive[i] = false;
        }
    }

    public MyMatrix(int dimension, ArrayList<Entry> entryArrayList) {
        this.dimension = dimension;
        this.entryNumbers = dimension * dimension;
        this.entryArrayList = entryArrayList;
        this.entrysOfBlock0 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock1 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock2 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock3 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock4 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock5 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock6 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock7 = new Entry[dimension / 3][dimension / 3];
        this.entrysOfBlock8 = new Entry[dimension / 3][dimension / 3];

        this.A = new double[dimension][dimension];
        this.M = new double[dimension][dimension];
        this.invA = new double[dimension][dimension];
        this.invM = new double[dimension][dimension];
        this.N = new double[dimension][dimension];

        for (int i = 0; i < 9; i++) {
            blockActive[i] = false;
        }
    }

    public void addMatrix(double[][] matrix) {
        int[] currentposition = new int[2];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                currentposition[0] = i;
                currentposition[1] = j;
                this.entryArrayList.add(new Entry(matrix[i][j], false, currentposition));
                A[i][j] = matrix[i][j];
            }
        }
    }

    public Entry getEntry(int i, int j) {
        return this.entryArrayList.get(j + dimension * i);
    }

    public void relateBlock() {
        int dimensionBlock = dimension / 3;
        for (int l = 1; l <= 3; l++) {
            for (int i = 1; i <= 3; i++) {
                for (int j = 1; j <= dimensionBlock; j++) {
                    for (int k = 1; k <= dimensionBlock; k++) {
                        this.entryArrayList.get((j - 1) * dimension + k + (i - 1) * dimensionBlock + (l - 1) * dimensionBlock * dimension - 1).setBlockNumber(i + 3 * (l - 1));
                    }
                }
            }
        }
    }

    public void selectBlock(int blocknumber) {
        int dimensionBlock = dimension / 3;

        for (int k = 0; k < 9; k++) {
            if (blocknumber == k) {
                for (int i = 0; i < dimensionBlock; i++) {
                    for (int j = 0; j < dimensionBlock; j++) {
                        this.getBlock(k)[i][j].setSelected();
                        blockActive[blocknumber] = true;
                    }
                }
            }
        }
    }

    public void unselectBlock(int blocknumber) {
        int dimensionBlock = dimension / 3;

        for (int k = 0; k < 9; k++) {
            if (blocknumber == k) {
                for (int i = 0; i < dimensionBlock; i++) {
                    for (int j = 0; j < dimensionBlock; j++) {
                        this.getBlock(k)[i][j].setUnselected();
                        blockActive[blocknumber] = false;
                    }
                }
            }
        }
    }

    public Entry[][] getBlock(int n) {
        Entry[][] blockMatrix = new Entry[(int) (1 / 3 * dimension)][(int) (1 / 3 * dimension)];

        n = n + 1;

        if (n == 9) {
            blockMatrix = this.entrysOfBlock8.clone();
        } else if (n == 1) {
            blockMatrix = this.entrysOfBlock0.clone();
        } else if (n == 2) {
            blockMatrix = this.entrysOfBlock1.clone();
        } else if (n == 3) {
            blockMatrix = this.entrysOfBlock2.clone();
        } else if (n == 4) {
            blockMatrix = this.entrysOfBlock3.clone();
        } else if (n == 5) {
            blockMatrix = this.entrysOfBlock4.clone();
        } else if (n == 6) {
            blockMatrix = this.entrysOfBlock5.clone();
        } else if (n == 7) {
            blockMatrix = this.entrysOfBlock6.clone();
        } else if (n == 8) {
            blockMatrix = this.entrysOfBlock7.clone();
        }

        return blockMatrix;
    }


    public void sortEntrys() {
        int dimensionBlock = dimension / 3;
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (this.getEntry(i, j).getBlockNumber() == 1) {
                    entrysOfBlock0[i][j] = this.getEntry(i, j);
                } else if (this.getEntry(i, j).getBlockNumber() == 2) {
                    entrysOfBlock1[i][j - dimensionBlock] = this.getEntry(i, j);
                } else if (this.getEntry(i, j).getBlockNumber() == 3) {
                    entrysOfBlock2[i][j - 2 * dimensionBlock] = this.getEntry(i, j);
                } else if (this.getEntry(i, j).getBlockNumber() == 4) {
                    entrysOfBlock3[i - dimensionBlock][j] = this.getEntry(i, j);
                } else if (this.getEntry(i, j).getBlockNumber() == 5) {
                    entrysOfBlock4[i - dimensionBlock][j - dimensionBlock] = this.getEntry(i, j);
                } else if (this.getEntry(i, j).getBlockNumber() == 6) {
                    entrysOfBlock5[i - dimensionBlock][j - 2 * dimensionBlock] = this.getEntry(i, j);
                } else if (this.getEntry(i, j).getBlockNumber() == 7) {
                    entrysOfBlock6[i - 2 * dimensionBlock][j] = this.getEntry(i, j);
                } else if (this.getEntry(i, j).getBlockNumber() == 8) {
                    entrysOfBlock7[i - 2 * dimensionBlock][j - dimensionBlock] = this.getEntry(i, j);
                } else if (this.getEntry(i, j).getBlockNumber() == 9) {
                    entrysOfBlock8[i - 2 * dimensionBlock][j - 2 * dimensionBlock] = this.getEntry(i, j);
                } else {
                    Log.e("Test", "blah");
                }
            }
        }
    }

    public boolean[][] saveStructure() {
        boolean[][] activityMatrix = new boolean[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                activityMatrix[i][j] = this.getEntry(i, j).getStatus();
            }
        }
        return activityMatrix;
    }

    public void setActivityMatrix(boolean[][] activityMatrix) {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (activityMatrix[i][j] == true) {
                    this.getEntry(i, j).setSelected();
                } else if (activityMatrix[i][j] == false) {
                    this.getEntry(i, j).setUnselected();
                }
            }
        }
    }

    public double[][] getM() {

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (this.getEntry(i, j).getStatus() == true) {
                    this.M[i][j] = this.getEntry(i, j).getValue();
                } else {
                    this.M[i][j] = 0;
                }
            }
        }

        return this.M;
    }


    public void zoomBlock(int selectedBlock) {
        this.selectedBlock = selectedBlock;
    }

    public int getSelectedBlockNumber() {
        return selectedBlock;
    }

    public ArrayList<Entry> getEntryArrayList() {
        return entryArrayList;
    }

    public void setEntryArrayList(ArrayList<Entry> entryArrayList) {
        this.entryArrayList = entryArrayList;
    }

    public double[][] getA() {
        return A;
    }

    public void setA(double[][] a) {
        A = a;
    }

    public double[][] getN() {
        return N;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getEntryNumbers() {
        return entryNumbers;
    }

    public void setEntryNumbers(int entryNumbers) {
        this.entryNumbers = entryNumbers;
    }

    public boolean[] getBlockActive() {
        return blockActive;
    }

    public boolean getMarker() {
        return Marker;
    }

    public void setMarker(boolean marker) {
        Marker = marker;
    }

    public boolean[][] getActivityStructure() {
        return activityStructure;
    }

    public void setActivityStructure(boolean[][] activityStructure) {
        this.activityStructure = activityStructure;
    }

    public void selectDiag(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension; i++) {
                this.getEntry(i, i).setSelected();
            }
            wholeSelecterVector[0] = true;
            blockSelecterMatrix[0][0] = true;
            blockSelecterMatrix[0][4] = true;
            blockSelecterMatrix[0][8] = true;
        } else {
            blockSelecterMatrix[0][n] = true;
            for (int i = 0; i < dimension / 3; i++) {
                this.getBlock(n)[i][i].setSelected();
            }
        }
    }

    public void unselectDiag(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension; i++) {
                this.getEntry(i, i).setUnselected();
            }
            wholeSelecterVector[0] = false;
            blockSelecterMatrix[0][0] = false;
            blockSelecterMatrix[0][4] = false;
            blockSelecterMatrix[0][8] = false;
        } else {
            blockSelecterMatrix[0][n] = false;
            for (int i = 0; i < dimension / 3; i++) {
                this.getBlock(n)[i][i].setUnselected();
            }
        }
    }

    public void selectAntiDiag(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension; i++) {
                this.getEntry(i, dimension - i - 1).setSelected();
            }
            wholeSelecterVector[1] = true;
            blockSelecterMatrix[1][2] = true;
            blockSelecterMatrix[1][4] = true;
            blockSelecterMatrix[1][6] = true;
        } else {
            blockSelecterMatrix[1][n] = true;
            for (int i = 0; i < dimension / 3; i++) {
                this.getBlock(n)[i][dimension / 3 - i - 1].setSelected();
            }
        }
    }

    public void unselectAntiDiag(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension; i++) {
                this.getEntry(i, dimension - i - 1).setUnselected();
            }
            wholeSelecterVector[1] = false;
            blockSelecterMatrix[1][2] = false;
            blockSelecterMatrix[1][4] = false;
            blockSelecterMatrix[1][6] = false;
        } else {
            blockSelecterMatrix[1][n] = false;
            for (int i = 0; i < dimension / 3; i++) {
                this.getBlock(n)[i][dimension / 3 - i - 1].setUnselected();
            }
        }
    }

    public void selectLowerDiag(int n) {
        if (zoom == false) {
            for (int i = 1; i < dimension; i++) {
                this.getEntry(i, i - 1).setSelected();
            }
            wholeSelecterVector[2] = true;
            blockSelecterMatrix[2][0] = true;
            blockSelecterMatrix[2][4] = true;
            blockSelecterMatrix[2][8] = true;
        } else {
            blockSelecterMatrix[2][n] = true;
            for (int i = 1; i < dimension / 3; i++) {
                this.getBlock(n)[i][i - 1].setSelected();
            }
        }
    }

    public void unselectLowerDiag(int n) {
        if (zoom == false) {
            for (int i = 1; i < dimension; i++) {
                this.getEntry(i, i - 1).setUnselected();
            }
            wholeSelecterVector[2] = false;
            blockSelecterMatrix[2][0] = false;
            blockSelecterMatrix[2][4] = false;
            blockSelecterMatrix[2][8] = false;
        } else {
            blockSelecterMatrix[2][n] = false;
            for (int i = 1; i < dimension / 3; i++) {
                this.getBlock(n)[i][i - 1].setUnselected();
            }
        }
    }

    public void selectUpperDiag(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension - 1; i++) {
                this.getEntry(i, i + 1).setSelected();
            }
            wholeSelecterVector[3] = true;
            blockSelecterMatrix[3][0] = true;
            blockSelecterMatrix[3][4] = true;
            blockSelecterMatrix[3][8] = true;
        } else {
            blockSelecterMatrix[3][n] = true;
            for (int i = 0; i < dimension / 3 - 1; i++) {
                this.getBlock(n)[i][i + 1].setSelected();
            }
        }
    }

    public void unselectUpperDiag(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension - 1; i++) {
                this.getEntry(i, i + 1).setUnselected();
            }
            wholeSelecterVector[3] = false;
            blockSelecterMatrix[3][0] = false;
            blockSelecterMatrix[3][4] = false;
            blockSelecterMatrix[3][8] = false;
        } else {
            blockSelecterMatrix[3][n] = false;
            for (int i = 0; i < dimension / 3 - 1; i++) {
                this.getBlock(n)[i][i + 1].setUnselected();
            }
        }
    }

    public void selectLowerAntiDiag(int n) {
        if (zoom == false) {
            for (int i = 1; i < dimension; i++) {
                this.getEntry(i, dimension - i).setSelected();
            }
            wholeSelecterVector[4] = true;
            blockSelecterMatrix[4][2] = true;
            blockSelecterMatrix[4][4] = true;
            blockSelecterMatrix[4][6] = true;
        } else {
            blockSelecterMatrix[4][n] = true;
            for (int i = 1; i < dimension / 3; i++) {
                this.getBlock(n)[i][dimension / 3 - i].setSelected();
            }
        }
    }

    public void unselectLowerAntiDiag(int n) {
        if (zoom == false) {
            for (int i = 1; i < dimension; i++) {
                this.getEntry(i, dimension - i).setUnselected();
            }
            wholeSelecterVector[4] = false;
            blockSelecterMatrix[4][2] = false;
            blockSelecterMatrix[4][4] = false;
            blockSelecterMatrix[4][6] = false;
        } else {
            blockSelecterMatrix[4][n] = false;
            for (int i = 1; i < dimension / 3; i++) {
                this.getBlock(n)[i][dimension / 3 - i].setUnselected();
            }
        }
    }

    public void selectUpperAntiDiag(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension - 1; i++) {
                this.getEntry(i, dimension - 2 - i).setSelected();
            }
            wholeSelecterVector[5] = true;
            blockSelecterMatrix[5][2] = true;
            blockSelecterMatrix[5][4] = true;
            blockSelecterMatrix[5][6] = true;
        } else {
            blockSelecterMatrix[5][n] = true;
            for (int i = 0; i < dimension / 3 - 1; i++) {
                this.getBlock(n)[i][dimension / 3 - 2 - i].setSelected();
            }
        }
    }

    public void unselectUpperAntiDiag(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension - 1; i++) {
                this.getEntry(i, dimension - 2 - i).setUnselected();
            }
            wholeSelecterVector[5] = false;
            blockSelecterMatrix[5][2] = false;
            blockSelecterMatrix[5][4] = false;
            blockSelecterMatrix[5][6] = false;
        } else {
            blockSelecterMatrix[5][n] = false;
            for (int i = 0; i < dimension / 3 - 1; i++) {
                this.getBlock(n)[i][dimension / 3 - 2 - i].setUnselected();
            }
        }
    }

    public void selectLowerTriangle(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    if (i > j) {
                        this.getEntry(i, j).setSelected();
                    }
                }
            }
            wholeSelecterVector[2] = true;
            wholeSelecterVector[6] = true;
            //Lower Diagonale
            blockSelecterMatrix[2][0] = true;
            blockSelecterMatrix[2][4] = true;
            blockSelecterMatrix[2][8] = true;
            // Lower Triangle
            blockSelecterMatrix[6][0] = true;
            blockSelecterMatrix[6][4] = true;
            blockSelecterMatrix[6][8] = true;
        } else {
            blockSelecterMatrix[2][n] = true;
            blockSelecterMatrix[6][n] = true;
            for (int i = 0; i < dimension / 3; i++) {
                for (int j = 0; j < dimension / 3; j++) {
                    if (i > j) {
                        this.getBlock(n)[i][j].setSelected();
                    }
                }
            }
        }
    }

    public void unselectLowerTriangle(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    if (i > j) {
                        this.getEntry(i, j).setUnselected();
                    }
                }
            }
            wholeSelecterVector[2] = false;
            wholeSelecterVector[6] = false;
            //Lower Diagonale
            blockSelecterMatrix[2][0] = false;
            blockSelecterMatrix[2][4] = false;
            blockSelecterMatrix[2][8] = false;
            // Lower Triangle
            blockSelecterMatrix[6][0] = false;
            blockSelecterMatrix[6][4] = false;
            blockSelecterMatrix[6][8] = false;
        } else {
            blockSelecterMatrix[2][n] = false;
            blockSelecterMatrix[6][n] = false;
            for (int i = 0; i < dimension / 3; i++) {
                for (int j = 0; j < dimension / 3; j++) {
                    if (i > j) {
                        this.getBlock(n)[i][j].setUnselected();
                    }
                }
            }
        }
    }

    public void selectUpperTriangle(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    if (j > i) {
                        this.getEntry(i, j).setSelected();
                    }
                }
            }
            wholeSelecterVector[3] = true;
            wholeSelecterVector[7] = true;
            //Lower Diagonale
            blockSelecterMatrix[3][0] = true;
            blockSelecterMatrix[3][4] = true;
            blockSelecterMatrix[3][8] = true;
            // Lower Triangle
            blockSelecterMatrix[7][0] = true;
            blockSelecterMatrix[7][4] = true;
            blockSelecterMatrix[7][8] = true;
        } else {
            blockSelecterMatrix[3][n] = true;
            blockSelecterMatrix[7][n] = true;
            for (int i = 0; i < dimension / 3; i++) {
                for (int j = 0; j < dimension / 3; j++) {
                    if (j > i) {
                        this.getBlock(n)[i][j].setSelected();
                    }
                }
            }
        }
    }

    public void unselectUpperTriangle(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    if (j > i) {
                        this.getEntry(i, j).setUnselected();
                    }
                }
            }
            wholeSelecterVector[3] = false;
            wholeSelecterVector[7] = false;
            //Lower Diagonale
            blockSelecterMatrix[3][0] = false;
            blockSelecterMatrix[3][4] = false;
            blockSelecterMatrix[3][8] = false;
            // Lower Triangle
            blockSelecterMatrix[7][0] = false;
            blockSelecterMatrix[7][4] = false;
            blockSelecterMatrix[7][8] = false;
        } else {
            blockSelecterMatrix[3][n] = false;
            blockSelecterMatrix[7][n] = false;
            for (int i = 0; i < dimension / 3; i++) {
                for (int j = 0; j < dimension / 3; j++) {
                    if (j > i) {
                        this.getBlock(n)[i][j].setUnselected();
                    }
                }
            }
        }
    }

    public void selectLowerAntiTriangle(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    if (j >= dimension - i) {
                        this.getEntry(i, j).setSelected();
                    }
                }
            }
            wholeSelecterVector[4] = true;
            wholeSelecterVector[8] = true;
            //Lower Diagonale
            blockSelecterMatrix[4][2] = true;
            blockSelecterMatrix[4][4] = true;
            blockSelecterMatrix[4][6] = true;
            // Lower Triangle
            blockSelecterMatrix[8][2] = true;
            blockSelecterMatrix[8][4] = true;
            blockSelecterMatrix[8][6] = true;
        } else {
            blockSelecterMatrix[4][n] = true;
            blockSelecterMatrix[8][n] = true;
            for (int i = 0; i < dimension / 3; i++) {
                for (int j = 0; j < dimension / 3; j++) {
                    if (j >= dimension / 3 - i) {
                        this.getBlock(n)[i][j].setSelected();
                    }
                }
            }
        }
    }

    public void unselectLowerAntiTriangle(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    if (j >= dimension - i) {
                        this.getEntry(i, j).setUnselected();
                    }
                }
            }
            wholeSelecterVector[4] = false;
            wholeSelecterVector[8] = false;
            //Lower Diagonale
            blockSelecterMatrix[4][2] = false;
            blockSelecterMatrix[4][4] = false;
            blockSelecterMatrix[4][6] = false;
            // Lower Triangle
            blockSelecterMatrix[8][2] = false;
            blockSelecterMatrix[8][4] = false;
            blockSelecterMatrix[8][6] = false;
        } else {
            blockSelecterMatrix[4][n] = false;
            blockSelecterMatrix[8][n] = false;
            for (int i = 0; i < dimension / 3; i++) {
                for (int j = 0; j < dimension / 3; j++) {
                    if (j >= dimension / 3 - i) {
                        this.getBlock(n)[i][j].setUnselected();
                    }
                }
            }
        }
    }

    public void selectUpperAntiTriangle(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    if (j <= dimension - i - 2) {
                        this.getEntry(i, j).setSelected();
                    }
                }
            }
            wholeSelecterVector[5] = true;
            wholeSelecterVector[9] = true;
            //Upper Diagonale
            blockSelecterMatrix[5][2] = true;
            blockSelecterMatrix[5][4] = true;
            blockSelecterMatrix[5][6] = true;
            // Lower Triangle
            blockSelecterMatrix[9][2] = true;
            blockSelecterMatrix[9][4] = true;
            blockSelecterMatrix[9][6] = true;
        } else {
            blockSelecterMatrix[5][n] = true;
            blockSelecterMatrix[9][n] = true;
            for (int i = 0; i < dimension / 3; i++) {
                for (int j = 0; j < dimension / 3; j++) {
                    if (j <= dimension / 3 - i - 2) {
                        this.getBlock(n)[i][j].setSelected();
                    }
                }
            }
        }
    }

    public void unselectUpperAntiTriangle(int n) {
        if (zoom == false) {
            for (int i = 0; i < dimension; i++) {
                for (int j = 0; j < dimension; j++) {
                    if (j <= dimension - i - 2) {
                        this.getEntry(i, j).setUnselected();
                    }
                }
            }
            wholeSelecterVector[5] = false;
            wholeSelecterVector[9] = false;
            //Upper Diagonale
            blockSelecterMatrix[5][2] = false;
            blockSelecterMatrix[5][4] = false;
            blockSelecterMatrix[5][6] = false;
            // Lower Triangle
            blockSelecterMatrix[9][2] = false;
            blockSelecterMatrix[9][4] = false;
            blockSelecterMatrix[9][6] = false;
        } else {
            blockSelecterMatrix[5][n] = false;
            blockSelecterMatrix[9][n] = false;
            for (int i = 0; i < dimension / 3; i++) {
                for (int j = 0; j < dimension / 3; j++) {
                    if (j <= dimension / 3 - i - 2) {
                        this.getBlock(n)[i][j].setUnselected();
                    }
                }
            }
        }
    }

    public double[][] getInvA() {
        return invA;
    }

    public void setInvA(double[][] invA) {
        this.invA = invA;
    }

    public double[][] getInvM() {
        return invM;
    }

    public void setInvM(double[][] invM) {
        this.invM = invM;
    }

    public boolean[][] getBlockSelecterMatrix() {
        return blockSelecterMatrix;
    }

    public void setBlockSelecterMatrix(boolean[][] blockSelecterMatrix) {
        this.blockSelecterMatrix = blockSelecterMatrix;
    }

    public boolean getZoom() {
        return zoom;
    }

    public void setZoom(boolean zoom) {
        this.zoom = zoom;
    }

    public boolean[] getWholeSelecterVector() {
        return wholeSelecterVector;
    }

    public void setBlockSelecterMatrixFromStringVec(String[] s) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 9; j++) {
                this.blockSelecterMatrix[i][j] = getBooleanfromInt(Integer.parseInt(s[i * 9 + j]));
            }
        }
    }

    public void setWholeSelecterVectorFromStringVec(String[] sVec) {
        for (int i = 0; i < 10; i++) {
            this.wholeSelecterVector[i] = getBooleanfromInt(Integer.parseInt(sVec[i]));
        }
    }

    private boolean getBooleanfromInt(int i) {
        boolean b;

        if (i == 1) {
            b = true;
        } else {
            b = false;
        }

        return b;
    }

    public String StringVecToString(String[] logStringVec) {
        String logString = "";

        for (int i = 0; i < logStringVec.length; i++) {
            logString = logString + logStringVec[i] + " ";
        }

        return logString;
    }
}
