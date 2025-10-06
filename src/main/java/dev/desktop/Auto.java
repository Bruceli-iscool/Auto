package dev.desktop;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import java.io.File;

import java.io.FileNotFoundException;
import java.util.*;

public class Auto {
  private static String content = "";
  private static ArrayList<String> tokens = new ArrayList<>();
  public static void main(String[] args) {
    read();
  }
  private static void read() {
    Scanner s = new Scanner(System.in);
    String path = s.nextLine();
    try {
      File file = new File(path);
      Scanner g = new Scanner(file);
      while (g.hasNextLine()) {
        String line = g.nextLine();
        content += line + "\n";
      }
      g.close();
    } catch (FileNotFoundException e) {
        System.out.println("Error: The file '" + path + "' was not found.");
        e.printStackTrace();
    }
    tokens = lex(content);
    interpret(tokens);
  }
  public static ArrayList<String> lex(String line) {
      ArrayList<String> result = new ArrayList<String>();
      String z = "";
      boolean ifString = false;
      String p = line;
      for (int i = 0; i < p.length(); i++) {
          char c = p.charAt(i);
          if (c == '-' && !ifString && z.isEmpty() && i + 1 < p.length() && Character.isDigit(p.charAt(i + 1))) {
              z += c;
              continue;
          }
          switch (c) {
              case '(': case ')': case ';': case '=': case '+': case '-': case '*': case '/':case '{': case '}':case ':':case '%':
                  if (!z.isEmpty()) {
                      result.add(z);
                      z = "";
                  }
                  result.add(String.valueOf(c));
                  break;
              case '"':
                  if (!z.isEmpty() && ifString) {
                      ifString = false;
                      result.add(z);
                      z = "";
                  } else {
                      ifString = true;
                  }
                    result.add("\"");
                    break;
              case ' ':
                    if (!z.isEmpty() && !ifString) {
                        result.add(z);
                        z = "";
                    } else if (ifString) {
                        z += c;
                    }
                    break;
                default:
                    z += c;
                    break;
            }
        }
        if (!z.isEmpty()) {
            result.add(z);
        }
        return result;
    }
    private static void interpret(ArrayList<String> t) {
      Client client = new Client();
      while (!t.isEmpty()) {
        String current = get(tokens);
        tokens= remove(tokens);
        if (current.matches("generate")) {
          current = get(tokens);
          tokens = remove(tokens);
          if (current.matches("\"")) {
            current = get(tokens);
            tokens = remove(tokens);
            String prompt = current;
            if (current.matches("\"")) {
              GenerateContentResponse response =
                  client.models.generateContent(
                      "gemini-2.5-flash",
                      prompt,
                      null);
              System.out.println(response.text());
            } 
          }
        }
      }
    }
    protected static String get(ArrayList<String>tokens) {
        if (tokens.isEmpty()) {
            return "";
        } else {
            return tokens.get(0);
        }
    }
    protected static ArrayList<String> remove(ArrayList<String> tokens) {
        if (tokens.isEmpty()) {
            return tokens;
        } else {
            tokens.remove(0);
            return tokens;
        }
        
    }
}
