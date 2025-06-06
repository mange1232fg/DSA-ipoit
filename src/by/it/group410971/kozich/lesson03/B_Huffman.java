package by.it.group410971.kozich.lesson03;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Lesson 3. B_Huffman.
// Восстановите строку по её коду и беспрефиксному коду символов.

public class B_Huffman {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream inputStream = B_Huffman.class.getResourceAsStream("dataB.txt");
        B_Huffman instance = new B_Huffman();
        String result = instance.decode(inputStream);
        System.out.println(result);
    }

    String decode(InputStream inputStream) throws FileNotFoundException {
        StringBuilder result = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);
        Integer count = scanner.nextInt();
        Integer length = scanner.nextInt();

        // Считываем коды символов в Map: код -> символ
        Map<String, Character> codeToChar = new HashMap<>();
        for (int i = 0; i < count; i++) {
            String line = scanner.next();
            // Формат: "a:" и "0" например
            char ch = line.charAt(0);
            String code = scanner.next();
            codeToChar.put(code, ch);
        }

        // Считываем закодированную строку
        String encoded = scanner.next();

        // Декодируем строку, двигаясь по символам
        StringBuilder tempCode = new StringBuilder();
        for (int i = 0; i < encoded.length(); i++) {
            tempCode.append(encoded.charAt(i));
            // Если текущий код есть в словаре, добавляем символ в результат
            if (codeToChar.containsKey(tempCode.toString())) {
                result.append(codeToChar.get(tempCode.toString()));
                tempCode.setLength(0); // сбрасываем временный код
            }
        }

        return result.toString();
    }
}
