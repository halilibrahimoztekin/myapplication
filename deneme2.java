package com.example.myapplication;

import java.util.Random;
import java.util.Scanner;

/*
 * genetic algorithm that uses neural network to learn to play tictactoe
 */
class TicTacToe {

    static int[] oyuntahtasi = new int[9];
    static int[] inputBoard = new int[27];
    static int n_noronlar = 30;  //gizli katmandaki nöron sayısı
    static int n_uyeler = 1000;//popülasyondaki üye sayısı
    static int n_denemeler = 20;	//popülasyonu test etmek için simüle edilen oyun sayısı
    static int n_egitim = 50;	//popülasyonun her bir üyesi üzerinde simüle edilen oyunların sayısı
    static double p_hayattakalma = .3; //üyenin bir sonraki nesle geçme olasılığı
    //neural network implementation
    static double[][][] w1 = new double[n_uyeler][27][n_noronlar];
    static double[] hL = new double[n_noronlar];
    static double[][][] w2 = new double[n_uyeler][n_noronlar][9];
    static double[] ciktilar = new double[9];


    //skor = kazanılan oyun sayısı, antiskor = kaybedilen oyun sayısı (tutarsızlık = beraberlik)
    static double[] skor = new double[n_uyeler];
    static double[] antiskor = new double[n_uyeler];
    static double initW = 1;

    static double var = .4; //her ağdaki ağırlıkların varyansı
    static Random r = new Random();
    static int simdikiTest = 0;
    static int[][] testers = new int[n_denemeler][9];

