

import java.io.BufferedReader;
import java.io.File;
import java.math.BigDecimal;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class GA {

	  private Chromosome[] chromosomes;
	  private Chromosome[] nextGeneration;
	  private int N;
	  private int cityNum;
	  private double p_c_t;
	  private double p_m_t;
	  private int MAX_GEN;
	  private double bestLength;
	  private int[] bestTour;
	  private double bestFitness;
	  private double[] averageFitness;
	  private double[][] distance;
	  private String filename;
	  
	  public GA(){
	    N = 100;
	    cityNum = 30;
	    p_c_t = 0.9;
	    p_m_t = 0.1;
	    MAX_GEN = 1000;
	    bestLength = 0;
	    bestTour = new int [cityNum];
	    bestFitness = 0.0;
	    averageFitness = new double[MAX_GEN];
	    chromosomes = new Chromosome[N];
	    distance = new double[cityNum][cityNum];
	    
	  }
	  
	  /**
	   * Constructor of GA class
	   * @param n 种群规模
	   * @param num 城市规模，相当于个体的染色体
	   * @param g 运行代数
	   * @param p_c 交叉率
	   * @param p_m 变异率
	   * @param filename 数据文件名
	   */
	  public GA(int n, int num, int g, double p_c, double p_m, String filename){
	    this.N = n;
	    this.cityNum = num;
	    this.MAX_GEN = g;
	    this.p_c_t = p_c;
	    this.p_m_t = p_m;
	    bestTour = new int [cityNum];
	    averageFitness = new double[MAX_GEN];//平均适应值
	    bestFitness = 0.0;
	    chromosomes = new Chromosome[N];
	    nextGeneration = new Chromosome[N];
	    distance = new double[cityNum][cityNum];
	    this.filename = filename;
	  }
	  
	  public void solve() throws IOException{
	    System.out.println("---------------------Start initilization(开始初始化)-----------------");
	    String []a=init();
	    System.out.println("---------------------End initilization(初始化结束)-------------------");//初始化完成
	    System.out.println("---------------------Start evolution(开始进化)----------------------");//开始进化
	    File filename =new File("D:\\软件工程\\JAVA\\JAVA程序\\其他文件\\进化列表.txt");
	    FileWriter list=new FileWriter(filename);
	    for (int i = 0; i < MAX_GEN; i++) {//进化次数
	    	list.write("\r\nStart generation(开始进化代数):"+(i+1));
//	      System.out.println("-----------Start generation "+ i+"----------");
	    	evolve(i,list);
	      list.write("\r\nEnd generation(进化总代数):"+(i+1)+"\r\n\r\n\r\n");
//	      System.out.println("-----------End generation "+ i+"----------");
	    }
	    System.out.println("---------------------End evolution(进化结束)------------------------");
	    printOptimal(a);
	    outputResults(a);
	    
	  }
	  /**
	   * 初始化GA
	   * @throws IOException
	   */
	  @SuppressWarnings("resource")
	  private String [] init() throws IOException{
	    //读取数据文件
		  	String[] a;
		  	double[] x;  
	        double[] y;  
	        String strbuff;  
	        BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));  
	        distance = new double[cityNum][cityNum];  
	        a = new String[cityNum];
	        x = new double[cityNum];  
	        y = new double[cityNum];  
	        for (int i = 0; i < cityNum; i++) {  
	            strbuff = data.readLine(); 
	            String[] strcol = strbuff.split(",");
	            a[i]=strcol[0];
	            x[i] = Double.valueOf(strcol[1]).doubleValue();  //纬度
	            y[i] = Double.valueOf(strcol[2]).doubleValue();  //经度
	        }  
	        //计算距离矩阵 ，针对具体问题，距离计算方法也不一样 
	        double R=6371.004;
	        for (int i = 0; i < cityNum - 1; i++) {  
	            distance[i][i] = 0;  //对角线为0
	            for (int j = i + 1; j < cityNum; j++) {  
	              //double rij = Math.sqrt((x[i] - x[j]) * (x[i] - x[j])+ (y[i] - y[j]) * (y[i] - y[j]));
	            	
	            	double C = add(mul_1(y[i],y[j],x[i],x[j]),mul(y[i],y[j]));
	            	double tij=mul_2(R,Math.acos(C),Math.PI,(double)1/180);
//	            	double tij=(R*Math.acos(C)*Math.PI)/180;
//	              int tij = (int) Math.round(rij);//四舍五入
	              //if (tij < rij) {
	              distance[i][j] = tij;  
	              distance[j][i] = distance[i][j];  
	        /*}else {
	          distance[i][j] = tij;  
	                    distance[j][i] = distance[i][j]; 
	        }*/
	            }  
	        }  
	        distance[cityNum - 1][cityNum - 1] = 0;  
	    
	       
	        
	    for (int i = 0; i < N; i++) {
	      Chromosome chromosome = new Chromosome(cityNum, distance);//染色体大小以及距离矩阵
	      chromosome.randomGeneration();
	      chromosomes[i] = chromosome;
//	      chromosome.print();
	    }
	    return a;
	  }
	  
	  
	  
