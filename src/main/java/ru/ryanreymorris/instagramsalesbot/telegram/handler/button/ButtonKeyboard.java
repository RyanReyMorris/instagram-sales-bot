package ru.ryanreymorris.instagramsalesbot.telegram.handler.button;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Inline keyboard button builder for outgoing message.
 */
public class ButtonKeyboard {
    /**
     * Buttons for every row. Key = row number, value = map of buttons (callbackData<-->button name).
     */
    private final Map<Integer, Map<String, String>> mapOfMessageButtons = new TreeMap<>();

    /**
     * Menu buttons for every row. Key = row number, value = list of buttons name.
     */
    private final Map<Integer, List<String>> mapOfMenuButtons = new TreeMap<>();

    /**
     * Reply keyboard for every row. Key = row number, value = list of buttons name.
     */
    private final Map<Integer, List<KeyboardButton>> mapOfReplyButtons = new TreeMap<>();

    /**
     * Add button to certain row.
     *
     * @param row          - row number.
     * @param callBackData - button data.
     * @param buttonName   - button name.
     */
    public void addMessageButton(Integer row, String callBackData, String buttonName) {
        Map<String, String> oneRowButtons = new HashMap<>();
        oneRowButtons.put(callBackData, buttonName);
        if (mapOfMessageButtons.get(row) == null) {
            mapOfMessageButtons.put(row, oneRowButtons);
        } else {
            Map<String, String> rowOfButtons = mapOfMessageButtons.get(row);
            rowOfButtons.put(callBackData, buttonName);
            mapOfMessageButtons.put(row, rowOfButtons);
        }
    }

    /**
     * Get object InlineKeyboardMarkup from button map.
     *
     * @return InlineKeyboardMarkup.
     */
    public InlineKeyboardMarkup getMessageButtons() {
        if (mapOfMessageButtons.isEmpty()) {
            return new InlineKeyboardMarkup();
        }
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        for (Integer row : mapOfMessageButtons.keySet()) {
            List<InlineKeyboardButton> keyboardButtonsRow = getOneLineOfButtons(mapOfMessageButtons.get(row));
            rowList.add(keyboardButtonsRow);
        }
        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    /**
     * Add menu button to certain row.
     *
     * @param row        - row number.
     * @param buttonName - name of button.
     */
    public void addMenuButton(Integer row, String buttonName) {
        List<String> oneRowButtons = new ArrayList<>();
        oneRowButtons.add(buttonName);
        if (mapOfMenuButtons.get(row) == null) {
            mapOfMenuButtons.put(row, oneRowButtons);
        } else {
            List<String> rowOfButtons = mapOfMenuButtons.get(row);
            rowOfButtons.add(buttonName);
            mapOfMenuButtons.put(row, rowOfButtons);
        }
    }

    /**
     * Get menu buttons ReplyKeyboardMarkup from map.
     *
     * @return ReplyKeyboardMarkup.
     */
    public ReplyKeyboardMarkup getMenuButtons() {
        if (mapOfMenuButtons.isEmpty()) {
            return new ReplyKeyboardMarkup();
        }
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (Integer row : mapOfMenuButtons.keySet()) {
            KeyboardRow keyboardButtons = getOneLineOfMenuButtons(mapOfMenuButtons.get(row));
            keyboardRows.add(keyboardButtons);
        }
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    /**
     * Add reply button to certain row.
     *
     * @param row    - row number.
     * @param button - button.
     */
    public void addReplyButton(Integer row, KeyboardButton button) {
        List<KeyboardButton> oneRowButtons = new ArrayList<>();
        oneRowButtons.add(button);
        if (mapOfReplyButtons.get(row) == null) {
            mapOfReplyButtons.put(row, oneRowButtons);
        } else {
            List<KeyboardButton> rowOfButtons = mapOfReplyButtons.get(row);
            rowOfButtons.add(button);
            mapOfReplyButtons.put(row, rowOfButtons);
        }
    }

    /**
     * Get reply button keyboard.
     *
     * @return ReplyKeyboardMarkup.
     */
    public ReplyKeyboardMarkup getReplyButtons() {
        if (mapOfReplyButtons.isEmpty()) {
            return new ReplyKeyboardMarkup();
        }
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (Integer row : mapOfReplyButtons.keySet()) {
            KeyboardRow keyboardRow = new KeyboardRow();
            keyboardRow.addAll(mapOfReplyButtons.get(row));
            keyboardRows.add(keyboardRow);
        }
        keyboardMarkup.setKeyboard(keyboardRows);
        keyboardMarkup.setResizeKeyboard(true);
        return keyboardMarkup;
    }

    /**
     * Create one line of menu buttons.
     *
     * @param listOfButtons - list of button names for one row.
     * @return - list of button for one row.
     */
    private KeyboardRow getOneLineOfMenuButtons(List<String> listOfButtons) {
        KeyboardRow keyboardButtons = new KeyboardRow();
        for (String button : listOfButtons) {
            keyboardButtons.add(button);
        }
        return keyboardButtons;
    }

    /**
     * Create one line of message buttons.
     *
     * @param mapOfButtons - key - callbackData, value - button name.
     * @return - list of buttons for one row.
     */
    private List<InlineKeyboardButton> getOneLineOfButtons(Map<String, String> mapOfButtons) {
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        for (String buttonCallbackData : mapOfButtons.keySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(mapOfButtons.get(buttonCallbackData));
            button.setCallbackData(buttonCallbackData);
            keyboardButtonsRow.add(button);
        }
        return keyboardButtonsRow;
    }
}
