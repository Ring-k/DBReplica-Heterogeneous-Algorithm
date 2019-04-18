public class TestLoop {
  public static void main(String args[]) throws InterruptedException {
      for(int i = 0; i < 1000000000000000000L; i++){
        System.out.println(i);
        Thread.sleep(500);
      }
  }
}
