import org.academiadecodigo.enuminatti.mafiagame.client.utils.InputOutput;

public class TestIO {
    public static void main(String[] args) {
        TestIO io = new TestIO();

        io.testWrite();
        io.testRead();
    }

    private void testRead(){
        System.out.println(InputOutput.readNicks());
        System.out.println(InputOutput.readHosts());

    }

    private void testWrite(){
        InputOutput.addHost("127.0.0.1", "MyNewLocalhost");
        InputOutput.addHost("127.0.0.1", "anotherlocalhost");
        InputOutput.addHost("127.0.0.1", "anotherlocalhost");
        //InputOutput.editHost("127.0.0.1", 6668, "edited host");
        //InputOutput.addNick("Sarc");
        //InputOutput.addNick("Sarc1");
        //InputOutput.addHost("127.0.0.1",6667,"home", "savedfiles/presistenseTest.txt");
        //InputOutput.addNick("Sarc1","ss", "savedfiles/presistenseTest.txt");
        //InputOutput.addHost("127.0.0.10",6667,"home", "savedfiles/presistenseTest.txt");
        //InputOutput.addNick("newSarc", "savedfiles/presistenseTest.txt");
    }
}