/*JAVA浮点数精确运算包*/	  
	  public static double mul(double v1,double v2){
	        BigDecimal b1 = new BigDecimal(Double.toString(Math.cos(90-v1)));
	        BigDecimal b2 = new BigDecimal(Double.toString(Math.cos(90-v2)));
	        return b1.multiply(b2).doubleValue();
	    }
	  public static double mul_1(double v1,double v2,double v3,double v4){
	        BigDecimal b1 = new BigDecimal(Double.toString(Math.sin(90-v1)));
	        BigDecimal b2 = new BigDecimal(Double.toString(Math.sin(90-v2)));
	        BigDecimal b5 = new BigDecimal(Double.toString(Math.cos(sub(v3,v4))));
	        return (b1.multiply(b2)).multiply(b5).doubleValue();
	    }
	    
	    public static double mul_2(double v1,double v2,double v3,double v4){
	        BigDecimal b1 = new BigDecimal(Double.toString(v1));
	        BigDecimal b2 = new BigDecimal(Double.toString(v2));
	        BigDecimal b3 = new BigDecimal(Double.toString(v3));
	        BigDecimal b4 = new BigDecimal(Double.toString(v4));
	        
	        return ((b1.multiply(b2)).multiply(b3)).multiply(b4).doubleValue();
	    }
	  public static double sub(double v1,double v2){
	        BigDecimal b1 = new BigDecimal(Double.toString(v1));
	        BigDecimal b2 = new BigDecimal(Double.toString(v2));
	        return b1.subtract(b2).doubleValue();
	    } 
	  public static double add(double v1,double v2){
	        BigDecimal b1 = new BigDecimal(Double.toString(v1));
	        BigDecimal b2 = new BigDecimal(Double.toString(v2));
	        return b1.add(b2).doubleValue();
	    }
	    public static double div(double v1,double v2,int scale){
	        if(scale<0){
	            throw new IllegalArgumentException(
	                "The scale must be a positive integer or zero");
	        }
	        BigDecimal b1 = new BigDecimal(Double.toString(v1));
	        BigDecimal b2 = new BigDecimal(Double.toString(v2));
	        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	    }
	    
	    
	  private void evolve(int g,FileWriter list){
		  try{
			  	double[] selectionP = new double[N];//每个个体的选择概率数组
			    double sum = 0.0;
			    double tmp = 0.0;
			    
			    for (int i = 0; i < N; i++) {
			      sum += chromosomes[i].getFitness();//所有个体适应度相加
			      if (chromosomes[i].getFitness() > bestFitness) {
			        bestFitness = chromosomes[i].getFitness();//在遍历过程中记录最优适应度个体（一个个体含有一条染色体，染色体内有cityNum个基因，并且具有一定的顺序，映射到实际中就是：每个个体是含有一种路径的数组）
			        //System.out.println(bestFitness);
			        bestLength = (1.0/bestFitness); //记录最优适应度个体的最优步长
			        for (int j = 0; j < cityNum; j++) {
			          bestTour[j] = chromosomes[i].getTour()[j];//将该最优适应度个体的路径顺序保存到bestTour[]数组中
			        }
			        
			      }
			    }
			    averageFitness[g] = sum/N;//第g次进化的平均适应度
			    list.write("\r\nThe average fitness in "+g+ " generation is: "+averageFitness[g]+ "\r\nand the best fitness is: "+bestFitness);
//			    System.out.println("The average fitness in "+g+ " generation is: "+averageFitness[g]+ ", and the best fitness is: "+bestFitness);
			    for (int i = 0; i < N; i++) {
			      tmp += chromosomes[i].getFitness()/sum;//将种群内的每个个体的适应度累加，再除以总的适应度
			      selectionP[i] = tmp;//选择的概率进行累加，为后面实现轮盘法
			    }
			    Random random = new Random(System.currentTimeMillis());
			    for (int i = 0; i < N; i = i+2) {//每次选择两个染色体，并产生了两个子代
			      
			      Chromosome[] children = new Chromosome[2];
			      list.write("\r\n---------start selection(轮盘法选择两个个体)-----------");
			      //轮盘赌选择两个染色体
			      //System.out.println("---------start selection-----------");
			      //System.out.println();
			      for (int j = 0; j < 2; j++) {
			        
			        int selectedChromosomes=0;//轮盘选择出的染色体
			        double p = random.nextDouble();//随机概率值，该随机值为均匀选取。故概率越大（适应度越高）的个体被选中的概率也会越大
			        for (int k = 0; k < N - 1; k++) {
			          
			          if (p > selectionP[k] && p <= selectionP[k+1]) {
			        	  //该处进行了修改：原程序为selsctCity=k;	        	  
			        	  selectedChromosomes = k+1;
			          }
			          if (k==0 &&p <= selectionP[k]) {
			        	  selectedChromosomes = 0;//selectCity从0值开始
			          }
			        }
			        try {
			          children[j] = (Chromosome) chromosomes[selectedChromosomes].clone();//轮盘法选择出两个孩子继承被选择到的两个父代，而选择父代根据适应度随机抽取，抽分布概率均匀，故而适应度大的父代被选中的可能性也更大
			          //list.write(children[j].print());
			          //children[j].print();
			          //System.out.println();
			        } catch (CloneNotSupportedException e) {
			          // TODO Auto-generated catch block
			          e.printStackTrace();
			        }
			      }
			      
			      //交叉操作(OX1)
			      list.write("\r\n----------Start crossover(交叉操作)----------");
			      //System.out.println("----------Start crossover(开始交叉)----------");
			      //System.out.println();
			      //Random random = new Random(System.currentTimeMillis());
			      if (random.nextDouble() < p_c_t) {//交叉率
			        //System.out.println("crossover");
			        //random = new Random(System.currentTimeMillis());
			        //定义两个cut点
			        int cutPoint1 = -1;
			        int cutPoint2 = -1;
			        int r1 = random.nextInt(cityNum);
			        if (r1 > 0 && r1 < cityNum -1) {
			          cutPoint1 = r1;//随机选择一个染色体
			          //random = new Random(System.currentTimeMillis());
			          int r2 = random.nextInt(cityNum - r1);//选择另一个染色体
			          if (r2 == 0) {
			            cutPoint2 = r1 + 1;
			          }else if(r2 > 0){
			            cutPoint2 = r1 + r2;
			          }
			          
			        }
			        if (cutPoint1 > 0 && cutPoint2 > 0) {
			        	list.write("\r\n开始交叉:\r\nCut point1 is: "+cutPoint1 +", and Cut point2 is "+cutPoint2);//打印所选择的染色体
			          //System.out.println("Cut point1 is: "+cutPoint1 +", and cut point2 "+cutPoint2);
			          int [] tour1 = new int[cityNum];
			          int [] tour2 = new int[cityNum];
			          if (cutPoint2 == cityNum - 1) {//第二个是最后一个染色体
			            for (int j = 0; j < cityNum; j++) {
			              tour1[j] = children[0].getTour()[j];
			              tour2[j] = children[1].getTour()[j];//两个子代基因一致
			            }
			          }else {
			            
			            //int n = 1;
			            for (int j = 0; j < cityNum; j++) {
			              if (j < cutPoint1) {
			                tour1[j] = children[0].getTour()[j];
			                tour2[j] = children[1].getTour()[j];
			              }else if (j >= cutPoint1 && j < cutPoint1+cityNum-cutPoint2-1) {
			                tour1[j] = children[0].getTour()[j+cutPoint2-cutPoint1+1];
			                tour2[j] = children[1].getTour()[j+cutPoint2-cutPoint1+1];
			              }else {
			                tour1[j] = children[0].getTour()[j-cityNum+cutPoint2+1];
			                tour2[j] = children[1].getTour()[j-cityNum+cutPoint2+1];
			              }
			              
			            }
			          }
			          //list.write("\r\n进行交叉，打印当前两个子代的基因(路径): ");
			          /*System.out.println("The two tours are: ");
			          for (int j = 0; j < cityNum; j++) {
			            System.out.print(tour1[j] +"\t");
			          }
			          System.out.println();
			          for (int j = 0; j < cityNum; j++) {
			            System.out.print(tour2[j] +"\t");
			          }
			          System.out.println();*/
			          //String str1=Arrays.toString(tour1);
			          //String str2=Arrays.toString(tour2);
			          //list.write("\r\n"+str1+"\r\n"+str2);
			          
			          for (int j = 0; j < cityNum; j++) {
			            if (j < cutPoint1 || j > cutPoint2) {
			              
			              children[0].getTour()[j] = -1;
			              children[1].getTour()[j] = -1;
			            }else {
			              int tmp1 = children[0].getTour()[j];
			              children[0].getTour()[j] = children[1].getTour()[j];
			              children[1].getTour()[j] = tmp1;//将两个子代相同位置染色体对调
			            }
			          }
			          /*for (int j = 0; j < cityNum; j++) {
			            System.out.print(children[0].getTour()[j]+"\t");
			          }
			          System.out.println();
			          for (int j = 0; j < cityNum; j++) {
			            System.out.print(children[1].getTour()[j]+"\t");
			          }
			          System.out.println();*/
			          if (cutPoint2 == cityNum - 1) {
			            int position = 0;
			            for (int j = 0; j < cutPoint1; j++) {
			              for (int m = position; m < cityNum; m++) {
			                boolean flag = true;
			                for (int n = 0; n < cityNum; n++) {
			                  if (tour1[m] == children[0].getTour()[n]) {
			                    flag = false;
			                    break;
			                  }
			                }
			                if (flag) {
			                  
			                  children[0].getTour()[j] = tour1[m];
			                  position = m + 1;
			                  break;
			                }
			              }
			            }
			            position = 0;
			            for (int j = 0; j < cutPoint1; j++) {
			              for (int m = position; m < cityNum; m++) {
			                boolean flag = true;
			                for (int n = 0; n < cityNum; n++) {
			                  if (tour2[m] == children[1].getTour()[n]) {
			                    flag = false;
			                    break;
			                  }
			                }
			                if (flag) {
			                  children[1].getTour()[j] = tour2[m];
			                  position = m + 1;
			                  break;
			                }
			              }
			            }
			            
			          }else {
			            
			            int position = 0;
			            for (int j = cutPoint2 + 1; j < cityNum; j++) {
			              for (int m = position; m < cityNum; m++) {
			                boolean flag = true;
			                for (int n = 0; n < cityNum; n++) {
			                  if (tour1[m] == children[0].getTour()[n]) {
			                    flag = false;
			                    break;
			                  }
			                }
			                if (flag) {
			                  children[0].getTour()[j] = tour1[m];
			                  position = m+1;
			                  break;
			                }
			              }
			            }
			            for (int j = 0; j < cutPoint1; j++) {
			              for (int m = position; m < cityNum; m++) {
			                boolean flag = true;
			                for (int n = 0; n < cityNum; n++) {
			                  if (tour1[m] == children[0].getTour()[n]) {
			                    flag = false;
			                    break;
			                  }
			                }
			                if (flag) {
			                  children[0].getTour()[j] = tour1[m];
			                  position = m+1;
			                  break;
			                }
			              }
			            }
			            
			            
			            position = 0;
			            for (int j = cutPoint2 + 1; j < cityNum; j++) {
			              for (int m = position; m < cityNum; m++) {
			                boolean flag = true;
			                for (int n = 0; n < cityNum; n++) {
			                  if (tour2[m] == children[1].getTour()[n]) {
			                    flag = false;
			                    break;
			                  }
			                }
			                if (flag) {
			                  children[1].getTour()[j] = tour2[m];
			                  position = m+1;
			                  break;
			                }
			              }
			            }
			            for (int j = 0; j < cutPoint1; j++) {
			              for (int m = position; m < cityNum; m++) {
			                boolean flag = true;
			                for (int n = 0; n < cityNum; n++) {
			                  if (tour2[m] == children[1].getTour()[n]) {
			                    flag = false;
			                    break;
			                  }
			                }
			                if (flag) {
			                  children[1].getTour()[j] = tour2[m];
			                  position = m+1;
			                  break;
			                }
			              }
			            }
			          }
			          String str3=Arrays.toString(children[0].getTour());
				      String str4=Arrays.toString(children[1].getTour());
				      list.write("\r\n打印当前两个个体的基因(路径):"+"\r\n"+str3+"\r\n"+str4);
			          
			          
			        }
			      }
			      
			      //children[0].print();
			      //children[1].print();
			      
			      
			      //变异操作(DM)
			      list.write("\r\n---------Start mutation(变异操作)------");
			      //System.out.println("---------Start mutation(开始变异)------");
			      //System.out.println();
			      //random = new Random(System.currentTimeMillis());
			      if (random.nextDouble() < p_m_t) {
			        //System.out.println("mutation");
			        for (int j = 0; j < 2; j++) {
			          //random = new Random(System.currentTimeMillis());
			          //定义两个cut点
			          int cutPoint1 = -1;
			          int cutPoint2 = -1;
			          int r1 = random.nextInt(cityNum);
			          if (r1 > 0 && r1 < cityNum -1) {
			            cutPoint1 = r1;
			            //random = new Random(System.currentTimeMillis());
			            int r2 = random.nextInt(cityNum - r1);
			            if (r2 == 0) {
			              cutPoint2 = r1 + 1;
			            }else if(r2 > 0){
			              cutPoint2 = r1 + r2;
			            }
			            
			          }
			          
			          
			          if (cutPoint1 > 0 && cutPoint2 > 0) {
			        	List<Integer> tour = new ArrayList<Integer>();
			        	list.write("\r\n开始变异:\r\nCut point1 is "+cutPoint1+", and Cut point2 is " +cutPoint2);
			            //System.out.println("Cut point1 is "+cutPoint1+", and cut point2 +cutPoint2);
			            if (cutPoint2 == cityNum - 1) {
			              for (int k = 0; k < cutPoint1; k++) {
			                tour.add(Integer.valueOf(children[j].getTour()[k]));
			              }
			            }
			            else {
			              for (int k = 0; k < cityNum; k++) {
			                if (k < cutPoint1 || k > cutPoint2) {
			                  tour.add(Integer.valueOf(children[j].getTour()[k]));
			                }
			              }
			            }
			            //random = new Random(System.currentTimeMillis());
			            int position = random.nextInt(tour.size());
			            
			            if (position == 0) {
			              
			              for (int k = cutPoint2; k >= cutPoint1; k--) {
			                tour.add(0, Integer.valueOf(children[j].getTour()[k]));
			              }
			              
			            }
			            else if (position == tour.size()-1) {
			              
			              for (int k = cutPoint1; k <= cutPoint2; k++) {
			                tour.add(Integer.valueOf(children[j].getTour()[k]));
			              }
			              
			            } 
			            else {
			              
			              for (int k = cutPoint1; k <= cutPoint2; k++) {
			                tour.add(position, Integer.valueOf(children[j].getTour()[k]));
			              }
			              
			            }
			            
			            
			            for (int k = 0; k < cityNum; k++) {
			              children[j].getTour()[k] = tour.get(k).intValue();
			              
			            }
			            //System.out.println();
			            String str5=Arrays.toString(children[0].getTour());
					    String str6=Arrays.toString(children[1].getTour());
					    list.write("\r\n打印当前两个个体的基因(路径):"+"\r\n"+str5+"\r\n"+str6);
			          }
			          
			          
			        }
			      }
			      
			      
			      //children[0].print();
			      //children[1].print();
			      
			      
			      nextGeneration[i] = children[0];
			      nextGeneration[i+1] = children[1];
			      
			    }
			    
			    for (int k = 0; k < N; k++) {
			      try {
			        chromosomes[k] = (Chromosome) nextGeneration[k].clone();//子代替换父代
			        
			      } catch (CloneNotSupportedException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			      }
			    }
			    list.write("\r\nNext generation is:");
			    
			    //System.out.println("Next generation is:");
			    for (int k = 0; k < N; k++) {
			    	String str7=Arrays.toString(chromosomes[k].getTour());
			    	list.write("\r\n"+str7);
			      //chromosomes[k].print();
			    }
		  }
		  catch(IOException e){
			  
		  }
	   
	  }
	  
	  private void printOptimal(String [] a){
		System.out.println("The point number(选点数量):"+GA.this.cityNum);
		System.out.println("The generation's times(迭代次数): "+GA.this.MAX_GEN);
	    System.out.println("The best fitness is(最优适应度): " + bestFitness);
	    System.out.println("The best tour length is(最优路径长度): " + bestLength+" (千米)");
	    System.out.println("The best tour is(最优路径序列): ");
	    System.out.println("\n");
	    for (int i = 0; i < cityNum; i++) {
	    	System.out.print(bestTour[i] +"(" +a[bestTour[i]]+")"+",");
	    }
	    String str=Arrays.toString(bestTour);
	    String str2=Arrays.toString(a);
	    System.out.println("\n\n");
	    System.out.println("(PS:图形界面示例请转至Python)");
	    	    
	  }
	  
	  private void outputResults(String [] a){
		  String str=Arrays.toString(bestTour);
		  String str2=Arrays.toString(a);
		    System.out.println("\n\n");
		    String filename="D:\\软件工程\\JAVA\\JAVA程序\\其他文件\\最优路径结果.txt";
		    try{
		    	FileWriter output=new FileWriter(filename);
		    	output.write("                 《三亚自然景观最优路径结果》---万波文");
		    	output.write("\r\nThe point number(选点数量):"+GA.this.cityNum);//注意：在该环境下只有\r\n才能实现换行操作
		    	output.write("\r\nThe generation's times(迭代次数): "+GA.this.MAX_GEN);
		    	output.write("\r\nThe best tour length is(最优路径长度): " + bestLength+" (千米)");
		    	output.write("\r\nThe best tour is(最优路径序列): ");
		    	output.write("\r\n"+str);
		    	output.write("\r\n"+str2);
		    	output.write("\r\n\r\n(图形界面示例请转至Python)");
		    	output.close();
		    }
		    catch(IOException e){
		    	
		    }
	  }
	  public Chromosome[] getChromosomes() {
	    return chromosomes;
	  }
	  public void setChromosomes(Chromosome[] chromosomes) {
	    this.chromosomes = chromosomes;
	  }
	  public int getCityNum() {
	    return cityNum;
	  }
	  public void setCityNum(int cityNum) {
	    this.cityNum = cityNum;
	  }
	  public double getP_c_t() {
	    return p_c_t;
	  }
	  public void setP_c_t(double p_c_t) {
	    this.p_c_t = p_c_t;
	  }
	  public double getP_m_t() {
	    return p_m_t;
	  }
	  public void setP_m_t(double p_m_t) {
	    this.p_m_t = p_m_t;
	  }
	  public int getMAX_GEN() {
	    return MAX_GEN;
	  }
	  public void setMAX_GEN(int mAX_GEN) {
	    MAX_GEN = mAX_GEN;
	  }
	  public double getBestLength() {
	    return bestLength;
	  }
	  public void setBestLength(int bestLength) {
	    this.bestLength = bestLength;
	  }
	  public int[] getBestTour() {
	    return bestTour;
	  }
	  public void setBestTour(int[] bestTour) {
	    this.bestTour = bestTour;
	  }
	  public double[] getAverageFitness() {
	    return averageFitness;
	  }
	  public void setAverageFitness(double[] averageFitness) {
	    this.averageFitness = averageFitness;
	  }
	  public int getN() {
	    return N;
	  }


	  public void setN(int n) {
	    N = n;
	  }


	  public double[][] getDistance() {
	    return distance;
	  }

	  public void setDistance(double[][] distance) {
	    this.distance = distance;
	  }

	  /**
	   * @param args
	   * @throws IOException 
	   */
	  public static void main(String[] args) throws IOException {
		           //种群数量，染色体大小，迭代次数，交叉率，变异率
	    GA ga = new GA(50, 20, 10000, 0.7, 0.005, "D:\\软件工程\\JAVA\\JAVA程序\\其他文件\\三亚自然景观经纬坐标.txt");
	    ga.solve();
	  }
	  

	}
