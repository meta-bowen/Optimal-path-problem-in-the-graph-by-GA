

import java.util.Random;
import java.util.Vector;

public class Chromosome implements Cloneable {

  
  private int[] tour;//路径
  private double[][] distance;
  private int cityNum;
  private double fitness;//适应度
  
  public Chromosome(){
    cityNum = 30;
    tour = new int[cityNum];
    distance = new double[cityNum][cityNum];
  }
  
  public Chromosome(int num, double[][] distance2){
    this.cityNum = num;
    tour = new int[cityNum];//tour数组初始大小
    this.distance = distance2;
    
  }
  
  public void randomGeneration(){
    Vector<Integer> allowedCities = new Vector<Integer>();//构造一个空向量，使其内部数据数组的大小为10，其标准容量增量为零。注意allowedCities是数组
    for (int i = 0; i < cityNum; i++) {
      allowedCities.add(Integer.valueOf(i));//每次循环增加一个表示i的整数实例。
    }    
    Random r = new Random(System.currentTimeMillis());
    for (int i = 0; i < cityNum; i++) {
      
      int index = r.nextInt(allowedCities.size());//每次循环生成不同的index，范围在allowedCities.size()内，即数组值范围内
      int selectedCity = allowedCities.get(index).intValue();
      tour[i] = selectedCity;
      allowedCities.remove(index);
    }
    
  }
  
  public void print(){
    for (int i = 0; i < cityNum; i++) {
      System.out.print(tour[i] + ",");
    }
    System.out.println();
    System.out.println("Its fitness measure is: "+ getFitness());
//    return "Its fitness measure is: "+ getFitness();
//    return tour;
  }
  
  private double calculatefitness(){
    /*for (int i = 0; i < cityNum; i++) {
      for (int j = 0; j < cityNum; j++) {
        System.out.print(distance[i][j]+"\t");
      }
      System.out.println();
    }*/
    double fitness = 0.0;
    double len = 0;
    for (int i = 0; i < cityNum - 1; i++) {
      len += distance[this.tour[i]][this.tour[i+1]]; 
    }
    len += distance[0][tour[cityNum-1]];
    fitness = 1.0/len;
    return fitness;//方法输出值为无穷大，有问题需要解决
  }
  
  public int[] getTour() {//返回类型为数组
    return tour;
  }

  public void setTour(int[] tour) {
    this.tour = tour;
  }

  public double[][] getDistance() {//返回类型为二维数组
    return distance;
  }

  public void setDistance(double[][] distance) {
    this.distance = distance;
  }

  public int getCityNum() {
    return cityNum;
  }

  public void setCityNum(int cityNum) {
    this.cityNum = cityNum;
  }

  public double getFitness() {
    this.fitness = calculatefitness();
    return fitness;
  }

  public void setFitness(double fitness) {
    this.fitness = fitness;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {//返回值类型为类，类对象是类层次结构的根。每个类都有对象作为超类。所有对象，包括数组，都实现了该类的方法。
    Chromosome chromosome = (Chromosome) super.clone();//这个实例的克隆
    chromosome.cityNum = this.cityNum;
    chromosome.distance = this.distance.clone();
    chromosome.tour = this.tour.clone();
    chromosome.fitness = this.fitness;
    return chromosome;
  }
}