    public static void main(String[] args) {
        for(int i = 0 ; i < w1.length; i++) {
            for(int j = 0; j < w1[0].length; j++) {
                for(int k = 0; k < w1[0][0].length; k++) {
                    w1[i][j][k] = initW - (Math.random() * initW / 2);
                }
            }
        }
        for(int i = 0 ; i < w2.length; i++) {
            for(int j = 0; j < w2[0].length; j++) {
                for(int k = 0; k < w2[0][0].length; k++) {
                    w2[i][j][k] = initW - (Math.random() * initW / 2);;
                }
            }
        }
        calistir();

    }
    public static void newGame(int i) {
        oyuntahtasi = new int[9];
        inputBoard = new int[27];
        for(int j = 0; j < 27; j += 3) {
            inputBoard[j] = 1;
        }
        game(i);
    }
    public static void game(int idx) {

        hL = new double[n_noronlar];
        for(int i = 0; i < w1[idx].length; i++) {
            for(int j = 0; j < w1[idx][i].length; j++) {
                hL[j] += inputBoard[i] * w1[idx][i][j];
            }
        }
        ciktilar = new double[9];
        for(int i = 0; i < w2[idx].length; i++) {
            for(int j = 0; j < w2[idx][i].length; j++) {
                ciktilar[j] += hL[i] * w2[idx][i][j];
            }
        }
        double currmin = -100000; int curridx = -1;
        for(int i = 0; i < 9; i++) {
            if(ciktilar[i] > currmin && oyuntahtasi[i] == 0) {
                currmin = ciktilar[i];
                curridx = i;
            }
        }
        oyuntahtasi[curridx] = 1;
        inputBoard[curridx * 3 + 1] = 1; inputBoard[curridx * 3] = 0;
        boolean empty = false;
        for(int i = 0; i < oyuntahtasi.length; i++) {
            if(oyuntahtasi[i] == 0)
            {
                empty = true;
            break;
            }
        }

        int o_idx = -1;
        if((oyuntahtasi[0] == 1 && oyuntahtasi[3] == 1 && oyuntahtasi[6] == 1) ||
                (oyuntahtasi[1] == 1 && oyuntahtasi[4] == 1 && oyuntahtasi[7] == 1) ||
                (oyuntahtasi[2] == 1 && oyuntahtasi[5] == 1 && oyuntahtasi[8] == 1) ||
                (oyuntahtasi[0] == 1 && oyuntahtasi[1] == 1 && oyuntahtasi[2] == 1) ||
                (oyuntahtasi[3] == 1 && oyuntahtasi[4] == 1 && oyuntahtasi[5] == 1) ||
                (oyuntahtasi[6] == 1 && oyuntahtasi[7] == 1 && oyuntahtasi[8] == 1) ||
                (oyuntahtasi[0] == 1 && oyuntahtasi[4] == 1 && oyuntahtasi[8] == 1) ||
                (oyuntahtasi[2] == 1 && oyuntahtasi[4] == 1 && oyuntahtasi[6] == 1) ) {
            skor[idx] += 1;
            return;
        }
        if(!empty) {
            skor[idx] -= .5;
            return;

        }
        for(int i = 0; i < 9; i++) {
            if(oyuntahtasi[testers[simdikiTest][i]] == 0){
                o_idx = testers[simdikiTest][i];
                break;
            }
        }
        if(oyuntahtasi[0] == 2 && oyuntahtasi[3] == 2 && oyuntahtasi[6] == 0) {o_idx = 6;}
        if(oyuntahtasi[0] == 2 && oyuntahtasi[3] == 0 && oyuntahtasi[6] == 2) {o_idx = 3;}
        if(oyuntahtasi[0] == 0 && oyuntahtasi[3] == 2 && oyuntahtasi[6] == 2) {o_idx = 0;}

        if(oyuntahtasi[1] == 2 && oyuntahtasi[4] == 2 && oyuntahtasi[7] == 0) {o_idx = 7;}
        if(oyuntahtasi[1] == 2 && oyuntahtasi[4] == 0 && oyuntahtasi[7] == 2) {o_idx = 4;}
        if(oyuntahtasi[1] == 0 && oyuntahtasi[4] == 2 && oyuntahtasi[7] == 2) {o_idx = 1;}

        if(oyuntahtasi[2] == 2 && oyuntahtasi[5] == 2 && oyuntahtasi[8] == 0) {o_idx = 8;}
        if(oyuntahtasi[2] == 2 && oyuntahtasi[5] == 0 && oyuntahtasi[8] == 2) {o_idx = 5;}
        if(oyuntahtasi[2] == 0 && oyuntahtasi[5] == 2 && oyuntahtasi[8] == 2) {o_idx = 2;}

        if(oyuntahtasi[0] == 2 && oyuntahtasi[1] == 2 && oyuntahtasi[2] == 0) {o_idx = 2;}
        if(oyuntahtasi[0] == 2 && oyuntahtasi[1] == 0 && oyuntahtasi[2] == 2) {o_idx = 1;}
        if(oyuntahtasi[0] == 0 && oyuntahtasi[1] == 2 && oyuntahtasi[2] == 2) {o_idx = 0;}

        if(oyuntahtasi[3] == 2 && oyuntahtasi[4] == 2 && oyuntahtasi[5] == 0) {o_idx = 5;}
        if(oyuntahtasi[3] == 2 && oyuntahtasi[4] == 0 && oyuntahtasi[5] == 2) {o_idx = 4;}
        if(oyuntahtasi[3] == 0 && oyuntahtasi[4] == 2 && oyuntahtasi[5] == 2) {o_idx = 3;}

        if(oyuntahtasi[6] == 2 && oyuntahtasi[7] == 2 && oyuntahtasi[8] == 0) {o_idx = 8;}
        if(oyuntahtasi[6] == 2 && oyuntahtasi[7] == 0 && oyuntahtasi[8] == 2) {o_idx = 7;}
        if(oyuntahtasi[6] == 0 && oyuntahtasi[7] == 2 && oyuntahtasi[8] == 2) {o_idx = 6;}

        if(oyuntahtasi[0] == 2 && oyuntahtasi[4] == 2 && oyuntahtasi[8] == 0) {o_idx = 8;}
        if(oyuntahtasi[0] == 2 && oyuntahtasi[4] == 0 && oyuntahtasi[8] == 2) {o_idx = 4;}
        if(oyuntahtasi[0] == 0 && oyuntahtasi[4] == 2 && oyuntahtasi[8] == 2) {o_idx = 0;}

        if(oyuntahtasi[2] == 2 && oyuntahtasi[4] == 2 && oyuntahtasi[6] == 0) {o_idx = 6;}
        if(oyuntahtasi[2] == 2 && oyuntahtasi[4] == 0 && oyuntahtasi[6] == 2) {o_idx = 4;}
        if(oyuntahtasi[2] == 0 && oyuntahtasi[4] == 2 && oyuntahtasi[6] == 2) {o_idx = 2;}

        oyuntahtasi[o_idx] = 2;
        inputBoard[o_idx * 3 + 2] = 1; inputBoard[o_idx * 3] = 0;
        if((oyuntahtasi[0] == 2 && oyuntahtasi[3] == 2 && oyuntahtasi[6] == 2) ||
                (oyuntahtasi[1] == 2 && oyuntahtasi[4] == 2 && oyuntahtasi[7] == 2) ||
                (oyuntahtasi[2] == 2 && oyuntahtasi[5] == 2 && oyuntahtasi[8] == 2) ||
                (oyuntahtasi[0] == 2 && oyuntahtasi[1] == 2 && oyuntahtasi[2] == 2) ||
                (oyuntahtasi[3] == 2 && oyuntahtasi[4] == 2 && oyuntahtasi[5] == 2) ||
                (oyuntahtasi[6] == 2 && oyuntahtasi[7] == 2 && oyuntahtasi[8] == 2) ||
                (oyuntahtasi[0] == 2 && oyuntahtasi[4] == 2 && oyuntahtasi[8] == 2) ||
                (oyuntahtasi[2] == 2 && oyuntahtasi[4] == 2 && oyuntahtasi[6] == 2) ) {
            antiskor[idx]++; return;
        }
        game(idx);
    }
    public static  void calistir() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Komut Girişi: train ya da play");
        String s = sc.next();
        if(s.equals("train")) {
            System.out.println("Kaç Adet");
            int i;
            try {
                i = Integer.parseInt(sc.next());
            }catch(NumberFormatException e) {
                e.printStackTrace();
                i = 0;
            }

            while(i > 0) {
                i--;
                for(int j = 0; j < n_denemeler; j++) {
                    boolean[] atanan = new boolean[9];
                    for(int k = 0; k < 9; k++) {
                        int it = (int)(Math.random() * 9);
                        if(atanan[it]) {k--; continue;}
                        atanan[it] = true;
                        testers[j][k] = it;
                    }
                }
                int dene = n_egitim;
                while(dene > 0) {
                    dene--;

                    System.out.println(i + "," + dene);
                    for(int j = 0; j < n_uyeler; j++) {
                        for(int k = 0; k < n_denemeler; k++) {
                            newGame(j);
                            simdikiTest++;
                        }
                        simdikiTest = 0;
                    }
                    int[] hayattakalanlar = new int[(int)(p_hayattakalma * n_uyeler)];
                    for(int j = 0; j < (int)(p_hayattakalma * n_uyeler); j++) {
                        int idx = 0;
                        for(int k = 1; k < skor.length; k++) {if(antiskor[k] < antiskor[idx]
                                || (antiskor[k] == antiskor[idx] && skor[k] > skor[idx])) {
                            idx = k; }
                        }
                        if(skor[idx] == n_denemeler) {
                            dene = 0;
                        }
                        skor[idx] = 0;
                        hayattakalanlar[j] = idx;
                    }
                    double[][][] w1kopyasi = new double[n_uyeler][27][n_noronlar];
                    double[][][] w2kopyasi = new double[n_uyeler][n_noronlar][9];
                    int idx = 0;
                    for(int j = 0; j < hayattakalanlar.length; j++) {
                        for(int k = 0; k < w1[0].length; k++) {
                            for(int h = 0; h < w1[0][0].length; h++) {
                                w1kopyasi[idx][k][h] = w1[hayattakalanlar[j]][k][h];
                            }
                        }
                        for(int k = 0; k < w2[0].length; k++) {
                            for(int h = 0; h < w2[0][0].length; h++) {
                                w2kopyasi[idx][k][h] = w2[hayattakalanlar[j]][k][h];
                            }
                        }
                        idx++;
                    }
                    int foo = idx;
                    for(idx = foo; idx < n_uyeler - (int)(p_hayattakalma * n_uyeler); idx++) {
                        if(Math.random() > .5) {
                            int thisIdx = (int)(Math.random() * hayattakalanlar.length);
                            w1kopyasi[idx] = w1[hayattakalanlar[thisIdx]];
                            w2kopyasi[idx] = w2[hayattakalanlar[thisIdx]];
                            for(int k = 0; k < w1[0].length; k++) {
                                for(int h = 0; h < w1[0][0].length; h++) {
                                    w1kopyasi[idx][k][h] +=  (var - 2 * var * Math.random());
                                }
                            }
                            for(int k = 0; k < w2[0].length; k++) {
                                for(int h = 0; h < w2[0][0].length; h++) {
                                    w2kopyasi[idx][k][h] += (var - 2 * var * Math.random());
                                }
                            }
                        }else {
                            int p1 = (int)(Math.random() * hayattakalanlar.length), p2 = (int)(Math.random() * hayattakalanlar.length);
                            double favor = Math.random();
                            for(int k = 0; k < w1[0].length; k++) {
                                for(int h = 0; h < w1[0][0].length; h++) {
                                    if(Math.random() > favor) {
                                        w1kopyasi[idx][k][h] = w1[hayattakalanlar[p1]][k][h];
                                    }else {
                                        w1kopyasi[idx][k][h] = w1[hayattakalanlar[p2]][k][h];
                                    }
                                }
                            }
                            for(int k = 0; k < w2[0].length; k++) {
                                for(int h = 0; h < w2[0][0].length; h++) {
                                    if(Math.random() > favor) {
                                        w2kopyasi[idx][k][h] = w2[hayattakalanlar[p1]][k][h];
                                    }else {
                                        w2kopyasi[idx][k][h] = w2[hayattakalanlar[p2]][k][h];
                                    }
                                }
                            }
                        }
                    }
                    w1 = w1kopyasi;
                    w2 = w2kopyasi;
                    skor = new double[n_uyeler];
                    antiskor = new double[n_uyeler];
                }
            }

            calistir();
        }else if(s.equals("play")) {
            System.out.println("idx giriniz");
            int idx;
            try {
                idx = Integer.parseInt(sc.next());
            }catch(NumberFormatException e) {
                e.printStackTrace();
                calistir();
                sc.close();
                return;
            }
            oyuntahtasi = new int[9];
            inputBoard = new int[27];
            for(int j = 0; j < 27; j += 3) {
                inputBoard[j] = 1;
            }
            int state = 0;
            while(state == 0){
                hL = new double[n_noronlar];
                for(int i = 0; i < w1[idx].length; i++) {
                    for(int j = 0; j < w1[idx][i].length; j++) {
                        hL[j] += inputBoard[i] * w1[idx][i][j];
                    }
                }
                ciktilar = new double[9];
                for(int i = 0; i < w2[idx].length; i++) {
                    for(int j = 0; j < w2[idx][i].length; j++) {
                        ciktilar[j] += hL[i] * w2[idx][i][j];
                    }
                }
                double currmin = -1000000000; int curridx = -1;
                for(int i = 0; i < 9; i++) {
                    if(ciktilar[i] > currmin && oyuntahtasi[i] == 0) {
                        currmin = ciktilar[i];
                        curridx = i;
                    }
                }
                oyuntahtasi[curridx] = 1;
                inputBoard[curridx * 3 + 1] = 1; inputBoard[curridx * 3] = 0;
                boolean empty = false;
                for(int i = 0; i < oyuntahtasi.length; i++) {
                    if(oyuntahtasi[i] == 0) {empty = true; break;}
                }

                if((oyuntahtasi[0] == 1 && oyuntahtasi[3] == 1 && oyuntahtasi[6] == 1) ||
                        (oyuntahtasi[1] == 1 && oyuntahtasi[4] == 1 && oyuntahtasi[7] == 1) ||
                        (oyuntahtasi[2] == 1 && oyuntahtasi[5] == 1 && oyuntahtasi[8] == 1) ||
                        (oyuntahtasi[0] == 1 && oyuntahtasi[1] == 1 && oyuntahtasi[2] == 1) ||
                        (oyuntahtasi[3] == 1 && oyuntahtasi[4] == 1 && oyuntahtasi[5] == 1) ||
                        (oyuntahtasi[6] == 1 && oyuntahtasi[7] == 1 && oyuntahtasi[8] == 1) ||
                        (oyuntahtasi[0] == 1 && oyuntahtasi[4] == 1 && oyuntahtasi[8] == 1) ||
                        (oyuntahtasi[2] == 1 && oyuntahtasi[4] == 1 && oyuntahtasi[6] == 1) ) {
                    state = 1; break;
                }
                if(!empty) {
                    state = -1; break;
                }
                for(int i = 0; i < 9; i++) {
                    if(i % 3 == 0) {System.out.println();}
                    if(oyuntahtasi[i] == 0) {
                        System.out.print("E");
                    }else if(oyuntahtasi[i] == 1) {
                        System.out.print("X");
                    }else if(oyuntahtasi[i] == 2) {
                        System.out.print("O");
                    }
                }
                System.out.println();
                int o_hareket = -1;
                while(o_hareket < 0 || o_hareket > 9 || oyuntahtasi[o_hareket] != 0 ){
                    System.out.println("nereye (0-8 arasında bir sayı giriniz)?");
                    String stringdeger = sc.next();
                    try {
                        o_hareket = Integer.parseInt(stringdeger);
                    }catch (NumberFormatException e) {
                        System.out.println("0 ile 8 arasında bir sayı giriniz[0-8]");
                        o_hareket = -5;
                    }
                    if(o_hareket < 0 || o_hareket >= 9 ||  oyuntahtasi[o_hareket] != 0) {
                        System.out.println("Hatalı işlem! Lütfen [0-8] arasında bir sayı girin");
                    }
                }
                oyuntahtasi[o_hareket] = 2;
                inputBoard[o_hareket * 3 + 2] = 1; inputBoard[o_hareket * 3] = 0;
                if((oyuntahtasi[0] == 2 && oyuntahtasi[3] == 2 && oyuntahtasi[6] == 2) ||
                        (oyuntahtasi[1] == 2 && oyuntahtasi[4] == 2 && oyuntahtasi[7] == 2) ||
                        (oyuntahtasi[2] == 2 && oyuntahtasi[5] == 2 && oyuntahtasi[8] == 2) ||
                        (oyuntahtasi[0] == 2 && oyuntahtasi[1] == 2 && oyuntahtasi[2] == 2) ||
                        (oyuntahtasi[3] == 2 && oyuntahtasi[4] == 2 && oyuntahtasi[5] == 2) ||
                        (oyuntahtasi[6] == 2 && oyuntahtasi[7] == 2 && oyuntahtasi[8] == 2) ||
                        (oyuntahtasi[0] == 2 && oyuntahtasi[4] == 2 && oyuntahtasi[8] == 2) ||
                        (oyuntahtasi[2] == 2 && oyuntahtasi[4] == 2 && oyuntahtasi[6] == 2) ) {
                    state = 2;
                }
            }
            for(int i = 0; i < 9; i++) {
                if(i % 3 == 0) {System.out.println();}
                if(oyuntahtasi[i] == 0) {
                    System.out.print("E");
                }else if(oyuntahtasi[i] == 1) {
                    System.out.print("X");
                }else if(oyuntahtasi[i] == 2) {
                    System.out.print("O");
                }
            }
            System.out.println();
            switch(state) {
                case 0:System.out.println("Hata?"); break;
                case 1:System.out.println("X Kazandı!"); break;
                case 2:System.out.println("Kazandın Tebrikler!"); break;
                case -1:System.out.println("Berabere!"); break;
            }
            calistir();
        }else if(!s.equals("exit")) {
            System.out.println("Bilinmeyen Komut Girişi Yapıldı!");
            calistir();
        }
        sc.close();
    }
}