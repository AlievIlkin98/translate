package com.example.demo.config;

import com.example.demo.buttonService.Translate;
import com.vdurmont.emoji.EmojiParser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.*;

@Slf4j
public class TelegramBot extends TelegramLongPollingBot {
    @Autowired
    private BankBot bankBot;
    @Autowired
    Translate translate = new Translate();
    Currency currency ;
    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            switch (message){
                case "/start":
                    execute(SendMessage.builder()
                            .chatId(update.getMessage().getChatId())
                            .text("Choose you type")
                            .replyMarkup(inlineKeyboardMarkup())
                            .build());
                    break;
            }
        } else if (update.hasCallbackQuery()) {
            clickOnKeyboard(update);
        }
    }

    public void clickOnKeyboard(Update update){
        long messageId = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        switch (update.getCallbackQuery().getData()){
            case "ENGLISH_BUTTON":
                try {
                    execute(EditMessageText
                            .builder()
                            .chatId(chatId)
                            .messageId((int)messageId)
                            .text(translate.translate("en","Привет"))
                            .build());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "RUSSIAN_BUTTON":
                try {
                    execute(EditMessageText
                            .builder()
                            .chatId(chatId)
                            .messageId((int)messageId)
                            .text("Write the text")
                            .build());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;
        }
    }

    public ReplyKeyboardMarkup replyKeyboardMarkup(){
       return ReplyKeyboardMarkup
               .builder()
               .selective(true)
               .build();
    }
    public InlineKeyboardMarkup inlineKeyboardMarkup(){
        List<List<InlineKeyboardButton>> list = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        var key1 = new InlineKeyboardButton();
        key1.setText(EmojiParser.parseToUnicode("English :us:"));
        key1.setCallbackData("ENGLISH_BUTTON");
        inlineKeyboardButtons.add(key1);
        var key2 = new InlineKeyboardButton();
        key2.setText(EmojiParser.parseToUnicode("Russian :ru:"));
        key2.setCallbackData("RUSSIAN_BUTTON");
        inlineKeyboardButtons.add(key2);
        list.add(inlineKeyboardButtons);
        return InlineKeyboardMarkup
                .builder()
                .keyboard(list)
                .build();
    }

    @Override
    public String getBotUsername() {
        return "BetmanAliBot";
    }

    @Override
    public String getBotToken() {
        return "5567583682:AAH122Z2zzWSSzX3kj8TQjDfEEyhpnwJV7s";
    }

    @SneakyThrows
    public static void main(String[] args) {
        TelegramBot telegramBot = new TelegramBot();
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            log.error("Error TelegramApiException");
        }
    }
}
