public class main {

    public static void main(String[] args) {

        Hello hello=new Hello();
        hello.setName("김현용");
        hello.setPrinter(new ConsolePrinter());
        hello.print();

    }
}
