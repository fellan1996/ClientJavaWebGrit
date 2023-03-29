import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;

public class Main {
    static String val;

    public static void main(String[] args) {
        System.out.println("Client är nu redo");

        //Init stuff. Set as null to be initialized as "something"
        Socket socket = null;
        InputStreamReader inputSR = null;
        OutputStreamWriter outputSW = null;
        BufferedReader bReader = null;
        BufferedWriter bWriter = null;

        //Starta Klienten
        try {
            //Init Socket med specifik port
            socket = new Socket("localhost", 4321);

            //Initiera Reader och Writer och koppla dem till socket
            inputSR = new InputStreamReader(socket.getInputStream());
            outputSW = new OutputStreamWriter(socket.getOutputStream());
            bReader = new BufferedReader(inputSR);
            bWriter = new BufferedWriter(outputSW);

            while (true) {
                //Anroppar meny för användare, låter dem göra ett val.
                //Valet returneras som ett färdigt JSON string
                String message = userInput();
                //Skicka meddelande till server
                bWriter.write(message);
                bWriter.newLine();
                bWriter.flush();

                //Hämta respnse från server
                String resp = bReader.readLine();

                //Anropa openResponse metod med server response
                openResponse(resp);
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                //Stäng kopplingar
                if (socket != null) socket.close();
                if (inputSR != null) inputSR.close();
                if (outputSW != null) outputSW.close();
                if (bWriter != null) bWriter.close();
                if (bReader != null) bReader.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println("Client Avslutas");
        }
    }

    static String userInput() {
        //Steg 1. Skriv ut en meny för användaren
        System.out.println("1. Visa allas namn");
        System.out.println("2. Visa allas namn och ålder");
        System.out.println("3. Visa allas namn och favoritfärg");

        //Steg 2. Låta användaren göra ett val
        Scanner scan = new Scanner(System.in);
        System.out.print("Skriv in ditt menyval: ");

        val = scan.nextLine();

        //Skapa JSON objekt för att hämta data om alla personer. Stringifiera objekete och returnera det
        JSONObject jsonReturn = new JSONObject();
        jsonReturn.put("httpURL", "persons");
        jsonReturn.put("httpMethod", "get");
        //Returnera JSON objekt
        //return jsonReturn.toJSONString();
        //Steg 3. Bearbeta användarens val
        return jsonReturn.toJSONString();
    }

    static void openResponse(String resp) throws org.json.simple.parser.ParseException {
        //Init Parser för att parsa till JSON Objekt
        JSONParser parser = new JSONParser();
        //Skapar ett JSON objekt från server respons
        JSONObject serverResponse = (JSONObject) parser.parse(resp);

        //Kollar om respons lyckas
        if ("200".equals(serverResponse.get("httpStatusCode").toString())) {
            //Bygger upp ett JSONObjekt av den returnerade datan
            JSONObject data = (JSONObject) parser.parse((String) serverResponse.get("data"));

            //Hämtar en lista av alla nycklar attribut i data och loopar sedan igenom dem
            Set<String> keys = data.keySet();
            for (String x : keys) {
                //Hämtar varje person object som finns i data
                JSONObject person = (JSONObject) data.get(x);
                switch (val) {
                    case "1":
                        System.out.println(person.get("name"));
                        break;
                    case "2":
                        System.out.println(person.get("name") + ", " + person.get("age") + " år");
                        break;
                    case "3":
                        System.out.println(person.get("name") + ", favoritfärg: " + person.get("favoriteColor"));
                }
            }
            System.out.println();
        }
    }
}