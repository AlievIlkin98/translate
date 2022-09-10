package com.example.demo.buttonService;

import com.darkprograms.speech.translator.GoogleTranslate;
import org.springframework.stereotype.Component;

import javax.xml.transform.Result;
import java.io.IOException;
import java.util.List;

@Component
public class Translate {
    public static void main(String[] args) {

    }
    public String translate(String code, String message){
        String translate;
        try {
             translate = GoogleTranslate.translate(code,message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return translate;
    }
}
