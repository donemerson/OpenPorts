
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

/**
 *
 * @author Emerson Murilo
 */
public class OpenPorts {
    private static String ipText = "";

    public static void main(String[] args) {
        final String regex = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
        if (args.length > 0 && args[0].matches(regex)) {// Se foi especificado um ip valido
            ipText = args[0];
        } else {// IP da propria máquina
            try {
                ipText = getIp();
            } catch (final Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(0);
            }
        }
        System.out.println("Lista de portas abertas: ");
        new OpenPorts().CheckAllPorts();
    }

    private void CheckAllPorts() {
        for (int i = 1; i < 65535; i++) { // testando em portas portas
            new Thread(new CheckPortRunnable(ipText, i)).start();
        }
    }

    public static String getIp() throws Exception {
        BufferedReader in = null;
        String myip = null;
        try {
            final URL whatismyip = new URL("http://checkip.amazonaws.com");
            in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            myip = in.readLine();
            return myip;
        } catch (final Exception ex) {
            throw new Exception("ERRO: ao tentar obter o seu IP, verifique sua conexão com a Internet");
        } finally {
            try {
                in.close();
            } catch (final Exception ex) {
            }
        }
    }

    private class CheckPortRunnable implements Runnable {

        String ip;
        int port;

        public CheckPortRunnable(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void run() {
            String str = ip + ";" + port;

            if (portaEstaAberta(ip, port, 200)) {
                str += ";OPEN";
            }else{
                str += ";CLOSED";
            }
            System.out.println(str);
        }

        public boolean portaEstaAberta(String ip, int porta, int timeout) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(ip, porta), timeout);
                socket.close();
                return true;
            } catch (Exception ex) {
                return false;
            }
        }

    }
}
